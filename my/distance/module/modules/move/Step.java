package my.distance.module.modules.move;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventStepConfirm;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventStep;
import my.distance.api.value.Mode;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Step extends Module {
    private final Mode Modes = new Mode("Mode", modes.values(), modes.NCP);
    private boolean isStep = false;
    private double stepX = 0.0;
    private double stepY = 0.0;
    private double stepZ = 0.0;

    public Step() {
        super("Step", new String[]{""}, ModuleType.Movement);
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        setSuffix(Modes.getValue());
    }

    @EventHandler
    public void onStep(EventStep event) {
        if (event.getStepHeight() > 0.5F) {
            isStep = true;
            stepX = mc.thePlayer.posX;
            stepY = mc.thePlayer.posY;
            stepZ = mc.thePlayer.posZ;
        }
    }

    @EventHandler
    public void onStepConfirm(EventStepConfirm e) {
        if (mc.thePlayer == null || !isStep) // Check if step
            return;
        if (mc.thePlayer.getEntityBoundingBox().minY - stepY > 0.5) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(stepX,
                    stepY + 0.41999998688698, stepZ, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(stepX,
                    stepY + 0.7531999805212, stepZ, false));
        }
    }

    enum modes {
        NCP
    }
}
