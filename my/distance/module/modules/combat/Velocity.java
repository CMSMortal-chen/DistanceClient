package my.distance.module.modules.combat;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventPacket;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.EntityUtils;
import my.distance.util.time.MSTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

public class Velocity extends Module {
    private final Mode mode;
    private final Numbers<Double> vertical;
    private final Numbers<Double> horizontal;


    /**
     * VALUES
     */
    private final MSTimer velocityTimer = new MSTimer();
    private final MSTimer timer = new MSTimer();
    private boolean velocityInput = false;

    private int templateX = 0;
    private int templateY = 0;
    private int templateZ = 0;

    public Velocity() {
        super("Velocity", new String[]{"AntiKB"}, ModuleType.Combat);
        mode = new Mode("Velocity", modes.values(), modes.Simple);
        this.vertical = new Numbers<>("Vertical", 0.0, 0.0, 1.0, 0.01);
        this.horizontal = new Numbers<>("Horizontal", 0.0, 0.0, 1.0, 0.01);
        this.addValues(mode, vertical, horizontal);
    }

    boolean needSimple = false;

    @EventHandler
    public void onEvent(EventPreUpdate event) {
        switch ((modes) mode.getValue()) {
            case Huayuting:
                if (isArrowNearby() && isEnemyNearby()) {
                    timer.reset();
                }
                break;
            case AAC: {
                if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                    mc.thePlayer.motionX *= horizontal.getValue();
                    mc.thePlayer.motionZ *= horizontal.getValue();
                    velocityInput = false;
                }
                break;
            }
            case AAC1:
                if (mc.thePlayer.hurtTime == 1 || mc.thePlayer.hurtTime == 2 || mc.thePlayer.hurtTime == 3 || mc.thePlayer.hurtTime == 4 || mc.thePlayer.hurtTime == 5 || mc.thePlayer.hurtTime == 6 || mc.thePlayer.hurtTime == 7 || mc.thePlayer.hurtTime == 8) {
                    if (mc.thePlayer.onGround) {
                        break;
                    }
                    double yaw = mc.thePlayer.rotationYawHead;
                    yaw = Math.toRadians(yaw);
                    double dX = (-MathHelper.sin(yaw)) * 0.08;
                    double dZ = MathHelper.cos((float) yaw) * 0.08;
                    if (mc.thePlayer.getHealth() >= 6.0f) {
                        mc.thePlayer.motionX = dX;
                        mc.thePlayer.motionZ = dZ;
                    }
                }
                break;
            case AAC5Reduce: {
                if (mc.thePlayer.hurtTime > 1 && velocityInput) {
                    mc.thePlayer.motionX *= 0.81;
                    mc.thePlayer.motionZ *= 0.81;
                }
                if (velocityInput && (mc.thePlayer.hurtTime < 5 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false;
                }
                break;
            }
            case AAC5Combat: {
                if (mc.thePlayer.hurtTime > 0 && velocityInput) {
                    velocityInput = false;
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.jumpMovementFactor = -0.002f;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                }
                if (velocityTimer.hasTimePassed(80L) && velocityInput) {
                    velocityInput = false;
                    mc.thePlayer.motionX = templateX / 8000.0;
                    mc.thePlayer.motionZ = templateZ / 8000.0;
                    mc.thePlayer.motionY = templateY / 8000.0;
                    mc.thePlayer.jumpMovementFactor = -0.002f;
                }
                break;
            }

        }
    }


    @EventHandler
    public void onEvent(EventPacket event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                velocityInput = true;
                velocityTimer.reset();
            }
        }
        if (event.getPacket() instanceof S27PacketExplosion) {
            velocityInput = true;
            velocityTimer.reset();
        }
        switch ((modes) mode.getValue()) {
            case Huayuting: {
                if (event.packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (!(timer.hasTimePassed(1000L)) || KillAura.currentTarget != null) {
                        if (this.vertical.getValue() == 0.0D && this.horizontal.getValue() == 0.0D) {
                            event.setCancelled(true);
                        } else {
                            packet.motionX = (int) (packet.motionX * this.horizontal.getValue());
                            packet.motionZ = (int) (packet.motionZ * this.horizontal.getValue());
                            packet.motionY = (int) (packet.motionY * this.vertical.getValue());
                        }
                    }
                }
                break;
            }
            case AAC5Combat: {
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    event.setCancelled(true);
                    templateX = packet.motionX;
                    templateZ = packet.motionZ;
                    templateY = packet.motionY;
                }
                break;
            }
            case Simple: {
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        if (this.vertical.getValue() == 0.0D && this.horizontal.getValue() == 0.0D) {
                            event.setCancelled(true);
                        } else {
                            packet.motionX = (int) (packet.motionX * this.horizontal.getValue());
                            packet.motionZ = (int) (packet.motionZ * this.horizontal.getValue());
                            packet.motionY = (int) (packet.motionY * this.vertical.getValue());
                        }
                    }
                }


                if (event.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
                    if (this.vertical.getValue() == 0.0D && this.horizontal.getValue() == 0.0D) {
                        event.setCancelled(true);
                    } else {
                        packet.field_149152_f = (float) (packet.field_149152_f * this.horizontal.getValue());
                        packet.field_149153_g = (float) (packet.field_149153_g * this.horizontal.getValue());
                        packet.field_149159_h = (float) (packet.field_149159_h * this.vertical.getValue());
                    }
                }
                break;
            }
            case AAC520: {
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
                    if (mc.thePlayer == null || mc.theWorld.getEntityByID(packet.getEntityID()) != mc.thePlayer) {
                        return;
                    }
                    event.setCancelled(true);
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                }
                break;
            }
        }
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        this.setSuffix(mode.getValue());
    }

    enum modes {
        Simple,
        AAC,
        AAC1,
        AAC520,
        AAC5Reduce,
        AAC5Combat,
        Huayuting
    }

    private boolean isArrowNearby() {
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityArrow) {
                if (((EntityArrow) entity).shootingEntity != mc.thePlayer && !((EntityArrow) entity).inGround) {
                    return (entity.getDistanceToEntity(mc.thePlayer) < 10);
                }
            }
        }
        return false;
    }
    private boolean isEnemyNearby() {
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (EntityUtils.isSelected(entity, true) && ModuleManager.getModuleByClass(KillAura.class).isEnabled()) {
                return (entity.getDistanceToEntity(mc.thePlayer) < ((KillAura) ModuleManager.getModuleByClass(KillAura.class)).rangeValue.getValue() + 1.5);
            }
        }
        return false;
    }
}


