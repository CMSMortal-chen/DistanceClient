package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MovementUtils;
import my.distance.api.events.World.*;

public class HytTypeBSpeed extends SpeedModule {
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
            mc.thePlayer.jump();
            mc.thePlayer.speedInAir = 0.0201f;
            mc.timer.timerSpeed = 0.94f;
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.thePlayer.speedInAir = 0.02f;
            mc.timer.timerSpeed = 1.8f;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
