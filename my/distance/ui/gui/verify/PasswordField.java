package my.distance.ui.gui.verify;

import my.distance.util.anim.AnimationUtils;
import my.distance.util.render.RenderUtil;
import my.distance.util.sound.SoundFxPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;

public class PasswordField
extends Gui {
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private String text = "";
    private int maxStringLength = 50;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    public boolean isFocused = false;
    private boolean isEnabled = true;
    private int i = 0;
    private int cursorPosition = 0;
    private int selectionEnd = 0;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private boolean b = true;

    public PasswordField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
        this.fontRenderer = par1FontRenderer;
        this.xPos = par2;
        this.yPos = par3;
        this.width = par4;
        this.height = par5;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String par1Str) {
        this.text = par1Str.length() > this.maxStringLength ? par1Str.substring(0, this.maxStringLength) : par1Str;
        this.setCursorPositionEnd();
    }

    public String getText() {
        String newtext = this.text.replaceAll(" ", "");
        return newtext;
    }

    public String getSelectedtext() {
        int var1 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(var1, var2);
    }

    public void writeText(String par1Str) {
        int var8;
        String var2 = "";
        String var3 = ChatAllowedCharacters.filterAllowedCharacters(par1Str);
        int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int var6 = this.maxStringLength - this.text.length() - (var4 - this.selectionEnd);
        boolean var7 = false;
        if (this.text.length() > 0) {
            var2 = String.valueOf(String.valueOf(var2)) + this.text.substring(0, var4);
        }
        if (var6 < var3.length()) {
            var2 = String.valueOf(String.valueOf(var2)) + var3.substring(0, var6);
            var8 = var6;
        } else {
            var2 = String.valueOf(String.valueOf(var2)) + var3;
            var8 = var3.length();
        }
        if (this.text.length() > 0 && var5 < this.text.length()) {
            var2 = String.valueOf(String.valueOf(var2)) + this.text.substring(var5);
        }
        this.text = var2.replaceAll(" ", "");
        this.cursorPos(var4 - this.selectionEnd + var8);
    }

    public void func_73779_a(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int par1) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean var2 = par1 < 0;
                int var3 = var2 ? this.cursorPosition + par1 : this.cursorPosition;
                int var4 = var2 ? this.cursorPosition : this.cursorPosition + par1;
                String var5 = "";
                if (var3 >= 0) {
                    var5 = this.text.substring(0, var3);
                }
                if (var4 < this.text.length()) {
                    var5 = String.valueOf(String.valueOf(var5)) + this.text.substring(var4);
                }
                this.text = var5;
                if (var2) {
                    this.cursorPos(par1);
                }
            }
        }
    }

    public int getNthWordFromCursor(int par1) {
        return this.getNthWordFromPos(par1, this.getCursorPosition());
    }

    public int getNthWordFromPos(int par1, int par2) {
        return this.type(par1, this.getCursorPosition(), true);
    }

    public int type(int par1, int par2, boolean par3) {
        int var4 = par2;
        boolean var5 = par1 < 0;
        int var6 = Math.abs(par1);
        for (int var7 = 0; var7 < var6; ++var7) {
            if (!var5) {
                int var8 = this.text.length();
                if ((var4 = this.text.indexOf(32, var4)) == -1) {
                    var4 = var8;
                    continue;
                }
                while (par3 && var4 < var8 && this.text.charAt(var4) == ' ') {
                    ++var4;
                }
                continue;
            }
            while (par3 && var4 > 0 && this.text.charAt(var4 - 1) == ' ') {
                --var4;
            }
            while (var4 > 0 && this.text.charAt(var4 - 1) != ' ') {
                --var4;
            }
        }
        return var4;
    }

    public void cursorPos(int par1) {
        this.setCursorPosition(this.selectionEnd + par1);
    }

    public void setCursorPosition(int par1) {
        this.cursorPosition = par1;
        int var2 = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }
        if (this.cursorPosition > var2) {
            this.cursorPosition = var2;
        }
        this.func_73800_i(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char par1, int par2) {
        if (!this.isEnabled || !this.isFocused) {
            return false;
        }
        switch (par1) {
            case '\u0001': {
                this.setCursorPositionEnd();
                this.func_73800_i(0);
                return true;
            }
            case '\u0003': {
                GuiScreen.setClipboardString(this.getSelectedtext());
                return true;
            }
            case '\u0016': {
                this.writeText(GuiScreen.getClipboardString());
                return true;
            }
            case '\u0018': {
                GuiScreen.setClipboardString(this.getSelectedtext());
                this.writeText("");
                return true;
            }
        }
        switch (par2) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyDelete,-10);
                    this.func_73779_a(-1);
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyDelete,-10);
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.func_73800_i(0);
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                        this.func_73800_i(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                        this.func_73800_i(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.cursorPos(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                        this.func_73800_i(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                        this.func_73800_i(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.cursorPos(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.func_73800_i(this.text.length());
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyMovement,-10);

                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlKeyDown()) {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyDelete,-10);

                    this.func_73779_a(1);
                } else {
                    new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyDelete,-10);

                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
            SoundFxPlayer.playRandomKeySound();

            this.writeText(Character.toString(par1));
            return true;
        }
        return false;
    }

    public void mouseClicked(int par1, int par2, int par3) {
        boolean var4;
        boolean bl = var4 = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;
        if (this.canLoseFocus) {
            this.setFocused(this.isEnabled && var4);
        }
        if (this.isFocused && par3 == 0) {
            int var5 = par1 - this.xPos;
            if (this.enableBackgroundDrawing) {
                var5 -= 4;
            }
            String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.i);
        }
    }

    double anim = 0;
    public void drawTextBox() {
        if (this.isFocused){
            anim = AnimationUtils.animate(0,anim,10f /  Minecraft.getDebugFPS());
        }else {
            anim = AnimationUtils.animate((width/2d ) + 1,anim, 10f / Minecraft.getDebugFPS());
        }
        if (this.func_73778_q()) {
            if (this.getEnableBackgroundDrawing()) {
                RenderUtil.drawFastRoundedRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1,1, new Color(255,255,255, 150).getRGB());
                //Gui.drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
                RenderUtil.drawRect(this.xPos + anim - 1, this.yPos + height - 0.3f, this.xPos - anim + this.width + 1, this.yPos + this.height + 1, new Color(0, 196, 255, 255).getRGB());
            }
            int var1 = new Color(1,1,1).getRGB();
            int var2 = this.cursorPosition - this.i;
            int var3 = this.selectionEnd - this.i;
            String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), this.getWidth());
            boolean var5 = var2 >= 0 && var2 <= var4.length();
            boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
            int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
            int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
            float var9 = var7;
            if (var3 > var4.length()) {
                var3 = var4.length();
            }
            if (var4.length() > 0) {
                if (var5) {
                    var4.substring(0, var2);
                }
                var9 = Minecraft.getMinecraft().fontRendererObj.drawString(this.text.replaceAll("(?s).", "*"), var7, var8, var1);
            }
            boolean var10 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float var11 = var9;
            if (!var5) {
                var11 = var2 > 0 ? var7 + this.width : var7;
            } else if (var10) {
                var11 = var9 - 1.0f;
                var9 -= 1.0f;
            }
            if (var4.length() > 0 && var5 && var2 < var4.length()) {
                Minecraft.getMinecraft().fontRendererObj.drawString(var4.substring(var2), (int) var9, var8, var1);
            }
            if (var6) {
                if (var10) {
                    Gui.drawRect(var11, (float)(var8 - 1), var11 + 1.0f, (float)(var8 + 1 + this.fontRenderer.FONT_HEIGHT), -3092272);
                } else {
                    Minecraft.getMinecraft().fontRendererObj.drawString("_", (int) var11, var8, var1);
                }
            }
            if (var3 != var2) {
                int var12 = var7 + this.fontRenderer.getStringWidth(var4.substring(0, var3));
                this.drawCursorVertical((int)var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_)
    {
        if (p_146188_1_ < p_146188_3_)
        {
            int i = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i;
        }

        if (p_146188_2_ < p_146188_4_)
        {
            int j = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = j;
        }

        if (p_146188_3_ > this.xPos + this.width)
        {
            p_146188_3_ = this.xPos + this.width;
        }

        if (p_146188_1_ > this.xPos + this.width)
        {
            p_146188_1_ = this.xPos + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)p_146188_1_, (double)p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos((double)p_146188_3_, (double)p_146188_4_, 0.0D).endVertex();
        worldrenderer.pos((double)p_146188_3_, (double)p_146188_2_, 0.0D).endVertex();
        worldrenderer.pos((double)p_146188_1_, (double)p_146188_2_, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int par1) {
        this.maxStringLength = par1;
        if (this.text.length() > par1) {
            this.text = this.text.substring(0, par1);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean par1) {
        this.enableBackgroundDrawing = par1;
    }

    public void func_73794_g(int par1) {
        this.enabledColor = par1;
    }

    public void setFocused(boolean par1) {
        if (par1 && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = par1;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void func_73800_i(int par1) {
        int var2 = this.text.length();
        if (par1 > var2) {
            par1 = var2;
        }
        if (par1 < 0) {
            par1 = 0;
        }
        this.selectionEnd = par1;
        if (this.fontRenderer != null) {
            if (this.i > var2) {
                this.i = var2;
            }
            int var3 = this.getWidth();
            String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), var3);
            int var5 = var4.length() + this.i;
            if (par1 == this.i) {
                this.i -= this.fontRenderer.trimStringToWidth(this.text, var3, true).length();
            }
            if (par1 > var5) {
                this.i += par1 - var5;
            } else if (par1 <= this.i) {
                this.i -= this.i - par1;
            }
            if (this.i < 0) {
                this.i = 0;
            }
            if (this.i > var2) {
                this.i = var2;
            }
        }
    }

    public void setCanLoseFocus(boolean par1) {
        this.canLoseFocus = par1;
    }

    public boolean func_73778_q() {
        return this.b;
    }

    public void func_73790_e(boolean par1) {
        this.b = par1;
    }
}

