package net.minecraft.client.gui;

import my.distance.Client;
import my.distance.fastuni.FastUniFontRenderer;
import my.distance.util.SuperLib;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiButton extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;
    private double animation;

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.width = 200;
        this.height = 20;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.animation = 0.10000000149011612D;
    }

    protected int getHoverState(boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }

        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FastUniFontRenderer fr = Client.FontLoaders.Chinese16;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            this.animation = AnimationUtil.moveUD(this.animation, (this.hovered ? 0.3F : 0.1F), 10f / Minecraft.getDebugFPS(),4f / Minecraft.getDebugFPS());
            if (this.enabled) {
                RenderUtil.drawGradientSideways((float)this.xPosition, (float)(this.yPosition + this.height) - 1.5F, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height), SuperLib.reAlpha(new Color(10, 90, 205).getRGB(), 0.95F),SuperLib.reAlpha(new Color(1, 190, 206).getRGB(),0.95F));
            }else {
                //this.displayString = "\247o" + this.displayString;
                RenderUtil.drawGradientSideways((float)this.xPosition, (float)(this.yPosition + this.height) - 1.5F, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height), SuperLib.reAlpha(new Color(255, 10, 10).getRGB(), 0.95F),SuperLib.reAlpha(new Color(255, 111, 0).getRGB(),0.95F));
            }
            RenderUtil.drawRect((float)this.xPosition, (float)this.yPosition, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height) - 1.5F, new Color(0,0,0,180).getRGB());
            if (this.enabled) {
                RenderUtil.drawRect((float)this.xPosition, (float)this.yPosition, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height), SuperLib.reAlpha(new Color(225, 225, 225).getRGB(), (float)this.animation));
            } else {
                RenderUtil.drawRect((float)this.xPosition, (float)this.yPosition, (float)(this.xPosition + this.width), (float)(this.yPosition + this.height), SuperLib.reAlpha(new Color(255, 255, 255).getRGB(), 0.1F));
            }

            this.mouseDragged(mc, mouseX, mouseY);

            fr.drawStringWithShadow(this.displayString, (float)this.xPosition + ((float)this.width - fr.getStringWidth(SuperLib.removeColorCode(this.displayString)) + 2.0F) / 2.0F, (float)(this.yPosition + (this.height - 8) / 2) + 1.5f, -1);
        }

    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
