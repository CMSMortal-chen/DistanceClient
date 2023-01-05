package my.distance.ui.gui.mainmenu.animation;

import my.distance.fastuni.FastUniFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.util.anim.AnimationUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class AnimatedButton {
    public double posX;
    public double posY;
    public double targetPosX;
    public double targetPosY;

    public double hoverAnim = 0;
    public double hoverAnim2 = 0;
    public double animColor = 1;

    public boolean isIntroPlayed;
    private final FastUniFontRenderer font;

    public String text;
    public String icons;
    public Runnable action;
    private boolean isRunning = false;

    public AnimatedButton(FastUniFontRenderer font, String text,String icons,double posX, double posY,Runnable action) {
        this.posX = posX;
        this.posY = posY;
        this.targetPosX = posX;
        this.targetPosY = posY;
        this.font = font;
        this.action = action;
        this.text = text;
        this.icons = icons;
    }

    public void updateAndDraw(int mouseX, int mouseY) {
        if (isIntroPlayed) {
            if (posX != targetPosX) {
                posX = AnimationUtil.moveUD(posX, targetPosX, 10f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
            }
            if (posY != targetPosY) {
                posY = AnimationUtil.moveUD(posY, targetPosY, 10f / Minecraft.getDebugFPS(), 6f / Minecraft.getDebugFPS());
            }
            animColor = AnimationUtil.moveUD(animColor, 255, 10f / Minecraft.getDebugFPS(), 6f / Minecraft.getDebugFPS());
            hoverAnim = AnimationUtil.moveUD(hoverAnim, isHovered(mouseX, mouseY) ? 10 : 0, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
            if (isHovered(mouseX, mouseY)){
                hoverAnim2 = AnimationUtil.moveUD(hoverAnim2, 255, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
            }else {
                hoverAnim2 = AnimationUtil.moveUD(hoverAnim2, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
            }

            if (hoverAnim2 > 5) {
                FontLoaders.NovICON64.drawString(icons, (float) (posX) - 10f - (icons.equals("A") ? 10 : 0), (float) posY - 20, new Color(190, 190, 190, Math.max((int) hoverAnim2,0)).getRGB());
            }


            font.drawString(text, (float) (posX + hoverAnim), (float) posY, new Color(255, 255, 255, (int) animColor).getRGB());

            if (Mouse.isButtonDown(0) && isHovered(mouseX, mouseY) && !isRunning) {
                isRunning = true;
                action.run();
            }
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= posX && mouseX <= posX + font.getStringWidth(text) && mouseY >= posY - 5 && mouseY <= posY + font.getHeight() + 5;
    }
    public void playIntro() {
        if (!isIntroPlayed) {
            targetPosX = posX - 30;
            isIntroPlayed = true;
        }
    }
    public void playOutro() {
        if (isIntroPlayed) {
            targetPosX = -font.getStringWidth(text) - 2;
        }
    }
}
