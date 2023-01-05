package my.distance.module.modules.move.flymode.fly;

import my.distance.manager.ModuleManager;
import my.distance.module.modules.combat.TargetStrafe;
import my.distance.module.modules.move.Fly;
import my.distance.module.modules.move.flymode.FlyModule;
import my.distance.util.entity.MovementUtils;
import my.distance.util.entity.PlayerUtil;
import my.distance.util.time.StopWatchs;
import my.distance.api.events.World.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Random;

public class HypixelZoomFly implements FlyModule {
    int level;
    double lastDist;
    private int counter;
    public static boolean shouldSlow;
    double moveSpeed;
    private double y;
    boolean fly;
    boolean damaged = false;
    private final StopWatchs stopwatch = new StopWatchs();
    public static TargetStrafe targetStrafe = (TargetStrafe) ModuleManager.getModuleByClass(TargetStrafe.class);

    @Override
    public void onEnabled() {
        damaged = false;
        if (targetStrafe == null) {
            targetStrafe = (TargetStrafe) ModuleManager.getModByClass(TargetStrafe.class);
        }
        shouldSlow = mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.onGround || PlayerUtil.isOnLiquid() || PlayerUtil.isInLiquid() || mc.thePlayer.isOnLadder();
        damage(Fly.hypZoom_uhc.getValue());
        this.stopwatch.reset();
        this.y = 0.0;
        this.lastDist = 0.0;
        this.level = 1;
        this.counter = 0;
        mc.thePlayer.stepHeight = 0.0f;
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
    }
    @Override
    public void onDisable() {
        final EntityPlayerSP player = mc.thePlayer;
        mc.timer.timerSpeed = 1.0f;
        player.stepHeight = 0.625f;
        player.motionX = 0.0;
        player.motionZ = 0.0;
        player.setPosition(player.posX, player.posY + this.y, player.posZ);
    }

    @Override
    public void onMove(EventMove e) {
        if (mc.thePlayer.hurtTime != 0 && !damaged){
            damaged = true;
        }
        if (!MovementUtils.isMoving() || !damaged) {
            e.setX(0);
            e.setZ(0);
            return;
        }

        switch (this.level) {
            case 1: {
                this.moveSpeed = 1.777734 * MovementUtils.getBaseMoveSpeed();
                this.level = 2;
                break;
            }
            case 2: {
                this.moveSpeed *= 2.2772;
                this.level = 3;
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - ((mc.thePlayer.ticksExisted % 2 == 0) ? 0.0093 : 0.0143) * (this.lastDist - MovementUtils.getBaseMoveSpeed());
                this.level = 4;
                break;
            }
            default: {
                this.moveSpeed = this.lastDist - this.lastDist / 159;
                break;
            }
        }


        this.moveSpeed = Math.max(this.moveSpeed, 0.3);


        MovementUtils.setSpeed(e, moveSpeed);
    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        ++counter;
        mc.thePlayer.motionY = 0;
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {
        mc.thePlayer.motionY = 0.0;
        if (Fly.hypZoom_Multiplier.getValue()) {
            mc.timer.timerSpeed = (float) (stopwatch.elapsed((Fly.hypZoom_MultiplyTime.getValue()).longValue()) ? 1.0 : (Fly.hypZoom_MultiplySpeed.getValue()));
        }
        if (this.level <= 2) {
            return;
        }
        final double posX = mc.thePlayer.posX;
        final double n2 = mc.thePlayer.posY - 0.002;
        mc.thePlayer.setPosition(posX, n2, mc.thePlayer.posZ);
        ++this.counter;
        switch (counter) {
            case 1: {
                this.y *= -0.94666665455465f;
                break;
            }
            case 2 | 3 | 4: {
                this.y += 1.45E-3f;
                break;
            }
            case 5: {
                this.y += 1.0E-3f;
                counter = 0;
                break;
            }
        }
        e.setPosY(mc.thePlayer.posY + this.y);
        if (this.level <= 2) {
            return;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY = 0.4;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY = -0.4;
        }
        final double posX2 = mc.thePlayer.posX;
        final double n3 = mc.thePlayer.posY + 0.0;
        mc.thePlayer.setPosition(posX2, n3, mc.thePlayer.posZ);
    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {
        final double posX = mc.thePlayer.posX;
        final double xDist = posX - mc.thePlayer.prevPosX;
        final double posZ = mc.thePlayer.posZ;
        final double zDist = posZ - mc.thePlayer.prevPosZ;
        this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {
        e.setStepHeight(0.0f);
    }

    public void damage(boolean UHC) {
        if (mc.thePlayer.onGround && !PlayerUtil.isOnLiquid() && !PlayerUtil.isInLiquid() && !mc.thePlayer.isOnLadder()) {
            for (int i = 0; i < 9; i++)
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            for (double fallDistance = (UHC ? 4.0125 : 3.0125); fallDistance > 0; fallDistance -= 0.0624986421) {
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false));
            }
            mc.thePlayer.jump();
            mc.thePlayer.posY += 0.42 + (new Random()).nextDouble() / 4000;
            mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        } else {
            shouldSlow = true;
        }
    }
}
