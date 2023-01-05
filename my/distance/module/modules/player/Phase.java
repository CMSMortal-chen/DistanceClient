package my.distance.module.modules.player;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventCollideWithBlock;
import my.distance.api.events.Misc.EventPushBlock;
import my.distance.api.events.Render.EventRenderBlock;
import my.distance.api.events.World.EventMotionUpdate;
import my.distance.api.events.World.EventMove;
import my.distance.api.events.World.EventPacketReceive;
import my.distance.api.events.World.EventTick;
import my.distance.api.value.Mode;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.MovementUtils;
import my.distance.util.entity.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;


public final class Phase
        extends Module {
    private final Mode mode = new Mode("Mode",modes.values(),modes.Hypixel);
    private int moveUnder;

    public Phase() {
        super("Phase",new String[]{"Phase"}, ModuleType.Player);
        addValues(mode);
    }
    public void onEnabled() {
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (mc.thePlayer != null && this.moveUnder == 1) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2.0, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
            this.moveUnder = 0;
        }
        if (mc.thePlayer != null && this.moveUnder == 1488) {
            double mx = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
            double mz = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
            double x = (double)mc.thePlayer.movementInput.moveForward * mx + (double)mc.thePlayer.movementInput.moveStrafe * mz;
            double z = (double)mc.thePlayer.movementInput.moveForward * mz - (double)mc.thePlayer.movementInput.moveStrafe * mx;
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, mc.thePlayer.posY, Double.NEGATIVE_INFINITY, true));
            this.moveUnder = 0;
        }
    }

    @EventHandler
    public void onBoundingBox(EventCollideWithBlock event) {
        switch ((modes)((Object)this.mode.getValue())) {
            case Hypixel: {
                if (!PlayerUtil.isInsideBlock()) break;
                event.setBoundingBox(null);
                break;
            }
        }
    }

    @EventHandler
    public void onPush(EventPushBlock event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockRender(EventRenderBlock event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMotionUpdate(EventMotionUpdate event) {
        switch ((modes)((Object)this.mode.getValue())) {
            case Hypixel: {
                if (event.isPre()) break;
                double multiplier = 0.3;
                double mx = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
                double mz = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
                double x = (double)mc.thePlayer.movementInput.moveForward * multiplier * mx + (double)mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
                double z = (double)mc.thePlayer.movementInput.moveForward * multiplier * mz - (double)mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
                if (!mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isOnLadder()) break;
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + 0.001, mc.thePlayer.posZ + z, false));
                for (int i = 1; i < 10; ++i) {
                    mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.22, mc.thePlayer.posZ, false));
                }
                mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                break;
            }
        }
    }

    @EventHandler
    public void onReceive(EventPacketReceive event) {
        S02PacketChat packet;
        if (event.getPacket() instanceof S02PacketChat && (packet = (S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText().contains("You cannot go past the border.")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(EventMove event) {
        switch ((modes)((Object)this.mode.getValue())) {
            case Hypixel: {
                if (!PlayerUtil.isInsideBlock()) break;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    event.y = mc.thePlayer.motionY += (double)0.09f;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    event.y = mc.thePlayer.motionY -= 0.0;
                } else {
                    mc.thePlayer.motionY = 0.0;
                    event.y = 0.0;
                }
                MovementUtils.setSpeed(event, 0.3);
                if (mc.thePlayer.ticksExisted % 2 != 0) break;
                event.y = mc.thePlayer.motionY += (double)0.09f;
                break;
            }
        }
    }

    enum modes {
        Hypixel
    }
}

