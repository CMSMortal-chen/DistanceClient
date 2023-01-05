package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MovementUtils;
import my.distance.api.events.World.*;

public class HytBhopSpeed extends SpeedModule {

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
        if (!MovementUtils.isMoving())
            return;
        if (mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = false;
            mc.thePlayer.jump();
        }
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance <= 0.1) {
            mc.thePlayer.speedInAir = 0.02f;
            mc.timer.timerSpeed = 1.5f;
        }
        if (mc.thePlayer.fallDistance > 0.1 && mc.thePlayer.fallDistance < 1.3) {
            mc.timer.timerSpeed = 0.7f;
        }
        if (mc.thePlayer.fallDistance >= 1.3) {
            mc.timer.timerSpeed = 1;
            mc.thePlayer.speedInAir = 0.02f;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {
    }
}
