package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.api.events.World.*;

public class AAC440Speed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mc.timer.timerSpeed = 1.6105f;
                mc.thePlayer.motionX *= 1.0708;
                mc.thePlayer.motionZ *= 1.0708;
            } else if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 1) {
                mc.timer.timerSpeed = 0.6f;
            }
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
