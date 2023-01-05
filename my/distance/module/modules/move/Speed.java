package my.distance.module.modules.move;

import my.distance.api.EventHandler;
import my.distance.api.events.World.*;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.module.modules.move.speedmode.speed.*;
import my.distance.ui.notifications.user.Notifications;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;


public class Speed extends Module {
    public static Mode mode = new Mode("Mode", SpeedMode.values(), SpeedMode.Hypixel);
    public static final Numbers<Double> maxtimerValue = new Numbers<>("MaxTimer", 1.0d, 1.0d, 2.0d,0.01d);
    public static final Numbers<Double> mintimerValue = new Numbers<>("MinTimer", 1.0d, 1.0d, 2.0d,0.01d);
    public static final Numbers<Double> maxtimerMisValue = new Numbers<>("MaxMSTimer", 500.0d, 0.0d, 2000.0d,0.01d);
    public static final Numbers<Double> mintimerMisValue = new Numbers<>("MinMSTimer", 600.0d, 0.0d, 2000.0d,0.01d);
    public static final Option sendJumpValue = new Option("SendJump",false);

    public static Option lagcheck = new Option("LagBackCheck", true);

    public Speed() {
        super("Speed", new String[]{"zoom"}, ModuleType.Movement);
        addValues(mode, mintimerValue, maxtimerValue, mintimerMisValue, maxtimerMisValue, sendJumpValue, lagcheck);

        setValueDisplayable(new Value<?>[]{mintimerValue, maxtimerValue, mintimerMisValue, maxtimerMisValue, sendJumpValue}, mode, SpeedMode.Hypixel);
    }
    public void onEnable() {
        ((SpeedMode)mode.getValue()).getModule().onEnabled();
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        ((SpeedMode)mode.getValue()).getModule().onDisabled();
    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive e) {
        Packet<?> packet = e.getPacket();
        if (packet instanceof S08PacketPlayerPosLook && lagcheck.getValue()) {
            Notifications.getManager().post("Speed", "Speed拉回!已自动关闭Speed");
            this.setEnabled(false);
        }
    }
    @EventHandler
    public void onSteps(EventStep e){
        ((SpeedMode)mode.getValue()).getModule().onStep(e);
    }

    @EventHandler
    public void onPost(EventPostUpdate e) {
        ((SpeedMode)mode.getValue()).getModule().onPost(e);
    }

    @EventHandler
    public void onMotion(EventMotionUpdate e){
        ((SpeedMode)mode.getValue()).getModule().onMotion(e);
    }

    @EventHandler
    public void onPacket(EventPacketSend e){
        ((SpeedMode)mode.getValue()).getModule().onPacketSend(e);
    }

    @EventHandler
    private void onMove(EventMove event) {
        ((SpeedMode)mode.getValue()).getModule().onMove(event);
    }


    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        ((SpeedMode)mode.getValue()).getModule().onPre(e);
        this.setSuffix(mode.getValue());
    }

    public enum SpeedMode {
        Hypixel(new HypixelSpeed()),
        Hive(new HiveSpeed()),
        AAC440(new AAC440Speed()),
        AutoJump(new AutoJumpSpeed()),
        Bhop(new BhopSpeed()),
        GudHop(new GudHopSpeed()),
        HuaYuTingA(new HytBhopSpeed()),
        HuaYuTingB(new HytTypeBSpeed()),
        OnGround(new OnGroundSpeed()),
        AACTimer(new AACTimer()),
        VulcanHop(new VulcanHopSpeed()),
        VulcanFastHop(new VulcanFastHopSpeed()),
        VulcanLowHop(new VulcanLowHopSpeed());

        final SpeedModule module;
        SpeedMode(SpeedModule speedModule){
            this.module = speedModule;
        }
        public SpeedModule getModule(){
            return module;
        }
    }
}
