package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.manager.FriendManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.math.MathUtil;
import my.distance.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Tracers
        extends Module {
    public Tracers() {
        super("Tracers", new String[]{"lines", "tracer"}, ModuleType.Render);
    }

    @EventHandler
    private void on3DRender(EventRender3D e) {
        for (Entity o : mc.theWorld.loadedEntityList) {
            double[] arrd;
            if (!o.isEntityAlive() || !(o instanceof EntityPlayer) || o == mc.thePlayer) continue;
            double posX = o.lastTickPosX + (o.posX - o.lastTickPosX) * (double)e.getPartialTicks() - mc.getRenderManager().renderPosX;
            double posY = o.lastTickPosY + (o.posY - o.lastTickPosY) * (double)e.getPartialTicks() - mc.getRenderManager().renderPosY;
            double posZ = o.lastTickPosZ + (o.posZ - o.lastTickPosZ) * (double)e.getPartialTicks() - mc.getRenderManager().renderPosZ;
            boolean old = mc.gameSettings.viewBobbing;
            RenderUtil.startDrawing();
            mc.gameSettings.viewBobbing = false;
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
            mc.gameSettings.viewBobbing = old;
            float color = (float)Math.round(255.0 - mc.thePlayer.getDistanceSqToEntity(o) * 255.0 / MathUtil.square((double) mc.gameSettings.renderDistanceChunks * 2.5)) / 255.0f;
            if (FriendManager.isFriend(o.getName())) {
                double[] arrd2 = new double[3];
                arrd2[0] = 0;
                arrd2[1] = 130;
                arrd = arrd2;
                arrd2[2] = 255;
            } else {
                double[] arrd3 = new double[3];
                arrd3[0] = 255;
                arrd3[1] = 1f - color;
                arrd = arrd3;
                arrd3[2] = 1f - color;
            }
            this.drawLine(o, arrd, posX, posY, posZ);
            RenderUtil.stopDrawing();
        }
    }

    private void drawLine(Entity entity, double[] color, double x, double y, double z) {
        float distance = mc.thePlayer.getDistanceToEntity(entity);
        float xD = distance / 48.0f;
        if (xD >= 1.0f) {
            xD = 1.0f;
        }
        boolean entityesp = false;
        GL11.glEnable(2848);
        if (color.length >= 4) {
            if (color[3] <= 0.1) {
                return;
            }
            GL11.glColor4d(color[0], color[1], color[2], color[3]);
        } else {
            GL11.glColor3d(color[0], color[1], color[2]);
        }
        GL11.glLineWidth(1f);
        GL11.glBegin(1);
        GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(2848);
    }
}


