package my.distance.ui.gui;

import my.distance.Client;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.jelloparticle.ParticleEngine;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.anim.Translate;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import my.distance.fastuni.FastUniFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.Random;

public class GuiGoodBye extends GuiScreen {
    public ParticleEngine pe = new ParticleEngine();
    Translate translate = new Translate(0, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
    Translate translate2 = new Translate(0, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
    private static final TimerUtil timer = new TimerUtil();
    boolean skiped = false;
    String[] bye2 = {
            "See you next time",
            "再见",
            "さようなら",
    };
    String[] bye = {
            "\"很多人不需要再见,因为只是路过而已。遗忘就是我们给彼此最好的纪念\"",
            "\"首先说再见的那个人,永远占上风\"",
            "\"忘掉岁月，忘掉痛苦,我们永不说再见\"",
            "\"世界很小,城市很大,欠缺缘分的人也许终生也不会再见了\"",
            "\"不曾说挥手再见,却道是友谊长远\"",
            "\"再见的一层含义是重逢,另一层是再也不见\""
    };
    Random r = new Random();
    String bye2r = bye2[r.nextInt(bye2.length)];
    String byer = bye[r.nextInt(bye.length)];
    boolean rev = false;
    double anim,anim2,anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if (mouseButton == 0){
            skiped = true;
        }
    }

    @Override
    public void initGui() {
        timer.reset();
        translate = new Translate(0, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        translate2 = new Translate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!timer.hasReached(500)) {
            anim = anim2 = anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
            rev = true;
        }
        if (rev) {
            anim = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim, (skiped ? 12.0f : 6.0f) / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim2, (skiped ? 8.0f : 4.0f) / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim3, (skiped ? 11.0f : 5.5f) / Minecraft.getDebugFPS());
        } else {
            anim = AnimationUtils.animate(0, anim, (skiped ? 6.0f : 3.0f) / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(0, anim2, (skiped ? 10.0f : 5.0f) / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(0, anim3, (skiped ? 9f : 4.5f) / Minecraft.getDebugFPS());
        }
        ScaledResolution sr = new ScaledResolution(mc);


        FastUniFontRenderer fontwel2 = Client.FontLoaders.Chinese18;
        FastUniFontRenderer fontwel = Client.FontLoaders.Chinese35;
        //if (timer.hasReached(1000)) {
        BackGroundRenderer.render();

        pe.render(0, 0);
        if (!timer.hasReached(3500)) {
            translate.interpolate((float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2 - 3f, 0.14f);
            translate2.interpolate((float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2 + fontwel.FONT_HEIGHT, 0.14f);
        }
        fontwel.drawCenteredStringWithShadow(bye2r, translate.getX(), translate.getY(), new Color(255, 255, 255).getRGB());
        fontwel2.drawCenteredStringWithShadow(byer, translate2.getX(), translate2.getY(), new Color(255, 255, 255).getRGB());

        if (timer.hasReached(3500)) {
            translate.interpolate(0, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() + 5, 0.14f);
            translate2.interpolate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() + 5, 0.14f);

        }

        RenderUtil.drawRect(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());

        if (timer.hasReached(4000) || skiped) {
            rev = true;
            if (anim2 >= width - 5) {
                mc.shutdown();
            }
        } else {
            rev = false;
        }
    }
}
