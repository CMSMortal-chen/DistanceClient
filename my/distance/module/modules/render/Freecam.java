package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventCollideWithBlock;
import my.distance.api.events.World.EventMove;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.MovementUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Freecam
        extends Module {
    private EntityOtherPlayerMP copy;
    private double x;
    private double y;
    private double z;

    private final Numbers<Double> speedValue = new Numbers<>("Speed", 0.8d, 0.1d, 2d,0.1d);
    private final Option flyValue = new Option("Fly", true);
    private final Option noClipValue = new Option("NoClip", true);
    public Freecam() {
        super("Freecam", new String[]{"outofbody"}, ModuleType.Render);
        addValues(speedValue,flyValue,noClipValue);
    }

    @Override
    public void onEnable() {
        this.copy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        this.copy.clonePlayer(mc.thePlayer, true);
        this.copy.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        this.copy.rotationYawHead = mc.thePlayer.rotationYawHead;
        this.copy.setEntityId(-1337);
        this.copy.setSneaking(mc.thePlayer.isSneaking());
        mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        this.x = mc.thePlayer.posX;
        this.y = mc.thePlayer.posY;
        this.z = mc.thePlayer.posZ;
        mc.thePlayer.noClip = true;
    }

    @EventHandler
    private void onPreMotion(EventMove e) {
        if (noClipValue.get())
            mc.thePlayer.noClip = true;

        mc.thePlayer.fallDistance = 0.0f;

        if (flyValue.get()) {
            float value = speedValue.get().floatValue();
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;

            e.setY(mc.gameSettings.keyBindJump.isKeyDown() ? value : mc.gameSettings.keyBindSneak.isKeyDown() ? -value : 0);

            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.thePlayer.motionY += value;

            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.thePlayer.motionY -= value;

            MovementUtils.strafe(value);
        }
    }

    @EventHandler
    private void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C0BPacketEntityAction) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBB(EventCollideWithBlock e) {
        e.setBoundingBox(null);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
        mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
        mc.thePlayer.setSneaking(this.copy.isSneaking());
        this.copy = null;
        mc.thePlayer.setPosition(this.x, this.y, this.z);
        mc.thePlayer.noClip = false;
        mc.thePlayer.motionY = 0.0;
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
    }
}

