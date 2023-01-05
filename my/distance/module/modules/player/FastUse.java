package my.distance.module.modules.player;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.time.TimerUtil;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastUse extends Module {
    private TimerUtil timer = new TimerUtil();
    public static Mode MODE = new Mode("Mode",Modes.values(),Modes.Packet);
    public static Numbers<Double> TICKS = new Numbers<>("Ticks", "Ticks", 12d, 1d, 20d, 1d);
    public FastUse() {
        super("FastUse",new String[]{"FastEat"}, ModuleType.Player);
        this.addValues(MODE,TICKS);
    }
    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
        mc.timer.timerSpeed = 1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1;
    }

    @EventHandler
    public void onEvent(EventPreUpdate event) {
        setSuffix(MODE.getValue());
        if (MODE.getValue() == Modes.Timer) {
            if (mc.thePlayer.getItemInUseDuration() >= (TICKS.getValue().intValue()) && canUseItem(mc.thePlayer.getItemInUse().getItem())) {
                mc.timer.timerSpeed = 1.3555f;
            } else if (mc.timer.timerSpeed == 1.3555f) {
                mc.timer.timerSpeed = 1;
            }
        }
        if (MODE.getValue() == Modes.Packet) {
            if (mc.thePlayer.getItemInUseDuration() == (TICKS.getValue().intValue()) && canUseItem(mc.thePlayer.getItemInUse().getItem())) {
                for (int i = 0; i < 30; i++) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.thePlayer.stopUsingItem();
            }
        }
        if (MODE.getValue().equals(Modes.Instant) && timer.hasReached(270)) {
            if (mc.thePlayer.isUsingItem()) {
                Item usingItem = mc.thePlayer.getItemInUse().getItem();
                if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.itemInUse));
                    for (int i = 0; i < 30; ++i) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    timer.reset();
                }
            }
        }
        if (!mc.thePlayer.isUsingItem() && MODE.getValue() == Modes.Instant) {
            timer.reset();
        }
    }

    private boolean canUseItem(Item item) {
        boolean result = !((item instanceof ItemSword) || (item instanceof ItemBow));
        return result;
    }

    enum Modes{
        Timer,
        Packet,
        Instant
    }
}

