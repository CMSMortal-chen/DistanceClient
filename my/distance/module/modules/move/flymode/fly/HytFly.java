package my.distance.module.modules.move.flymode.fly;

import my.distance.module.modules.move.Fly;
import my.distance.module.modules.move.flymode.FlyModule;
import my.distance.util.entity.MovementUtils;
import my.distance.api.events.World.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class HytFly implements FlyModule {
    private final int aac520Purse = Fly.aac520Purse.getValue();
    private final boolean aac520UseC04 = Fly.aac520UseC04.getValue();

    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    public float vanillaSpeed = Fly.speed.getValue().floatValue();
    private final ArrayList<C03PacketPlayer> aac5C03List=new ArrayList<>();

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisable() {
        sendAAC5Packets();
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY += vanillaSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= vanillaSpeed;
        MovementUtils.strafe(vanillaSpeed);
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {

    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packetPlayer = (C03PacketPlayer) e.getPacket();
            aac5C03List.add(packetPlayer);
            e.setCancelled(true);
            if (aac5C03List.size() > aac520Purse) {
                sendAAC5Packets();
            }
        }
    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {

    }

    private void sendAAC5Packets(){
        float yaw=mc.thePlayer.rotationYaw;
        float pitch=mc.thePlayer.rotationPitch;
        for(C03PacketPlayer packet : aac5C03List){
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
            if(packet.isMoving()){
                if(packet.getRotating()){
                    yaw=packet.yaw;
                    pitch=packet.pitch;
                }
                if(aac520UseC04){
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x,1e+159,packet.z, true));
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x,packet.y,packet.z, true));
                }else{
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x,1e+159,packet.z, yaw, pitch, true));
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.x,packet.y,packet.z, yaw, pitch, true));
                }
            }
        }
        aac5C03List.clear();
    }
}
