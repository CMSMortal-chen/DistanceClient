package my.distance.module.modules.combat;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventMove;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.Client;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.move.Fly;
import my.distance.util.math.RotationUtil;
import my.distance.util.entity.PlayerUtil;
import my.distance.util.render.RenderUtil;
import my.distance.util.render.gl.GLUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;

import java.awt.*;

public class TargetStrafe extends Module {
	public static Mode Esp = new Mode("ESP",EspMode.values(),EspMode.Round);
	public static Numbers<Double> Radius = new Numbers<>("Radius", 1.0D, 0.0D, 6.0D, 0.1D);
	public static Option onJump = new Option("onJump", false);
	public static Option WallCheck = new Option("Check", true);
	private int direction = -1;
	public TargetStrafe() {
		super("TargetStrafe", new String[] { "TargetStrafe" }, ModuleType.Combat);
		this.addValues(Radius, Esp,onJump, WallCheck);
	}

	enum EspMode{
		Round,
		Polygon
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	public static double getSpeedByXZ(double motionX, double motionZ) {
		final double vel = Math.sqrt(motionX * motionX + motionZ * motionZ);
		return vel;
	}
	private void switchDirection() {
		if(this.direction == 1) {
			this.direction = -1;
		} else {
			this.direction = 1;
		}

	}
	public final boolean doStrafeAtSpeed(EventMove event, double moveSpeed) {
		boolean strafe = this.canStrafe();
		if(strafe) {
			float[] rotations = RotationUtil.getRotations(KillAura.currentTarget);
			if((double)mc.thePlayer.getDistanceToEntity(KillAura.currentTarget) <= Radius.getValue()) {
				PlayerUtil.setSpeed(event, moveSpeed, rotations[0], this.direction, 0.0D,false);
			}else {
				PlayerUtil.setSpeed(event, moveSpeed, rotations[0], this.direction, 1.0D, !(mc.thePlayer.getDistanceToEntity(KillAura.currentTarget) <= Radius.getValue() + 0.2));
			}
		}

		return strafe;
	}
	public final boolean doStrafeAtSpeedWithoutEvent(double moveSpeed) {
		boolean strafe = this.canStrafe();
		if(strafe) {
			float[] rotations = RotationUtil.getRotations(KillAura.currentTarget);
			if((double)mc.thePlayer.getDistanceToEntity(KillAura.currentTarget) <= Radius.getValue()) {
				PlayerUtil.setSpeedWithoutEvent(moveSpeed, rotations[0], this.direction, 0.0D,false);
			}else {
				PlayerUtil.setSpeedWithoutEvent(moveSpeed, rotations[0], this.direction, 1.0D, !(mc.thePlayer.getDistanceToEntity(KillAura.currentTarget) <= Radius.getValue() + 0.2));
			}
		}

		return strafe;
	}

	@EventHandler
	public final void onUpdate(EventPreUpdate event) {
		if (canStrafe()) {
			float[] rotations = RotationUtil.getRotations(KillAura.currentTarget);
			double cos = Math.cos(Math.toRadians(rotations[0] + 90.0F));
			double sin = Math.sin(Math.toRadians(rotations[0] + 90.0F));
			double x = KillAura.currentTarget.posX + Radius.getValue() * cos;
			double z = KillAura.currentTarget.posZ + Radius.getValue() * sin;
			if (WallCheck.getValue() && needToChange(x, z)) {
				this.switchDirection();
			}
		}
	}

	public boolean needToChange(double x, double z) {
		if (mc.thePlayer.isCollidedHorizontally)
			return true;
		for (int i = (int) (mc.thePlayer.posY + 4.0D); i >= 0; i--) {
			BlockPos playerPos = new BlockPos(x, i, z);
			if (mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.lava) || this.mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.fire))
				return true;
			if (!ModuleManager.getModuleByClass(Fly.class).isEnabled()) {
				if (!mc.theWorld.isAirBlock(playerPos))
					return false;
			}else {
				return false;
			}
		}
		return true;
	}

	@EventHandler
	public final void onRender3D(EventRender3D event) {
		switch ((EspMode)Esp.getValue()) {
			case Round: {
				if (KillAura.currentTarget != null) {
					drawCircle(KillAura.currentTarget, event.getPartialTicks(), Radius.getValue());
				}
				break;
			}
			case Polygon:{
				if (KillAura.currentTarget != null) {
					drawPolygon(KillAura.currentTarget, event.getPartialTicks(), Radius.getValue(),2.0f,new Color(255,255,255));
					//drawPolygon(KillAura.currentTarget, event.getPartialTicks(), Radius.getValue(),2.5f,new Color(0,0,0));
				}
				break;
			}
		}
	}

	private boolean Check(Entity e2) {
		if(!e2.isEntityAlive()) {
			return false;
		}else if(KillAura.currentTarget != e2){
			return false;
		} else {
			return e2 != mc.thePlayer && e2 instanceof EntityPlayer;
		}
	}

	private void drawCircle(Entity entity, float partialTicks, double rad) {
		GL11.glPushMatrix();
		GL11.glDisable(3553);
		RenderUtil.startDrawing();
		GLUtils.startSmooth();
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glLineWidth(1.0F);
		GL11.glBegin(3);
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().viewerPosX;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().viewerPosY;
		Color color = Color.WHITE;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().viewerPosZ;
		if(entity == KillAura.currentTarget && ModuleManager.getModuleByName("Speed").isEnabled()) {
			color = Client.getBlueColor(1);
		}

		float r = 0.003921569F * (float)color.getRed();
		float g = 0.003921569F * (float)color.getGreen();
		float b = 0.003921569F * (float)color.getBlue();
		double pix2 = 6.283185307179586D;

		for(int i = 0; i <= 90; ++i) {
			GL11.glColor3f(r, g, b);
			GL11.glVertex3d(x + rad * Math.cos((double)i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin((double)i * 6.283185307179586D / 45.0D));
		}

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(2929);
		RenderUtil.stopDrawing();
		GLUtils.endSmooth();
		GL11.glEnable(3553);
		GL11.glPopMatrix();
	}
	private void drawPolygon(Entity entity, float partialTicks, double rad, float Line,Color color) {
		GL11.glPushMatrix();
		GL11.glDisable(3553);
		RenderUtil.startDrawing();
		GLUtils.startSmooth();
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glLineWidth(Line);
		GL11.glBegin(3);
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().viewerPosX;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().viewerPosY;
		//Color color = Color.WHITE;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().viewerPosZ;

		float r = 0.004921569F * (float)color.getRed();
		float g = 0.003921569F * (float)color.getGreen();
		float b = 0.003921569F * (float)color.getBlue();

		for(int i = 0; i <= 90; ++i) {
			GL11.glColor3f(r, g, b);
			GL11.glVertex3d(x + rad * Math.cos((double)i * 35.283185307179586D / 90.0D), y, z + rad * Math.sin((double)i * 35.283185307179586D / 90.0D));
		}

		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(2929);
		RenderUtil.stopDrawing();
		GLUtils.endSmooth();
		GL11.glEnable(3553);
		GL11.glPopMatrix();
	}
	public boolean canStrafe() {
		if (this.isEnabled()){
			if(ModuleManager.getModuleByClass(KillAura.class).isEnabled() && KillAura.currentTarget != null){
				return !((Boolean) onJump.getValue()) || mc.gameSettings.keyBindJump.isKeyDown();
			}
		}
		return false;
	}
}
