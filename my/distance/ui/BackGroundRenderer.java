package my.distance.ui;

import my.distance.util.ClientSetting;
import my.distance.ui.shader.Shader;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * BackGround Renderer
 *
 * @author Mymylesaws
 */
public class BackGroundRenderer {
    public static int id = 0;
    public static int id2 = 1;
    private static final Shader sha = new Shader("Waves.shader");

    /**
     * Start Render BackGround
     */
    private static float currentX;
    private static float currentY;

    public static void render() {
        //Shader
        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int var141 = sr.getScaledWidth();
        int var151 = sr.getScaledHeight();
        int mouseX = Mouse.getX() * var141 / Minecraft.getMinecraft().displayWidth;
        int mouseY = var151 - Mouse.getY() * var151 / Minecraft.getMinecraft().displayHeight - 1;

        float xDiff = ((mouseX - sr.getScaledWidth() / 2f) - currentX) / sr.getScaleFactor();
        float yDiff = ((mouseY - sr.getScaledHeight() / 2f) - currentY) / sr.getScaleFactor();
        currentX += xDiff * 0.3F;
        currentY += yDiff * 0.3F;
        GlStateManager.translate(currentX / 80, currentY / 80, 0);
        if (ClientSetting.shaderBG.getValue() && !Minecraft.getMinecraft().gameSettings.ofFastRender) {
            sha.startShader();
            sha.stopShader();
        } else {
            //Picture
            RenderUtil.drawRect(-10,-10, sr.getScaledWidth() + 10, sr.getScaledHeight() + 10, Color.BLACK.getRGB());
            String bg = ((ClientSetting.backgrounds) ClientSetting.backGround.getValue()).getFileName();
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            RenderUtil.drawImage(new ResourceLocation("Distance/BACKGROUND/" + bg), -5, -5, (new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() + 15), (int) ((new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() + 15) / 1.74));
        }
        GL11.glPopMatrix();
    }
}
