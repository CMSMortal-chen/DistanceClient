package my.distance.module.modules.move;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventMotionUpdate;
import my.distance.api.events.World.EventMove;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.MovementUtils;

public class CustomSpeed extends Module {
    public final Numbers<Double> customSpeedValue = new Numbers<>("CustomSpeed", 1.6d, 0.2d, 2d, 0.01d);
    public final Numbers<Double> customYValue = new Numbers<>("CustomY", 0d, 0d, 4d, 0.01d);
    public final Numbers<Double> customTimerValue = new Numbers<>("CustomTimer", 1d, 0.1d, 2d, 0.01d);
    public final Option customStrafeValue = new Option("CustomStrafe", true);
    public final Option resetXZValue = new Option("CustomResetXZ", false);
    public final Option resetYValue = new Option("CustomResetY", false);
    public CustomSpeed() {
        super("CustomSpeed", new String[]{"Cspeed"}, ModuleType.Movement);
        addValues(customSpeedValue,customYValue,customTimerValue,customStrafeValue,resetXZValue,resetYValue);
    }
    @EventHandler
    public void onMotion(EventMotionUpdate e) {
        if (mc.thePlayer.isSneaking() || !e.isPre()) return;
        if (MovementUtils.isMoving()) {
            mc.timer.timerSpeed = customTimerValue.getValue().floatValue();

            if (mc.thePlayer.onGround) {
                MovementUtils.strafe(customSpeedValue.getValue().floatValue());
                mc.thePlayer.motionY = customYValue.getValue();
            } else if (customStrafeValue.getValue()) {
                MovementUtils.strafe(customSpeedValue.getValue().floatValue());
            } else {
                MovementUtils.strafe();
            }
        } else
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
    }

    @EventHandler
    public void onRender2d(EventRender2D e){
        setSuffix(customTimerValue.getValue()+"|"+customSpeedValue.getValue()+"|"+customYValue.getValue());
    }
    @Override
    public void onEnable() {
        if(resetXZValue.getValue()) mc.thePlayer.motionX = mc.thePlayer.motionZ = 0D;
        if(resetYValue.getValue()) mc.thePlayer.motionY = 0D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if(mc.thePlayer.isSneaking())
            return;

        if(MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);
    }

    @EventHandler
    public void onMove(EventMove event) {
    }
}
