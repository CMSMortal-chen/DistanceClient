package my.distance.module.modules.move.speedmode.speed;

import my.distance.api.events.World.*;
import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MovementUtils;

public class VulcanFastHopSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

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
        mc.timer.timerSpeed = 1.00f;
        if (Math.abs(mc.thePlayer.movementInput.moveStrafe) < 0.1f) {
            mc.thePlayer.jumpMovementFactor = 0.0265f;
        }else {
            mc.thePlayer.jumpMovementFactor = 0.0244f;
        }
        if (!mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = mc.gameSettings.keyBindJump.isKeyDown();
        }
        if (MovementUtils.getSpeed() < 0.215f) {
            MovementUtils.strafe(0.215f);
        }
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.timer.timerSpeed = 1.25f;
            mc.gameSettings.keyBindJump.pressed = false;
            mc.thePlayer.jump();
            MovementUtils.strafe();
            if(MovementUtils.getSpeed() < 0.5f) {
                MovementUtils.strafe(0.4849f);
            }
        }else if (!MovementUtils.isMoving()) {
            mc.timer.timerSpeed = 1.00f;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
