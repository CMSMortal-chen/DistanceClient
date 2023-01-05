package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.PlayerUtil;
import my.distance.api.events.World.*;

public class AutoJumpSpeed extends SpeedModule {

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        if (isMoving2()) {
            if (mc.thePlayer.onGround)
                e.setY(mc.thePlayer.motionY = 0.42f);
            else
                setMotion(e, PlayerUtil.getBaseMoveSpeed());
        } else {
            mc.thePlayer.motionX = 0D;
            mc.thePlayer.motionZ = 0D;
        }
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
