package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.render.gl.GLUtils;

import java.awt.*;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public class Breadcrumbs extends Module {
    public final Numbers<Double> colorRedValue = new Numbers<>("R", 255d, 0d, 255d, 1d);
    public final Numbers<Double> colorGreenValue = new Numbers<>("G", 179d, 0d, 255d, 1d);
    public final Numbers<Double> colorBlueValue = new Numbers<>("B", 72d, 0d, 255d, 1d);
    public final Option colorRainbow = new Option("Rainbow", false);
    private final LinkedList<double[]> positions = new LinkedList<>();

    public Breadcrumbs() {
        super("Breadcrumbs", new String[]{"Breadcrumb"}, ModuleType.Render);
        this.addValues(colorRedValue,colorGreenValue,colorBlueValue,colorRainbow);
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        final Color color = colorRainbow.getValue() ? HUD.RainbowColor : new Color(colorRedValue.getValue().intValue(), colorGreenValue.getValue().intValue(), colorBlueValue.getValue().intValue());

        synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glBegin(GL_LINE_STRIP);
            GLUtils.glColor(color.getRGB());
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (final double[] pos : positions)
                glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

            glColor4d(1, 1, 1, 1);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX,
                    mc.thePlayer.getEntityBoundingBox().minY + (mc.thePlayer.getEyeHeight() * 0.5f),
                    mc.thePlayer.posZ});
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        synchronized (positions) {
            positions.clear();
        }
        super.onDisable();
    }
}
