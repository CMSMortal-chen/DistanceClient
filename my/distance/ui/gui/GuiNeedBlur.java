package my.distance.ui.gui;

import LemonObfAnnotation.ObfuscationClass;
import my.distance.Client;
import my.distance.manager.FileManager;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.gui.verify.GuiLogin;
import my.distance.ui.jelloparticle.ParticleEngine;
import my.distance.ui.buttons.UIFlatButton;
import my.distance.util.ClientSetting;
import my.distance.fastuni.FontLoader;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.List;

@ObfuscationClass
public class GuiNeedBlur extends GuiScreen {
    public ParticleEngine pe = new ParticleEngine();
    public GuiButton yesButton;
    public GuiButton noButton;
    private static boolean isBlurEnabled;

    @Override
    public void initGui(){
        int h = new ScaledResolution(this.mc).getScaledHeight();
        int w = new ScaledResolution(this.mc).getScaledWidth();
        this.yesButton = new UIFlatButton(1, (int) (w / 2f) - 20 - 25, (int) (h / 2f) + 15, 40, 20, "是的", new Color(25,25,25).getRGB());
        this.noButton = new UIFlatButton(3, (int) (w / 2f) - 20 + 25, (int) (h / 2f) + 15, 40, 20, "不要", new Color(25,25,25).getRGB());
        this.buttonList.add(this.yesButton);
        this.buttonList.add(this.noButton);
    }

    @Override
    protected void keyTyped(char var1, int var2) {

    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                isBlurEnabled = true;
                FileManager.save("NeedBlur.txt","true",false);
                ClientSetting.enableBlur.setValue(true);
                mc.displayGuiScreen(new GuiLogin());
                break;
            case 3:
                isBlurEnabled = false;
                FileManager.save("NeedBlur.txt","false",false);
                ClientSetting.enableBlur.setValue(false);
                mc.displayGuiScreen(new GuiLogin());
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int h = new ScaledResolution(this.mc).getScaledHeight();
        int w = new ScaledResolution(this.mc).getScaledWidth();

        GlStateManager.translate(0, 0, 0);
        Gui.drawRect(-20, -20, w + 20, h + 20, new Color(0, 0, 0).getRGB());
        RenderUtil.drawRect(w / 2f - 30, h / 2f + 9, w / 2f - 30 + 60, h / 2f + 10, new Color(255, 255, 255).getRGB());

        if (isBlurConfiged()) {
            isBlurEnabled = setBlurEnabled();
            mc.displayGuiScreen(new GuiLogin());
            return;
        }
        pe.render(0, 0);
        Client.FontLoaders.Chinese18.drawCenteredString("启用模糊效果?", w / 2f, h / 2f - 25 + 4, -1);
        //(部分电脑不支持模糊效果会导致黑屏)
        FontLoader.msFont13.drawCenteredString("部分电脑不支持模糊效果会导致黑屏，如果您不清楚是否支持本效果请不要开启", w / 2f, h / 2f - 15 + 4, -1);
        FontLoader.msFont13.drawCenteredString("如果还是黑屏请删除\".minecraft/Distance/NeedBlur.txt\"来再次访问本界面", w / 2f, h / 2f - 7 + 4, -1);

        FontLoaders.GoogleSans16.drawCenteredString(Client.name+" made by \u00a7oMymylesaws\u00a7r & \u00a7oFunkNight\u00a7r ("+Client.author+")", width / 2f, height - FontLoaders.GoogleSans16.getHeight() - 6f, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    private boolean setBlurEnabled() {
        List<String> names = FileManager.read("NeedBlur.txt");
        for (String v : names) {
            return v.contains("true");
        }
        return false;
    }
    public static boolean isBlurEnabled() {
        return ClientSetting.enableBlur.get();
    }
    private boolean isBlurConfiged(){
        List<String> names = FileManager.read("NeedBlur.txt");
        return !names.isEmpty();
    }
}
