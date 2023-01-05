/*
 * Decompiled with CFR 0_132.
 */
package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;

public class FullBright
extends Module {
    private float old;

    public FullBright() {
        super("FullBright", new String[]{"FullBright", "brightness", "bright"}, ModuleType.Render);
        this.setColor(new Color(244, 255, 149).getRGB());
    }
    public void onEnable() {
        this.old = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @EventHandler
    private void onTick(EventTick e) {
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(),10000,1));
    }

    public void onDisable() {
    	mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        super.onDisable();
    }
}

