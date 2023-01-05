package my.distance.module.modules.render;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Numbers;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.util.anim.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

public class Keyrender extends Module {
    double anima;
    double anima2;
    double anima3;
    double anima4;
    double anima5;
    double anima6;
    private final Numbers<Double> x = new Numbers<>("X", "X", 500.0, 1.0, 1920.0, 5.0);
    private final Numbers<Double> y = new Numbers<>("Y", "Y", 2.0, 1.0, 1080.0, 5.0);
    private double rainbowTick;
    public Keyrender() {
        super("KeyRender", new String[]{"Key"}, ModuleType.Render);
        this.addValues(this.x,this.y);
        this.setRemoved(true);
    }
    @EventHandler
    public void onGui(EventRender2D e) {
        CFontRenderer font = FontLoaders.Jello18;
        Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 250.0
                + Math.sin((double) rainbowTick / 100.0 * 1.5)) % 1.0f, 0.8f, 0.9f));
        float xOffset = ((Double)this.x.getValue()).floatValue();
        float yOffset = ((Double)this.y.getValue()).floatValue();
        Gui.drawRect((double)xOffset+26, (double)yOffset, (double)(xOffset + 51), (double)(yOffset +25),new Color(0,0,0,150).getRGB());//w
        Gui.drawRect((double)xOffset+26, (double)yOffset+26, (double)(xOffset +51), (double)(yOffset+51),new Color(0,0,0,150).getRGB());//s
        Gui.drawRect((double)xOffset, (double)yOffset+26, (double)(xOffset +25), (double)(yOffset+51),new Color(0,0,0,150).getRGB());//a
        Gui.drawRect((double)xOffset+52, (double)yOffset+26, (double)(xOffset +77), (double)(yOffset+51),new Color(0,0,0,150).getRGB());//d
        Gui.drawRect((double)xOffset+1+77/2 ,(double)yOffset+52, (double)(xOffset +77), (double)(yOffset+77),new Color(0,0,0,150).getRGB());//LMB
        Gui.drawRect((double)xOffset, (double)yOffset+52, (double)(xOffset +77/2), (double)(yOffset+77),new Color(0,0,0,150).getRGB());//RMB
        if (++rainbowTick > 10000) {
            rainbowTick = 0;
        }
        //w
        if(mc.gameSettings.keyBindForward.pressed) {
            anima = AnimationUtils.animate(150,anima,30.4f / (float) Minecraft.getDebugFPS());
        }else if (this.anima > 0) {
            this.anima=AnimationUtils.animate(0,anima, 7.4f / (float) Minecraft.getDebugFPS());;
        }
        //s
        if(mc.gameSettings.keyBindBack.pressed) {
            anima2 =AnimationUtils.animate(150,anima2,30.4f / (float) Minecraft.getDebugFPS());
        }else if (this.anima2 > 0) {
            this.anima2=AnimationUtils.animate(0,anima2, 7.4f / (float) Minecraft.getDebugFPS());;
        }
        //a
        if(mc.gameSettings.keyBindLeft.pressed) {
            anima3 = AnimationUtils.animate(150,anima3,30.4f / (float) Minecraft.getDebugFPS());
        }else if (this.anima3 > 0) {
            this.anima3=AnimationUtils.animate(0,anima3, 7.4f / (float) Minecraft.getDebugFPS());;
        }
        //d
        if(mc.gameSettings.keyBindRight.pressed) {
            this.anima4 = AnimationUtils.animate(150,anima4,30.4f / (float) Minecraft.getDebugFPS());
        }else if (this.anima4 > 0) {
            this.anima4=AnimationUtils.animate(0,anima4, 7.4f / (float) Minecraft.getDebugFPS());;
        }
        //LMB
        if(Mouse.isButtonDown(1)) {
            this.anima5 = AnimationUtils.animate(150,anima5, 30.4f / (float) Minecraft.getDebugFPS());
        }else if (this.anima5 > 0) {
            this.anima5=AnimationUtils.animate(0,anima5, 7.4f / (float) Minecraft.getDebugFPS());
        }
        //RMB
        if(Mouse.isButtonDown(0)) {
            this.anima6 = AnimationUtils.animate(150,anima6, 1.0);
        }else if (this.anima6 > 0) {
            this.anima6=AnimationUtils.animate(0,anima6, 7.4f / (float) Minecraft.getDebugFPS());;
        }
        Gui.drawRect((double)xOffset+26, (double)yOffset+25, (double)(xOffset + 51), (double)(yOffset +25-25),new Color(255,255,255,(int)anima).getRGB());//w
        Gui.drawRect((double)xOffset+26, (double)yOffset+51, (double)(xOffset +51), (double)(yOffset+51-25),new Color(255,255,255,(int)anima2).getRGB());//s
        Gui.drawRect((double)xOffset, (double)yOffset+51, (double)(xOffset +25), (double)(yOffset+51-25),new Color(255,255,255,(int)anima3).getRGB());//a
        Gui.drawRect((double)xOffset+52, (double)yOffset+51, (double)(xOffset +77), (double)(yOffset+51-25),new Color(255,255,255,(int)anima4).getRGB());//d
        Gui.drawRect((double)xOffset+1+77/2 ,(double)yOffset+77, (double)(xOffset +77), (double)(yOffset+77-25),new Color(255,255,255,(int)anima5).getRGB());//LMB
        Gui.drawRect((double)xOffset, (double)yOffset+77, (double)(xOffset +77/2), (double)(yOffset+77-25),new Color(255,255,255,(int)anima6).getRGB());//RMB

        Gui.drawRect((double)xOffset+26, (double)yOffset+25, (double)(xOffset + 51), (double)(yOffset +25-25),new Color(255,255,255,(int)anima).getRGB());//w
        Gui.drawRect((double)xOffset+26, (double)yOffset+51, (double)(xOffset +51), (double)(yOffset+51-25),new Color(255,255,255,(int)anima2).getRGB());//s
        Gui.drawRect((double)xOffset, (double)yOffset+51, (double)(xOffset +25), (double)(yOffset+51-25),new Color(255,255,255,(int)anima3).getRGB());//a
        Gui.drawRect((double)xOffset+52, (double)yOffset+51, (double)(xOffset +77), (double)(yOffset+51-25),new Color(255,255,255,(int)anima4).getRGB());//d
        Gui.drawRect((double)xOffset+1+77/2f ,(double)yOffset+77, (double)(xOffset +77), (double)(yOffset+77-25),new Color(255,255,255,(int)anima5).getRGB());//LMB
        Gui.drawRect((double)xOffset, (double)yOffset+77, (double)(xOffset +77/2), (double)(yOffset+77-25),new Color(255,255,255,(int)anima6).getRGB());//RMB

        font.drawString("W", xOffset+(float)34.5, yOffset+9, new Color((1f-(float) anima / 150f),(1f-(float) anima / 150f),(1f-(float) anima / 150f)).getRGB());
        font.drawString("S", xOffset+(float)36, yOffset+35, new Color((1f - (float) anima2 / 150f),(1f - (float) anima2 / 150f),(1f - (float) anima2 / 150f)).getRGB());
        font.drawString("A", xOffset+(float)10, yOffset+35, new Color((1f - (float) anima3/150f),(1f - (float) anima3/150f),(1f - (float) anima3/150f)).getRGB());
        font.drawString("D", xOffset+(float)62, yOffset+35, new Color((1f - (float) anima4/150f),(1f - (float) anima4/150f),(1f - (float) anima4/150f)).getRGB());
        font.drawString("LMB", xOffset+(float)10, yOffset+61, new Color((1f - (float) anima6/150f),(1f - (float) anima6/150f),(1f - (float) anima6/150f)).getRGB()) ;
        font.drawString("RMB", xOffset+(float)49, yOffset+61, new Color((1f - (float) anima5/150f),(1f - (float) anima5/150f),(1f - (float) anima5/150f)).getRGB());
    }
    public void onDisable() {
        this.anima=0;
        this.anima2=0;
        this.anima3=0;
        this.anima4=0;
        this.anima5=0;
        this.anima6=0;
        super.onDisable();
    }
    public void onEnable() {
        super.isEnabled();
    }

}
