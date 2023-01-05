package my.distance.module.modules.move.speedmode.speed;

import my.distance.api.events.World.*;
import my.distance.module.modules.move.Speed;
import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MovementUtils;
import my.distance.util.time.MSTimer;

public class HypixelSpeed extends SpeedModule {
    private boolean stage = false;
    private final MSTimer timer = new MSTimer();

    @Override
    public void onMotion(EventMotionUpdate e) {
        if (MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && mc.thePlayer.jumpTicks == 0) {
            mc.thePlayer.jump();
            mc.thePlayer.jumpTicks = 10;
        }
        if (MovementUtils.isMoving() && !mc.thePlayer.onGround && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
            MovementUtils.strafe();
        }
        if (this.stage) {
            mc.timer.timerSpeed = Speed.mintimerValue.getValue().floatValue();
            if (this.timer.hasTimePassed(Speed.mintimerMisValue.get().longValue())) {
                this.timer.reset();
                this.stage = !this.stage;
            }
        } else {
            mc.timer.timerSpeed = Speed.maxtimerValue.get().floatValue();
            if (this.timer.hasTimePassed(Speed.maxtimerMisValue.get().longValue())) {
                this.timer.reset();
                this.stage = !this.stage;
            }
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onStep(EventStep e) {
    }

    @Override
    public void onPre(EventPreUpdate e) {
    }

    double y;
    @Override
    public void onMove(EventMove event) {
        if (MovementUtils.isMoving() && !mc.thePlayer.isInWeb && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
            double moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed(), MovementUtils.getSpeed());
//            TargetStrafe targetStrafe = (TargetStrafe) ModuleManager.getModuleByClass(TargetStrafe.class);
//            if (!targetStrafe.doStrafeAtSpeed(event,moveSpeed)) {
            MovementUtils.setSpeed(event, moveSpeed);
//            }
            if (MovementUtils.isMoving() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround && Speed.sendJumpValue.get()) {
                mc.thePlayer.jump();
            }
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
}
