package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MoveUtils;
import my.distance.api.events.World.*;

public class OnGroundSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        mc.timer.timerSpeed = 1.085f;
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        if ((forward != 0 || strafe != 0) && !mc.thePlayer.isJumping && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && (!mc.thePlayer.isCollidedHorizontally)) {

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, 0.4d, 0.0D)).isEmpty()) {
                e.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.2 : 0));
            } else {
                e.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 != 0 ? 0.4198 : 0));
            }

        }
        movementSpeed = Math.max(mc.thePlayer.ticksExisted % 2 == 0 ? 2.1 : 1.3, MoveUtils.defaultSpeed());
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = (0.0D);
            mc.thePlayer.motionZ = (0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 0.15;
                } else if (forward < 0.0D) {
                    forward = -0.15;
                }
            }
            if (strafe > 0) {
                strafe = 0.15;
            } else if (strafe < 0) {
                strafe = -0.15;
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionX = (forward * movementSpeed * cos + strafe * movementSpeed * sin);
            mc.thePlayer.motionZ = (forward * movementSpeed * sin - strafe * movementSpeed * cos);
        }
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onPost(EventPostUpdate e) {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }

    @Override
    public void onMotion(EventMotionUpdate e) {
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
