package my.distance.module.modules.render;


import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.render.Colors2;
import my.distance.util.render.RenderUtil;
import my.distance.api.EventHandler;
import my.distance.api.Priority;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Crosshair extends Module {
    private boolean dragging;
    float hue;
    private Option DYNAMIC;
    public static Numbers<Double> GAP;
    private Numbers<Double> WIDTH;
    public static Numbers<Double> SIZE;
    public static Numbers<Double> r = new Numbers<>("Red", "Red", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> g = new Numbers<>("Green", "Green", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> b = new Numbers<>("Blue", "Blue", 255.0, 0.0, 255.0, 1.0);

    private static double gaps = 0;

    static {
        Crosshair.GAP = new Numbers<>("Gap", 0.5, 0.25, 15.0, 0.25);
        Crosshair.SIZE = new Numbers<>("Size", 7.0, 0.25, 15.0, 0.25);
    }

    public Crosshair() {
        super("Crosshair", new String[]{"Crosshair"}, ModuleType.Render);
        this.DYNAMIC = new Option("Dynamic", true);
        this.WIDTH = new Numbers<>("Width", 0.25, 0.25, 10.0, 0.25);
        this.addValues(r, g, b, this.DYNAMIC, Crosshair.GAP, this.WIDTH, Crosshair.SIZE);
    }

    @EventHandler(priority = Priority.LOW)
    public void onGui(final EventRender2D e) {
        gaps = AnimationUtils.animate(isMoving() ? 4 : 0, gaps, 20f / Minecraft.getDebugFPS());
        final int red = r.getValue().intValue();
        final int green = g.getValue().intValue();
        final int blue = b.getValue().intValue();
        final int alph = 255;
        final double gap = Crosshair.GAP.getValue();
        final double width = this.WIDTH.getValue();
        final double size = Crosshair.SIZE.getValue();
        final ScaledResolution scaledRes = new ScaledResolution(Crosshair.mc);
        RenderUtil.rectangleBordered(scaledRes.getScaledWidth() / 2f - width, scaledRes.getScaledHeight() / 2f - gap - size - (gaps), scaledRes.getScaledWidth() / 2f + 1.0f + width, scaledRes.getScaledHeight() / 2f - gap - (gaps), 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(scaledRes.getScaledWidth() / 2f - width, scaledRes.getScaledHeight() / 2f + gap + 1.0 + (gaps) - 0.15, scaledRes.getScaledWidth() / 2f + 1.0f + width, scaledRes.getScaledHeight() / 2f + 1 + gap + size + (gaps) - 0.15, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(scaledRes.getScaledWidth() / 2f - gap - size - (gaps) + 0.15, scaledRes.getScaledHeight() / 2f - width, scaledRes.getScaledWidth() / 2f - gap - (gaps) + 0.15, scaledRes.getScaledHeight() / 2f + 1.0f + width, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
        RenderUtil.rectangleBordered(scaledRes.getScaledWidth() / 2f + 1 + gap + (gaps), scaledRes.getScaledHeight() / 2f - width, scaledRes.getScaledWidth() / 2f + size + gap + 1.0 + (gaps), scaledRes.getScaledHeight() / 2f + 1.0f + width, 0.5, Colors2.getColor(red, green, blue, alph), new Color(25, 25, 25, alph).getRGB());
    }

    public boolean isMoving() {
        if (this.DYNAMIC.getValue()) {
            final Minecraft mc = Crosshair.mc;
            if (!mc.thePlayer.isCollidedHorizontally) {
                final Minecraft mc2 = Crosshair.mc;
                if (!mc.thePlayer.isSneaking()) {
                    if (mc.thePlayer.movementInput.moveForward == 0.0f) {
                        return mc.thePlayer.movementInput.moveStrafe != 0.0f;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
