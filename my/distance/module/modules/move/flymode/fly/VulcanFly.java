package my.distance.module.modules.move.flymode.fly;

import my.distance.api.events.World.*;
import my.distance.module.modules.move.Fly;
import my.distance.module.modules.move.flymode.FlyModule;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class VulcanFly implements FlyModule {
    private boolean waitFlag = false;
    private boolean canGlide = false;
    private int ticks = 0;

    @Override
    public void onEnabled() {
        if(mc.thePlayer.onGround && Fly.vulcan_canClipValue.getValue()) {
            clip(0f, -0.1f);
            waitFlag = true;
            canGlide = false;
            ticks = 0;
            mc.timer.timerSpeed = 0.1f;
        } else {
            waitFlag = false;
            canGlide = true;
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {
        if (e.isPre() && canGlide) {
            mc.timer.timerSpeed = 1f;
            mc.thePlayer.motionY = -(ticks % 2 == 0 ? 0.17 : 0.10);
            if (ticks == 0) {
                mc.thePlayer.motionY = -0.07;
            }
            ticks++;
        }
    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {
        if(e.packet instanceof S08PacketPlayerPosLook && waitFlag) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.packet;
            waitFlag = false;
            mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            e.setCancelled(true);
            mc.thePlayer.jump();
            clip(0.127318f, 0f);
            clip(3.425559f, 3.7f);
            clip(3.14285f, 3.54f);
            clip(2.88522f, 3.4f);
            canGlide = true;
        }
    }

    @Override
    public void onStep(EventStep e) {

    }
    private void clip(float dist, float y) {
        double yaw = Math.toRadians((double) mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * dist;
        double z = Math.cos(yaw) * dist;
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
    }
}
