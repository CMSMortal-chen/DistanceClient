package my.distance.util.render;

import my.distance.ui.gui.GuiNeedBlur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class Blur {
    private static ShaderGroup blurShader;
    private static List<Shader> listShaders;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Framebuffer buffer;
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static final ResourceLocation shader = new ResourceLocation("shaders/post/blurArea.json");

    static {
        if (blurShader == null) {
            try {
                blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listShaders = blurShader.listShaders;
    }

    public static void initFboAndShader() {
        try {
            if (blurShader == null)
                blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
           /* Field field = blurShader.getClass().getDeclaredField("mainFramebuffer");
            field.setAccessible(true);
            buffer = (Framebuffer)field.get(blurShader);*/
            buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            buffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight, float opacity) {
        listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
        listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
        /*listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
//		listShaders.get(0).getShaderManager().getShaderUniform("Opacity").set(opacity);
//		listShaders.get(1).getShaderManager().getShaderUniform("Opacity").set(opacity);
        listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);*/
    }

    public static void blurArea(int x, int y, int width, int height, float intensity, float blurWidth, float blurHeight) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            if (OpenGlHelper.isFramebufferEnabled()) {
                buffer.framebufferClear();
                GL11.glScissor((x * factor), (mc.displayHeight - y * factor - height * factor), (width * factor), (height * factor));
                GL11.glEnable(3089);
                setShaderConfigs(intensity, blurWidth, blurHeight, 1.0f);
                buffer.bindFramebuffer(true);
                blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
                mc.getFramebuffer().bindFramebuffer(true);
                GL11.glDisable(3089);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableBlend();
                GL11.glScalef((float) factor, (float) factor, (float) 0.0f);
            }
        }
    }

    public static void blurArea(int x, int y, int width, int height, float intensity) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            buffer.framebufferClear();
            GL11.glScissor((x * factor), (mc.displayHeight - y * factor - height * factor), (width * factor), (height * factor));
            GL11.glEnable(3089);
            setShaderConfigs(intensity, 1.0f, 0.0f, 1.0f);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glDisable(3089);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableBlend();
            GL11.glScalef((float) factor, (float) factor, (float) 0.0f);
            RenderHelper.enableGUIStandardItemLighting();
        }
    }

    public static void blurAreaBoarder(float x, float f, float width, float height, float intensity, float blurWidth, float blurHeight) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            GL11.glScissor((int) ((x * (float) factor)), (int) (((float) mc.displayHeight - f * (float) factor - height * (float) factor) + 1), (int) ((width * (float) factor)), (int) ((height * (float) factor)));
            GL11.glEnable(3089);
            setShaderConfigs(intensity, blurWidth, blurHeight, 1.0f);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glDisable(3089);
        }
    }

    public static void blurAreaBoarder(float x, float f, float width, float height, float intensity, float opacity, float blurWidth, float blurHeight) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            GL11.glScissor((int) ((x * (float) factor)), (int) (((float) mc.displayHeight - f * (float) factor - height * (float) factor) + 1), (int) ((width * (float) factor)), (int) ((height * (float) factor)));
            GL11.glEnable(3089);
            setShaderConfigs(intensity, blurWidth, blurHeight, opacity);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glDisable(3089);
        }
    }

    public static void blurShape(float g, float f, float h, float height, float intensity, float blurWidth, float blurHeight) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            GL11.glScissor((int) ((g * (float) factor)), (int) (((float) mc.displayHeight - f * (float) factor - height * (float) factor) + 1), (int) ((h * (float) factor)), (int) ((height * (float) factor)));
            GL11.glEnable(3089);
            setShaderConfigs(intensity, blurWidth, blurHeight, 1.0F);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            GL11.glDisable(3089);
        }
    }

    public static void blurAreaBoarder(float x, float y, float width, float height) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            listShaders.get(0).getShaderManager().getShaderUniform("BlurXY").set(x, (float) RenderUtil.height() - y - height);
            listShaders.get(0).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
            listShaders.get(1).getShaderManager().getShaderUniform("BlurXY").set(x, (float) RenderUtil.height() - y - height);
            listShaders.get(1).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
            //  buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            //   GL11.glDisable(3089);
        }
    }

    public static void blurAreaBoarderXY(float x, float y, float x2, float y2) {
        if (GuiNeedBlur.isBlurEnabled()) {
            float width = Math.max(x, x2) - Math.min(x2, x);
            float height = Math.max(y, y2) - Math.min(y2, y);
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            listShaders.get(0).getShaderManager().getShaderUniform("BlurXY").set(x, (float) RenderUtil.height() - y - height);
            listShaders.get(0).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
            listShaders.get(1).getShaderManager().getShaderUniform("BlurXY").set(x, (float) RenderUtil.height() - y - height);
            listShaders.get(1).getShaderManager().getShaderUniform("BlurCoord").set(width, height);
            //  buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            //   GL11.glDisable(3089);
        }
    }

    public static void blurAll(float intensity) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            setShaderConfigs(intensity, 0.5f, 0.5f, 1.0f);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
        }
    }

    public static void blurAll(float intensity, float opacity) {
        if (GuiNeedBlur.isBlurEnabled()) {
            ScaledResolution scale = new ScaledResolution(mc);
            int factor = scale.getScaleFactor();
            int factor2 = scale.getScaledWidth();
            int factor3 = scale.getScaledHeight();
            if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
                initFboAndShader();
            }
            lastScale = factor;
            lastScaleWidth = factor2;
            lastScaleHeight = factor3;
            setShaderConfigs(intensity, 0.0f, 1.0f, opacity);
            buffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
        }
    }
}

