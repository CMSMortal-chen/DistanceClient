package my.distance.ui.gui;

import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.Client;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.ui.gui.mainmenu.GuiMainMenu;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.ClientSetting;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.buttons.SimpleButton;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.render.Blur;
import my.distance.util.render.RenderUtil;
import my.distance.util.render.gl.GLUtils;
import my.distance.util.time.TimerUtil;
import my.distance.fastuni.FastUniFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.main.Main;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class GuiClientSetting extends GuiScreen {
    public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    public static Module currentModule = ModuleManager.getModuleByClass(ClientSetting.class);
    public static float startX = sr.getScaledWidth() / 2f - 450 / 2f, startY = sr.getScaledHeight() / 2f - 350 / 2f;
    public static int moduleStart = 0;
    public static int valueStart = 0;
    boolean previousmouse = true;
    boolean exiting = false;
    private static final TimerUtil timer = new TimerUtil();
    boolean mouse;
    public float moveX = 0, moveY = 0;
    boolean bind = false;
    float hue;
    public static int alpha;

    boolean needAnim;

    boolean rev = false;
    double anim,anim2,anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();

    GuiScreen prevGuiScreen;

    public GuiClientSetting(GuiScreen prev,boolean needAnim){
        prevGuiScreen = prev;
        this.needAnim = needAnim;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!timer.hasReached(500)) {
            anim = anim2 = anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
            rev = true;
        }else if (!exiting){
            rev = false;
        }
        if (rev) {
            anim = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim, 6.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim2, 4.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim3, 5.5f / Minecraft.getDebugFPS());
            if (anim2 >= width - 5 && timer.hasReached(510)) {
                if (needAnim){
                    mc.displayGuiScreen(new GuiMainMenu(true));
                }else {
                    mc.displayGuiScreen(prevGuiScreen);
                }
            }
        } else {
            anim = AnimationUtils.animate(0, anim, 3.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(0, anim2, 5.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(0, anim3, 4.5f / Minecraft.getDebugFPS());
        }
        drawDefaultBackground();
        sr = new ScaledResolution(mc);
        alpha = 255;
        if (this.hue > 255.0f) {
            this.hue = 0.0f;
        }
        int color4 = Client.getClientColor(alpha);
        this.hue = (float)((double)this.hue + 0.1);
        Blur.blurAreaBoarderXY(startX, startY, startX + (float)450, startY + (float)350);
        RenderUtil.drawRect(startX, startY, startX + (float)450, startY + (float)350,  new Color(0, 0, 0, 166).getRGB());
        RenderUtil.drawRect(startX, startY, startX + (float)450, startY + 1,  new Color(0, 174, 255, 255).getRGB());

        FontLoaders.calibrilite32.drawCenteredString(Client.name, startX + 47, startY + 21, new Color(255,255,255, alpha).getRGB());
        FontLoaders.calibrilite18.drawCenteredString((Main.isbeta? "Beta ":"V") + Client.version, startX + 47 + FontLoaders.calibrilite32.getStringWidth(Client.name) - 14 + (Main.isbeta? 6:0), startY + 25, color4);
        FontLoaders.calibrilite18.drawCenteredString("ClientSetting", startX + 47 + FontLoaders.calibrilite32.getStringWidth(Client.name) - 50 + (Main.isbeta? 6:0), startY + 36, color4);

        Client.FontLoaders.Chinese15.drawString(Client.words, (int)startX + 15, (int)startY + 332, new Color(180,180,180,alpha).getRGB());
        int m = Mouse.getDWheel();
        if (this.isCategoryHovered(startX + 200, startY + 50, startX + 430, startY + 315, mouseX, mouseY)) {
            if (m < 0 && valueStart < currentModule.getValues().size() - 1) {
                valueStart++;
            }
            if (m > 0 && valueStart > 0) {
                valueStart--;
            }
        }
        float mY;

        mY = startY + 12;
        FastUniFontRenderer font = Client.FontLoaders.GoogleSans16;
        for (int i = 0; i < currentModule.getValues().size(); i++) {
            if (mY > startY + 220)
                break;
            if (i < valueStart) {
                continue;
            }
            Value value = currentModule.getValues().get(i);
            if (value instanceof Numbers) {
                float x = startX + 320;
                double render = (double) (74.0F
                        * (((Number) value.getValue()).floatValue() - ((Numbers) value).getMinimum().floatValue())
                        / (((Numbers) value).getMaximum().floatValue()
                        - ((Numbers) value).getMinimum().floatValue()));
                RenderUtil.drawFastRoundedRect(x - 35, mY + 53,  x + 75 - 35, mY + 56,
                        1,isButtonHovered(x - 35, mY + 53,  x + 75 - 35, mY + 56, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :(new Color(30, 30, 30, alpha)).getRGB());
                RenderUtil.drawFastRoundedRect((int) x - 35, mY + 53, (int) ((double) x + render + 1 - 35), mY + 56, 1,Client.getBlueColor());
                //Gui.drawFilledCircle((float) ((double) x + render + 2D) + 3, mY + 53, 1.5, Client.getBlueColor(), 5);
                font.drawStringWithShadow(value.getName(), startX + 100, mY + 50+3,
                        new Color(255,255,255, alpha).getRGB());
                font.drawStringWithShadow(value.getValue().toString(), x - 38 - font.getStringWidth(value.getValue().toString()), mY + 50f+2.5f,
                        new Color(255, 255, 255, alpha).getRGB());
                if (!Mouse.isButtonDown((int) 0)) {
                    this.previousmouse = false;
                }
                if (this.isButtonHovered(x - 35, mY + 53,  x + 75 - 35, mY + 56, mouseX, mouseY)
                        && Mouse.isButtonDown((int) 0)) {
                    if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
                        render = ((Numbers) value).getMinimum().doubleValue();
                        double max = ((Numbers) value).getMaximum().doubleValue();
                        double inc = ((Numbers) value).getIncrement().doubleValue();
                        double valAbs = (double) (mouseX) - ((double) x + 1.0D - 35);
                        double perc = valAbs / 68.0D;
                        perc = Math.min(Math.max(0.0D, perc), 1.0D);
                        double valRel = (max - render) * perc;
                        double val = render + valRel;
                        val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                        value.setValue(val);
                    }
                    if (!Mouse.isButtonDown((int) 0)) {
                        this.previousmouse = false;
                    }
                }
            }
            if (value instanceof Option) {
                float x = startX + 270;
                int xx = 65;
                int x2x = 65;
                ((Option) value).anim = AnimationUtil.moveUD(((Option) value).anim,(boolean)value.getValue()? 5f:0f,18f/Minecraft.getDebugFPS(),7f/Minecraft.getDebugFPS());
                font.drawStringWithShadow(value.getName(), startX + 100, mY + 50 + 3,
                        new Color(255,255,255, alpha).getRGB());
                GLUtils.startSmooth();
                //Gui.drawFilledCircle(x + xx, mY + 54.5, 4.5, isCheckBoxHovered(x + xx - 5, mY + 50, x + x2x + 6, mY + 59, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(20, 20, 20, alpha).getRGB(), 10);
                //Gui.drawFilledCircle(x + x2x, mY + 54.5, 4.5, isCheckBoxHovered(x + xx - 5, mY + 50, x + x2x + 6, mY + 59, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(20, 20, 20, alpha).getRGB(), 10);
                Gui.drawFilledCircle(x + x2x, mY + 54.5, 5 - ((Option) value).anim, new Color(56,56,56).getRGB(), 10);
                Gui.drawFilledCircle(x + x2x, mY + 54.5, ((Option) value).anim, Client.getBlueColor(), 10);
                GLUtils.endSmooth();
                if (this.isCheckBoxHovered(x + xx - 5, mY + 50, x + x2x + 6, mY + 59, mouseX, mouseY)) {
                    if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
                        this.previousmouse = true;
                        this.mouse = true;
                    }

                    if (this.mouse) {
                        value.setValue(!(boolean) value.getValue());
                        this.mouse = false;
                    }
                }
                if (!Mouse.isButtonDown((int) 0)) {
                    this.previousmouse = false;
                }
            }
            if (value instanceof Mode) {
                float x = startX + 320;
                font.drawStringWithShadow(value.getName(), startX + 100, mY + 52 + 3,
                        new Color(255,255,255, alpha).getRGB());

                RenderUtil.drawRoundedRect(x- 35, mY + 45, x + 75- 35, mY + 65,3, new Color(30, 30, 30).getRGB());

                RenderUtil.drawRoundedRect(x - 35,mY + 45,x + 13 - 35,mY + 65,1,isStringHovered(x- 35,mY + 45,x + 13- 35,mY + 65, mouseX, mouseY) ? new Color (101, 175,255).getRGB() :new Color(0,141,255).getRGB());
                font.drawCenteredString("<",x + 15f/ 2f- 35,mY + 53 + 1,new Color (255,255,255,alpha).getRGB());

                RenderUtil.drawRoundedRect(x + 75 - 13- 35, mY + 45, x + 75- 35, mY + 65,1, isStringHovered(x + 75 - 13- 35, mY + 45, x + 75- 35, mY + 65, mouseX, mouseY) ? new Color (101, 175,255).getRGB() :new Color(0,141,255).getRGB());
                font.drawCenteredString(">",x + 75- 35 - (11f / 2f),mY + 53 + 1,new Color (255,255,255,alpha).getRGB());

                FontLoaders.GoogleSans14.drawCenteredStringWithShadow((((Enum)value.getValue()).ordinal() + 1)+"/"+(((Mode) value).getModes().length),
                        (x), mY + 53 + 9, new Color (255,255,255,130).getRGB());

                font.drawCenteredStringWithShadow(((Mode) value).getModeAsString(),
                        (float) (x ), mY + 53 + 1, new Color (255,255,255).getRGB());

                if (this.isStringHovered(x- 35,mY + 45,x + 13- 35,mY + 65, mouseX, mouseY)) {
                    if (Mouse.isButtonDown((int) 0) && !this.previousmouse) {
                        Enum current = (Enum) value.getValue();
                        int next = current.ordinal() - 1 <= -1 ? ((Mode) value).getModes().length - 1
                                : current.ordinal() - 1;
                        value.setValue(((Mode) value).getModes()[next]);
                        this.previousmouse = true;
                    }

                }
                if (this.isStringHovered(x + 75 - 13- 35, mY + 45, x + 75- 35, mY + 65, mouseX, mouseY)) {
                    if (Mouse.isButtonDown((int) 0) && !this.previousmouse) {
                        Enum current = (Enum) value.getValue();
                        int next = current.ordinal() + 1 >= ((Mode) value).getModes().length ? 0
                                : current.ordinal() + 1;
                        value.setValue(((Mode) value).getModes()[next]);
                        this.previousmouse = true;
                    }

                }
            }
            mY += 25;
        }
        float x = startX + 320;
        float yyy = startY + 240;
        if ((isHovered(startX, startY, startX + 450, startY + 50, mouseX, mouseY) || isHovered(startX,startY + 315,startX + 450, startY + 350, mouseX,mouseY) || isHovered(startX + 430,startY,startX + 450, startY + 350, mouseX,mouseY)) && Mouse.isButtonDown(0)) {
            if (moveX == 0 && moveY == 0) {
                moveX = mouseX - startX;
                moveY = mouseY - startY;
            } else {
                startX = mouseX - moveX;
                startY = mouseY - moveY;
            }
            this.previousmouse = true;
        } else if (moveX != 0 || moveY != 0) {
            moveX = 0;
            moveY = 0;
        }
        super.drawScreen(mouseX,mouseY,partialTicks);

        RenderUtil.drawRect(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());

    }

    public void initGui() {
        timer.reset();
        this.buttonList.add(new SimpleButton(0, this.width / 2, (height) - 10, "<- GoBack"));

        for (int i = 0; i < currentModule.getValues().size(); i++) {
            Value value = currentModule.getValues().get(i);
//            if (value instanceof Option) {
//                ((Option) value).anim = 55;
//            }
        }

        super.initGui();
    }
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            rev = true;
            exiting = true;

        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            rev = true;
            exiting = true;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        float x = startX + 220;
        float mY = startY + 30;
        for (int i = 0; i < currentModule.getValues().size(); i++) {
            if (mY > startY + 350)
                break;
            if (i < valueStart) {
                continue;
            }
            Value value = currentModule.getValues().get(i);
            if (value instanceof Numbers) {
                mY += 20;
            }
            if (value instanceof Option) {

                mY += 20;
            }
            if (value instanceof Mode) {

                mY += 25;
            }
        }
        float x1 = startX + 320;
        float yyy = startY + 240;
        if (isHovered(x1 + 2, yyy + 45, x1 + 78, yyy + 65, mouseX, mouseY)) {
            this.bind = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
            return true;
        }

        return false;
    }

    @Override
    public void onGuiClosed() {
        alpha = 0;
    }
    public boolean doesGuiPauseGame() {
        return false;
    }
}
