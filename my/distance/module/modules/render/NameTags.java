package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.Priority;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.render.nametagmodules.DistanceNameTag;
import my.distance.module.modules.render.nametagmodules.DistanceShortNameTag;
import my.distance.util.math.RotationUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sigma
 */
public class NameTags extends Module
{
	public Mode mode = new Mode("Mode",nameTagModes.values(),nameTagModes.Distance);
	public static Map<EntityLivingBase, double[]> entityPositions = new HashMap<>();
	public Option invis = new Option("ShowInvisible", "ShowInvisible", true);
	public static Numbers<Double> alpha = new Numbers<>("Alpha",80d,10d,255d,1d);
	public Option armor = new Option("ShowArmor", "ShowArmor", false);
	public float animationX;

	public NameTags() {
		super("NameTags", new String[]{"NameTag"}, ModuleType.Render);
		this.addValues(mode,invis,armor,alpha);
	}

	@EventHandler
	public void update(EventRender3D class1170) {
		try {
			this.updatePositions();
		}
		catch (Exception ignored) {

		}
	}

	@EventHandler(priority = Priority.HIGH)
	public void onRender2D(EventRender2D class112) {
		GlStateManager.pushMatrix();
		for (EntityLivingBase entity : entityPositions.keySet()) {
			switch ((nameTagModes)mode.getValue()) {
				case Distance:
					DistanceNameTag.renderNameTag(entity, invis.getValue(), entityPositions, armor.getValue(), alpha.getValue().intValue());
					break;
				case Short:
					DistanceShortNameTag.renderNameTag(entity, invis.getValue(), entityPositions, armor.getValue(), alpha.getValue().intValue());
					break;
			}
		}
		GlStateManager.popMatrix();
	}

	private void updatePositions() {
		entityPositions.clear();
		float pTicks = mc.timer.renderPartialTicks;
		for (Entity o : mc.theWorld.loadedEntityList) {
			if ((o != mc.thePlayer) && ((o instanceof EntityPlayer))
					&& (!o.isInvisible() || !this.invis.getValue())) {
				double x = o.lastTickPosX + (o.posX - o.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
				double y = o.lastTickPosY + (o.posY - o.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
				double z = o.lastTickPosZ + (o.posZ - o.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;
				y += o.height + 0.2D;
				if ((convertTo2D(x, y, z)[2] >= 0.0D) && (convertTo2D(x, y, z)[2] < 1.0D)) {
					entityPositions.put((EntityLivingBase) o,
							new double[]{convertTo2D(x, y, z)[0], convertTo2D(x, y, z)[1],
									Math.abs(convertTo2D(x, y + 1.0D, z, o)[1] - convertTo2D(x, y, z, o)[1]),
									convertTo2D(x, y, z)[2]});
				}
			}
		}
	}
	private double[] convertTo2D(double x, double y, double z, Entity ent) {
		float pTicks = mc.timer.renderPartialTicks;
		float prevYaw = mc.thePlayer.rotationYaw;
		float prevPrevYaw = mc.thePlayer.prevRotationYaw;
		float[] rotations = RotationUtil.getRotationFromPosition(
				ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
				ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
				ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D);
		mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
		mc.entityRenderer.setupCameraTransform(pTicks, 0);
		double[] convertedPoints = convertTo2D(x, y, z);
		mc.getRenderViewEntity().rotationYaw = prevYaw;
		mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
		mc.entityRenderer.setupCameraTransform(pTicks, 0);
		return convertedPoints;
	}

	private double[] convertTo2D(double x, double y, double z) {
		FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
		IntBuffer viewport = BufferUtils.createIntBuffer(16);
		FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(2982, modelView);
		GL11.glGetFloat(2983, projection);
		GL11.glGetInteger(2978, viewport);
		boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
		if (result) {
			return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
		}
		return null;
	}
	enum nameTagModes{
		Distance,
		Short;
	}
}
