package my.distance.util.render.gl;

import my.distance.util.math.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GLUtils {
	public static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer((int) 16);
	public static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer((int) 16);
	public static final IntBuffer VIEWPORT = BufferUtils.createIntBuffer((int) 16);
	public static final FloatBuffer TO_SCREEN_BUFFER = BufferUtils.createFloatBuffer((int) 3);
	public static final FloatBuffer TO_WORLD_BUFFER = BufferUtils.createFloatBuffer((int) 3);

	private GLUtils() {
	}
	public static int getMouseX() {
		return Mouse.getX() * getScreenWidth() / Minecraft.getMinecraft().displayWidth;
	}

	public static int getMouseY() {
		return getScreenHeight() - Mouse.getY() * getScreenHeight() / Minecraft.getMinecraft().displayWidth - 1;
	}
	public static int getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth / getScaleFactor();
	}

	public static int getScreenHeight() {
		return Minecraft.getMinecraft().displayHeight / getScaleFactor();
	}

	public static int getScaleFactor() {
		int scaleFactor = 1;
		final boolean isUnicode = Minecraft.getMinecraft().isUnicode();
		int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
		if (guiScale == 0) {
			guiScale = 1000;
		}
		while (scaleFactor < guiScale && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320 && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
			++scaleFactor;
		}
		if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
			--scaleFactor;
		}
		return scaleFactor;
	}
	public static void init() {
	}

	public static float[] getColor(int hex) {
		return new float[] { (float) (hex >> 16 & 255) / 255.0f, (float) (hex >> 8 & 255) / 255.0f,
				(float) (hex & 255) / 255.0f, (float) (hex >> 24 & 255) / 255.0f };
	}

	public static void glColor(int hex) {
		float[] color = GLUtils.getColor(hex);
		GlStateManager.color(color[0], color[1], color[2], color[3]);
	}

	public static void rotateX(float angle, double x, double y, double z) {
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(angle, 1.0f, 0.0f, 0.0f);
		GlStateManager.translate(-x, -y, -z);
	}

	public static void rotateY(float angle, double x, double y, double z) {
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);
		GlStateManager.translate(-x, -y, -z);
	}

	public static void rotateZ(float angle, double x, double y, double z) {
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
		GlStateManager.translate(-x, -y, -z);
	}

	public static Vec3f toScreen(Vec3f pos) {
		return GLUtils.toScreen(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3f toScreen(double x, double y, double z) {
		boolean result = GLU.gluProject((float) ((float) x), (float) ((float) y), (float) ((float) z),
				(FloatBuffer) MODELVIEW, (FloatBuffer) PROJECTION, (IntBuffer) VIEWPORT,
				(FloatBuffer) ((FloatBuffer) TO_SCREEN_BUFFER.clear()));
		if (result) {
			return new Vec3f(TO_SCREEN_BUFFER.get(0), (float) Display.getHeight() - TO_SCREEN_BUFFER.get(1),
					TO_SCREEN_BUFFER.get(2));
		}
		return null;
	}

	public static Vec3f toWorld(Vec3f pos) {
		return GLUtils.toWorld(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3f toWorld(double x, double y, double z) {
		boolean result = GLU.gluUnProject((float) ((float) x), (float) ((float) y), (float) ((float) z),
				(FloatBuffer) MODELVIEW, (FloatBuffer) PROJECTION, (IntBuffer) VIEWPORT,
				(FloatBuffer) ((FloatBuffer) TO_WORLD_BUFFER.clear()));
		if (result) {
			return new Vec3f(TO_WORLD_BUFFER.get(0), TO_WORLD_BUFFER.get(1), TO_WORLD_BUFFER.get(2));
		}
		return null;
	}
	public static void startSmooth() {
		GL11.glEnable((int)2848);
		GL11.glEnable((int)2881);
		GL11.glEnable((int)2832);
		GL11.glEnable((int)3042);
		GL11.glBlendFunc((int)770, (int)771);
		GL11.glHint((int)3154, (int)4354);
		GL11.glHint((int)3155, (int)4354);
		GL11.glHint((int)3153, (int)4354);
	}

	public static void endSmooth() {
		GL11.glDisable((int)2848);
		GL11.glDisable((int)2881);
		GL11.glEnable((int)2832);
	}

	public static FloatBuffer getModelview() {
		return MODELVIEW;
	}

	public static FloatBuffer getProjection() {
		return PROJECTION;
	}

	public static IntBuffer getViewport() {
		return VIEWPORT;
	}

}
