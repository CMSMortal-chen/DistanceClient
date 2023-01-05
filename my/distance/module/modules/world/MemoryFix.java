package my.distance.module.modules.world;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.time.TimerUtil;

public class MemoryFix extends Module {
    private final TimerUtil mftimer = new TimerUtil();

    public MemoryFix() {
        super("MemoryFix", new String[]{"memoryfix"}, ModuleType.World);
    }

    @Override
    public void onEnable() {
        Runtime.getRuntime().gc();
        mftimer.reset();
    }

    @EventHandler
    public void onTick(EventTick e) {
        double mflimit = 10.0;
        if(mftimer.hasReached(120000) && mflimit <= ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * 100f / Runtime.getRuntime().maxMemory())) {
            Runtime.getRuntime().gc();
            mftimer.reset();
        }
    }
}
