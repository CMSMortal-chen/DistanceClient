package my.distance.module.modules.move;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventMove;
import my.distance.api.events.World.EventPacketReceive;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.time.Timer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.RandomUtils;

public class AntiFall extends Module {
	private final Timer timer = new Timer();
	private boolean saveMe;
	private final Mode mode = new Mode("Mode", "Mode", AntiMode.values(), AntiMode.Hypixel);
	public static Numbers<Double> distance = new Numbers<>("Distance", "Distance", 5.0, 0.0, 10.0, 1.0);
	public static Option Void = new Option("Void", "Void", true);

	public AntiFall() {
		super("AntiFall", new String[]{"AntiVoid"}, ModuleType.Movement);
		this.addValues(this.mode, distance, Void);
	}

	enum AntiMode {
		Hypixel, FlyFlag,
	}

	boolean needBlink;

	@EventHandler
	private void onUpdate(EventMove e) {
		NetworkManager networkManager = mc.thePlayer.sendQueue.getNetworkManager();
		if ((saveMe && timer.delay(150)) || mc.thePlayer.isCollidedVertically) {
			saveMe = false;
			needBlink = false;
			timer.reset();
		}
		int dist = distance.getValue().intValue();
		if (mc.thePlayer.fallDistance >= dist && !mc.thePlayer.capabilities.allowFlying) {
			if (!((Boolean) Void.getValue()) || !isBlockUnder()) {
				if (!saveMe) {
					saveMe = true;
					needBlink = true;
					switch ((AntiMode) mode.getValue()) {
						case FlyFlag:
							mc.thePlayer.motionY += 0.1;
							mc.thePlayer.fallDistance = 0F;
							break;
						case Hypixel:
							mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + distance.getValue().intValue() + 3 + RandomUtils.nextDouble(0.07, 0.09), mc.thePlayer.posZ, true));
							break;
					}
					timer.reset();
				}
			}
		}
	}

	@EventHandler
	public void onPacketSend(EventPacketSend e) {
	}

	@EventHandler
	public void onPacketRe(EventPacketReceive ep) {
		final Packet<?> packet = ep.getPacket();
	}

	@EventHandler
	public void onMove(EventMove e) {
	}

	private boolean isBlockUnder() {
		if (mc.thePlayer.posY < 0)
			return false;
		for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
			AxisAlignedBB bb = mc.thePlayer.boundingBox.offset(0, -off, 0);
			if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
