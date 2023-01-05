package my.distance.module.modules.world;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketReceive;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate
extends Module {
	private final Option Confirm = new Option("Confirm", "Confirm", false);
	private final Option ConfirmIllegalRotation = new Option("ConfirmIllegalRotation", "ConfirmIllegalRotation", false);
	private final Option NoZero = new Option("NoZero", "NoZero", false);
	public NoRotate() {
        super("NoRotate", new String[]{"NoRotateSet"}, ModuleType.Player);
        this.setColor(new Color(17, 250, 154).getRGB());
        super.addValues(Confirm,ConfirmIllegalRotation,NoZero);
    }

    @EventHandler
    private void onPacket(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();

        if(mc.thePlayer == null)return;


        if (packet instanceof S08PacketPlayerPosLook) {
        	S08PacketPlayerPosLook thePacket = (S08PacketPlayerPosLook)packet;
            if (NoZero.getValue() && thePacket.getYaw() == 0F && thePacket.getPitch() == 0F)
                return;

            if (ConfirmIllegalRotation.getValue() || thePacket.getPitch() <= 90 && thePacket.getPitch() >= -90 && thePacket.getYaw() != mc.thePlayer.rotationYaw &&
                    		thePacket.getPitch() != mc.thePlayer.rotationPitch) {

                if (Confirm.getValue())
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(thePacket.getYaw(), thePacket.getPitch(), mc.thePlayer.onGround));
            }

            thePacket.yaw = mc.thePlayer.rotationYaw;
            thePacket.pitch = mc.thePlayer.rotationPitch;
        }
    }
}




