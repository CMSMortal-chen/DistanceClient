package my.distance.ui.shader;

import my.distance.util.misc.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class Shader {
    private Framebuffer frameBuffer;
    private ShaderLoader clientShader;
    private String fragmentShader;

    public Shader(String fragmentShader) {
        this.fragmentShader = fragmentShader;
    }

    public void startShader() {
        if (Helper.mc.gameSettings != null) {
            if (Helper.mc.gameSettings.guiScale != 2 && Helper.mc.currentScreen == null) {
                Helper.mc.gameSettings.guiScale = 2;
            }
        }

        if (this.frameBuffer == null) {
            this.frameBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
        } else if (this.frameBuffer.framebufferWidth != Minecraft.getMinecraft().displayWidth || this.frameBuffer.framebufferHeight != Minecraft.getMinecraft().displayHeight) {
            this.frameBuffer.unbindFramebuffer();
            this.frameBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
            if (this.clientShader != null) {
                this.clientShader.delete();
                this.clientShader = new ShaderLoader(this.fragmentShader, this.frameBuffer.framebufferTexture);
            }
        }

        if (this.clientShader == null) {
            this.clientShader = new ShaderLoader(this.fragmentShader, this.frameBuffer.framebufferTexture);
        }

        this.frameBuffer.bindFramebuffer(false);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glClear(16640);
    }

    public void stopShader() {
        GL11.glBlendFunc(770, 771);
        this.clientShader.update();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        ScaledResolution sr = new ScaledResolution(Helper.mc);
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, this.clientShader.getFboTextureID());
        GL11.glBegin(4);
        GL11.glTexCoord2d(0.0D, 1.0D);
        GL11.glVertex2d(0.0D, 0.0D);
        GL11.glTexCoord2d(0.0D, 0.0D);
        double x = 0.0D;
        double y = 0.0D;
        double width = sr.getScaledWidth();
        double height = sr.getScaledHeight();
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2d(1.0D, 0.0D);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2d(1.0D, 0.0D);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2d(1.0D, 1.0D);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2d(0.0D, 1.0D);
        GL11.glVertex2d(0.0D, 0.0D);
        GL11.glEnd();
    }

    public void deleteShader() {
        try {
            this.clientShader.delete();
            this.frameBuffer.unbindFramebuffer();
            this.frameBuffer.unbindFramebufferTexture();
            this.clientShader = null;
            this.frameBuffer = null;
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}

