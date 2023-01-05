package my.distance.module.modules.world;

import my.distance.api.events.Render.EventRender2D;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.world.dis.DisablerModule;
import my.distance.module.modules.world.dis.disablers.*;
import my.distance.util.time.MSTimer;
import my.distance.api.EventHandler;
import my.distance.api.events.World.*;
import my.distance.api.value.Mode;
import my.distance.api.value.Option;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Disabler extends Module {
    private final Mode mode = new Mode("Mode", Modes.values(), Modes.NewSpoof);
    public static Option lowerTimer = new Option("Lower timer on Lag",false);

    private final MSTimer lagTimer = new MSTimer();
    //    public static final Numbers<Double> delay = new Numbers<>("Delay",500d, 300d, 2000d, 100d);

    public Disabler() {
        super("Disabler", new String[]{"Bypass", "Patcher"}, ModuleType.World);
        addValues(mode,lowerTimer);
    }

    @Override
    public void onEnable() {
        ((Modes) mode.getValue()).get().onEnabled();
    }

    @Override
    public void onDisable() {
        ((Modes) mode.getValue()).get().onDisable();
    }

    @EventHandler
    public void onMotionUpdate(EventMotionUpdate e){
        ((Modes) mode.getValue()).get().onMotionUpdate(e);
    }

    @EventHandler
    public void onRender2d(EventRender2D e){
        ((Modes) mode.getValue()).get().onRender2d(e);

    }

    @EventHandler
    public void onPre(EventPreUpdate e) {
        setSuffix(mode.getValue());
        ((Modes) mode.getValue()).get().onUpdate(e);
        if (lowerTimer.getValue()) {
            if (!lagTimer.hasTimePassed(1000)) {
                mc.timer.timerSpeed = 0.7f;
            } else {
                mc.timer.timerSpeed = 1f;
            }
        }
    }

    @EventHandler
    public void onPacket(EventPacket e){
        ((Modes) mode.getValue()).get().onPacket(e);
        if (e.packet instanceof S08PacketPlayerPosLook) {
            lagTimer.reset();
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend event) {
        ((Modes) mode.getValue()).get().onPacket(event);
    }

    @EventHandler
    public void onPacketRE(EventPacketReceive e) {
        ((Modes) mode.getValue()).get().onPacket(e);
    }

    @EventHandler
    public void onRespawn(EventWorldChanged e) {
        ((Modes) mode.getValue()).get().onWorldChange(e);
    }

    enum Modes {
        Hypxiel(new HypixelDisabler()),
        NewSpoof(new NewSpoofDisabler()),
        AAC4LessFlag(new AAC4LessFlagDisabler()),
        AAC5Test(new AAC5TestDisabler()),
        VulcanCombat(new VulcanCombatDisabler());

        final DisablerModule disablerModule;

        Modes(DisablerModule disabler) {
            disablerModule = disabler;
        }

        public DisablerModule get() {
            return disablerModule;
        }
    }
}
