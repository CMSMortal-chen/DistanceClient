package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventLivingUpdate;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.events.World.EventRespawn;
import my.distance.api.value.Numbers;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.font.FontLoaders;
import my.distance.util.misc.Location;
import my.distance.util.render.Particles;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class DMGParticle extends Module
{
	private final HashMap<EntityLivingBase, Float> healthMap;
	private final CopyOnWriteArrayList<Particles> particles;
	public static Numbers<Double> R = new Numbers<>("Red", 255d, 0d, 255d, 1d);
	public static Numbers<Double> G = new Numbers<>("Green", 0d, 0d, 255d, 1d);
	public static Numbers<Double> B = new Numbers<>("Blue", 0d, 0d, 255d, 1d);

	public DMGParticle() {
		super("DMGParticle", new String[]{"Damage Particle"}, ModuleType.Render);
		this.healthMap = new HashMap<>();
		this.particles = new CopyOnWriteArrayList<>();
		addValues(R,G,B);
	}

	@EventHandler
	public void onRespawn(EventRespawn class1785) {
		this.particles.clear();
		this.healthMap.clear();
	}

	@EventHandler
	public void onUpdate(EventPreUpdate eventUpdate) {
		this.particles.forEach(this::lambda$onUpdate$0);
	}

	@EventHandler
	public void onLivingUpdate(EventLivingUpdate class2165) {
		Entity entity = class2165.getEntity();
		if (entity == mc.thePlayer) {
			return;
		}
		if (!this.healthMap.containsKey(entity)) {
			this.healthMap.put((EntityLivingBase) entity, ((EntityLivingBase) entity).getHealth());
		}
		float floatValue = this.healthMap.get(entity);
		float health = ((EntityLivingBase) entity).getHealth();
		if (floatValue != health) {
			String p_i1238_2_;
			if (floatValue - health < 0.0f) {
				p_i1238_2_ = "\247a" + roundToPlace((floatValue - health) * -1.0f, 1);
			}
			else {
				p_i1238_2_ = "" + roundToPlace(floatValue - health, 1);
			}
			Location p_i1238_1_ = new Location((EntityLivingBase) entity);
			p_i1238_1_.setY(entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
			p_i1238_1_.setX(p_i1238_1_.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.1);
			p_i1238_1_.setZ(p_i1238_1_.getZ() - 0.5 + new Random(System.currentTimeMillis() + (0x203FF36645D9EA2EL ^ 0x203FF36645D9EA2FL)).nextInt(5) * 0.1);
			this.particles.add(new Particles(p_i1238_1_, p_i1238_2_));
			this.healthMap.remove(entity);
			this.healthMap.put((EntityLivingBase) entity, ((EntityLivingBase) entity).getHealth());
		}
	}
	public static double roundToPlace(double p_roundToPlace_0_,int p_roundToPlace_2_) {
		if (p_roundToPlace_2_ < 0) {
			throw new IllegalArgumentException();
		}
		return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
	}

	@EventHandler
	public void onRender(EventRender3D class1170) {
		for (Particles class1171 : this.particles) {
			double x = class1171.location.getX();
			double n = x - mc.getRenderManager().renderPosX;
			double y = class1171.location.getY();
			double n2 = y - mc.getRenderManager().renderPosY;
			double z = class1171.location.getZ();
			double n3 = z - mc.getRenderManager().renderPosZ;
			GlStateManager.pushMatrix();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
			GlStateManager.translate((float)n, (float)n2, (float)n3);
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
			float p_rotate_1_;
			if (mc.gameSettings.thirdPersonView == 2) {
				p_rotate_1_ = -1.0f;
			}
			else {
				p_rotate_1_ = 1.0f;
			}
			GlStateManager.rotate(mc.getRenderManager().playerViewX, p_rotate_1_, 0.0f, 0.0f);
			double p_scale_4_ = 0.03;
			GlStateManager.scale(-p_scale_4_, -p_scale_4_, p_scale_4_);
			RenderUtil.enableGL2D();
			RenderUtil.disableGL2D();
			GL11.glDepthMask(false);
			mc.fontRendererObj.drawStringWithShadow(class1171.text, (float)(-(FontLoaders.GoogleSans18.getStringWidth(class1171.text) / 2)), (float)(-(FontLoaders.GoogleSans18.getHeight() - 1)), new Color(R.getValue().intValue(),G.getValue().intValue(),B.getValue().intValue()).getRGB());
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDepthMask(true);
			GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
			GlStateManager.disablePolygonOffset();
			GlStateManager.popMatrix();
		}
	}

	private void lambda$onUpdate$0(Particles update) {
		++update.ticks;
		if (update.ticks <= 10) {
			update.location.setY(update.location.getY() + update.ticks * 0.005);
		}
		if (update.ticks > 20) {
			this.particles.remove(update);
		}
	}
	}
