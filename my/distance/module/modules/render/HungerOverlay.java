package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.FoodValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class HungerOverlay extends Module {
    float flashAlpha = 0f;
    byte alphaDir = 1;

    public HungerOverlay() {
        super("HungerOverlay", new String[]{"applecore", "appleskin"}, ModuleType.Render);
    }

    @EventHandler
    public void onRender2d(EventRender2D e) {
        if (mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack heldItem = player.getHeldItem();
        FoodStats stats = player.getFoodStats();

        ScaledResolution scale = new ScaledResolution(mc);

        int left = scale.getScaledWidth() / 2 + 91;
        int top = (mc.ingameGUI.getChatGUI().getChatOpen() ? scale.getScaledHeight() - 36
                : scale.getScaledHeight() - 22) - 17;
        // saturation overlay
        HungerOverlay.drawExhaustionOverlay(FoodValues.realFoodExhaustionLevel, mc, left, top, 0.9f);

        drawSaturationOverlay(0, stats.getSaturationLevel(), mc, left, top, 0.9f);

        if (heldItem == null || !(heldItem.getItem() instanceof ItemFood)) {
            flashAlpha = 0;
            alphaDir = 1;
            return;
        }

        // restored hunger/saturation overlay while holding food
        FoodValues foodValues = FoodValues.get(heldItem, player);
        drawHungerOverlay(foodValues.hunger, stats.getFoodLevel(), mc, left, top, flashAlpha);
        int newFoodValue = stats.getFoodLevel() + foodValues.hunger;
        float newSaturationValue = stats.getSaturationLevel() + foodValues.getSaturationIncrement();
        drawSaturationOverlay(newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodValues.getSaturationIncrement(), stats.getSaturationLevel(), mc, left, top, flashAlpha);
    }

    private static final ResourceLocation modIcons = new ResourceLocation("AppleCore/icons.png");

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel, Minecraft mc, int left, int top, float alpha) {
        if (saturationLevel + saturationGained < 0)
            return;

        int startBar = saturationGained != 0 ? Math.max(0, (int) saturationLevel / 2) : 0;
        int endBar = (int) Math.ceil(Math.min(20, saturationLevel + saturationGained) / 2f);
        int barsNeeded = endBar - startBar;
        mc.getTextureManager().bindTexture(modIcons);

        enableAlpha(alpha);
        for (int i = startBar; i < startBar + barsNeeded; ++i) {
            int x = left - i * 8 - 9;
            float effectiveSaturationOfBar = (saturationLevel + saturationGained) / 2 - i;

            if (effectiveSaturationOfBar >= 1)
                mc.ingameGUI.drawTexturedModalRect(x, top, 27, 0, 9, 9);
            else if (effectiveSaturationOfBar > .5)
                mc.ingameGUI.drawTexturedModalRect(x, top, 18, 0, 9, 9);
            else if (effectiveSaturationOfBar > .25)
                mc.ingameGUI.drawTexturedModalRect(x, top, 9, 0, 9, 9);
            else if (effectiveSaturationOfBar > 0)
                mc.ingameGUI.drawTexturedModalRect(x, top, 0, 0, 9, 9);
        }
        disableAlpha(alpha);

        // rebind default icons
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel, Minecraft mc, int left, int top, float alpha) {

        int startBar = foodLevel / 2;
        int endBar = (int) Math.ceil(Math.min(20, foodLevel + hungerRestored) / 2f);
        int barsNeeded = endBar - startBar;
        mc.getTextureManager().bindTexture(Gui.icons);

        enableAlpha(alpha);
        for (int i = startBar; i < startBar + barsNeeded; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int icon = 16;
            int background = 13;

            if (mc.thePlayer.isPotionActive(Potion.hunger)) {
                icon += 36;
            }

            mc.ingameGUI.drawTexturedModalRect(x, top, 16 + background * 9, 27, 9, 9);

            if (idx < foodLevel + hungerRestored)
                mc.ingameGUI.drawTexturedModalRect(x, top, icon + 36, 27, 9, 9);
            else if (idx == foodLevel + hungerRestored)
                mc.ingameGUI.drawTexturedModalRect(x, top, icon + 45, 27, 9, 9);
        }
        disableAlpha(alpha);
    }

    public static void enableAlpha(float alpha) {
        GlStateManager.enableBlend();

        if (alpha == 1f)
            return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableAlpha(float alpha) {

        if (alpha == 1f)
            return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @EventHandler
    public void onTick(EventTick e) {
        flashAlpha += alphaDir * 0.125f;
        if (flashAlpha >= 1.5f) {
            flashAlpha = 0.9f;
            alphaDir = -1;
        } else if (flashAlpha <= -0.5f) {
            flashAlpha = 0.1f;
            alphaDir = 1;
        }
    }

    public static void drawExhaustionOverlay(float exhaustion, Minecraft mc, int left, int top, float alpha) {
        mc.getTextureManager().bindTexture(modIcons);
        float maxExhaustion = 4f;
        float ratio = exhaustion / maxExhaustion;
        int width = (int) (ratio * 81);
        int height = 9;

        enableAlpha(.75f);
        mc.ingameGUI.drawTexturedModalRect(left - width, top, 81 - width, 18, width, height);
        disableAlpha(.75f);

        // rebind default icons
        mc.getTextureManager().bindTexture(Gui.icons);
    }
}
