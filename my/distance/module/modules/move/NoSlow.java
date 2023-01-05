package my.distance.module.modules.move;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventMotion;
import my.distance.api.events.World.EventMotionUpdate;
import my.distance.api.events.World.EventNoSlow;
import my.distance.api.events.World.EventPacket;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.combat.KillAura;
import my.distance.util.entity.MovementUtils;
import my.distance.util.time.MSTimer;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.CopyOnWriteArrayList;

public class NoSlow extends Module {
	private final MSTimer timer = new MSTimer();
	private final Numbers<Double> blockForwardMultiplier = new Numbers<>("BlockForwardMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Numbers<Double> blockStrafeMultiplier = new Numbers<>("BlockStrafeMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Numbers<Double> consumeForwardMultiplier = new Numbers<>("ConsumeForwardMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Numbers<Double> consumeStrafeMultiplier = new Numbers<>("ConsumeStrafeMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Numbers<Double> bowForwardMultiplier = new Numbers<>("BowForwardMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Numbers<Double> bowStrafeMultiplier = new Numbers<>("BowStrafeMultiplier", 1.0d, 0.2d, 1.0d, 0.01d);
	private final Option customOnGround = new Option("CustomOnGround", false);
	private final Numbers<Double> customDelayValue = new Numbers<>("CustomDelay", 60d, 10d, 200d, 1d);
	private final Mode modeValue = new Mode("PacketMode", NoSlowMode.values(), NoSlowMode.Vanilla);
	// Soulsand
	private final Option soulsandValue = new Option("Soulsand", true);

	private final Option sendPacketValue = new Option("SendPacket", true);


	private boolean nextTemp = false;
	private final MSTimer msTimer = new MSTimer();
	private final CopyOnWriteArrayList<Packet<?>> packetBuf = new CopyOnWriteArrayList<>();
	private boolean lastBlockingStat = false;
	private boolean waitC03 = false;
	private boolean isBlocking() {
		return  (mc.thePlayer.isUsingItem() || ((KillAura) ModuleManager.getModuleByClass(KillAura.class)).blockingStatus) &&
		mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}

	public NoSlow() {
		super("NoSlow", new String[]{"NoSlowDown"}, ModuleType.Movement);
		this.addValues(blockForwardMultiplier, blockStrafeMultiplier, consumeForwardMultiplier, consumeStrafeMultiplier, bowForwardMultiplier, bowStrafeMultiplier, customOnGround, customDelayValue, modeValue, soulsandValue,sendPacketValue);
		setValueDisplayable(sendPacketValue,modeValue,NoSlowMode.Hypixel);
	}

	@Override
	public void onDisable() {
		timer.reset();
		packetBuf.clear();
	}

	@EventHandler
	public void onRender2D(EventRender2D e) {
		setSuffix(modeValue.getValue());
	}

	@EventHandler
	public void onPacket(EventPacket event){
		if (modeValue.getValue().equals(NoSlowMode.Hypixel) && event.getPacket() instanceof S30PacketWindowItems && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
			event.setCancelled(true);
		}
		if(modeValue.getValue().equals(NoSlowMode.Vulcan) && nextTemp) {
			Packet<?> packet = event.packet;
			if((packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) && isBlocking()) {
				event.setCancelled(true);
			}else if (packet instanceof C03PacketPlayer || packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) {
				if (modeValue.getValue().equals(NoSlowMode.Vulcan) && waitC03 && packet instanceof C03PacketPlayer) {
					waitC03 = false;
					return;
				}
				packetBuf.add(packet);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onUpdate(EventMotionUpdate e){
		if (modeValue.getValue().equals(NoSlowMode.Vulcan) && (lastBlockingStat || isBlocking())) {
			if (msTimer.hasTimePassed(230) && nextTemp) {
				nextTemp = false;
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
				if (!packetBuf.isEmpty()) {
					boolean canAttack = false;
					for (Packet<?> packet : packetBuf) {
						if (packet instanceof C03PacketPlayer) {
							canAttack = true;
						}
						if (!((packet instanceof C02PacketUseEntity || packet instanceof C0APacketAnimation) && !canAttack)) {
							mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
						}
					}
					packetBuf.clear();
				}
			}
			if (!nextTemp) {
				lastBlockingStat = isBlocking();
				if (!isBlocking()) {
					return;
				}
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
				nextTemp = true;
				waitC03 = modeValue.getValue().equals(NoSlowMode.Vulcan);
				msTimer.reset();
			}
		}
	}

	@EventHandler
	public void onEventMotion(EventMotion event) {
		if (!MovementUtils.isMoving()) {
			return;
		}
		
		if (modeValue.get() == NoSlowMode.AAC5) {
			if (event.getTypes().equals(EventMotion.Type.POST) && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || ((KillAura)ModuleManager.getModuleByClass(KillAura.class)).getBlockingStatus())) {
				mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
			}
		} else {
			if (!mc.thePlayer.isBlocking() && !((KillAura)ModuleManager.getModuleByClass(KillAura.class)).getBlockingStatus()) {
				return;
			}
			switch ((NoSlowMode) modeValue.get()) {
				case LiquidBounce:
					sendPacket(event, true, true, false, 0, false, false);
					break;
				case AAC: {
					if (mc.thePlayer.ticksExisted % 3 == 0) {
						sendPacket(event, true, false, false, 0, false, false);
					} else if (mc.thePlayer.ticksExisted % 3 == 1) {
						sendPacket(event, false, true, false, 0, false, false);
					}
					break;
				}
				case Custom: {
					sendPacket(event, true, true, true, customDelayValue.get().longValue(), customOnGround.get(), false);
					break;
				}
				case NCP: {
					sendPacket(event, true, true, false, 0, false, false);
					break;
				}
				case Hypixel:{
					if (!this.sendPacketValue.get() || ((KillAura)ModuleManager.getModuleByClass(KillAura.class)).getBlockingStatus()) break;
					if (event.isPre()) {
						mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
						break;
					}
					mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
					break;
				}
			}
		}
	}

	@EventHandler
	public void onSlowDown(EventNoSlow event) {
		Item heldItem = mc.thePlayer.getHeldItem().getItem();

		event.setMoveForward(getMultiplier(heldItem, true));
		event.setMoveStrafe(getMultiplier(heldItem, false));
	}

	private void sendPacket(EventMotion event, boolean sendC07, boolean sendC08, boolean delay, long delayValue, boolean onGround, boolean watchDog) {
		C07PacketPlayerDigging digging = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN);
		C08PacketPlayerBlockPlacement blockPlace = new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem());
		C08PacketPlayerBlockPlacement blockMent = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f);
		if (onGround && !mc.thePlayer.onGround) {
			return;
		}
		if (sendC07 && event.getTypes().equals(EventMotion.Type.PRE)) {
			if (delay && timer.hasTimePassed(delayValue)) {
				mc.getNetHandler().addToSendQueue(digging);
			} else if (!delay) {
				mc.getNetHandler().addToSendQueue(digging);
			}
		}
		if (sendC08 && event.getTypes().equals(EventMotion.Type.POST)) {
			if (delay && timer.hasTimePassed(delayValue) && !watchDog) {
				mc.getNetHandler().addToSendQueue(blockPlace);
				timer.reset();
			} else if (!delay && !watchDog) {
				mc.getNetHandler().addToSendQueue(blockPlace);
			} else if (watchDog) {
				mc.getNetHandler().addToSendQueue(blockMent);
			}
		}
	}

	private float getMultiplier(Item item, boolean isForward) {
		if (item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk) {
			return (isForward) ? this.consumeForwardMultiplier.get().floatValue() : this.consumeStrafeMultiplier.get().floatValue();
		} else if (item instanceof ItemSword) {
			return (isForward) ? this.blockForwardMultiplier.get().floatValue() : this.blockStrafeMultiplier.get().floatValue();

		} else if (item instanceof ItemBow) {
			return (isForward) ? this.bowForwardMultiplier.get().floatValue() : this.bowStrafeMultiplier.get().floatValue();
		} else {
			return 0.2F;
		}
	}


	enum NoSlowMode {
		LiquidBounce, Custom, Hypixel, NCP, AAC, AAC5, Vulcan, Vanilla
	}
}
