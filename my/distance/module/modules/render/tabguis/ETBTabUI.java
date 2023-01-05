package my.distance.module.modules.render.tabguis;


import my.distance.Client;
import my.distance.api.EventBus;
import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventKey;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.manager.Manager;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.render.HUD;
import my.distance.module.modules.render.TabGui;
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.anim.Palette;
import my.distance.util.math.MathUtil;
import my.distance.util.misc.Helper;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ETBTabUI
        implements Manager {
    private Section section = Section.TYPES;
    private ModuleType selectedType = ModuleType.values()[0];
    private Module selectedModule = null;
    private Value selectedValue = null;
    private int currentType = 0;
    private int currentModule = 0;
    private int currentValue = 0;
    private int height = 22;
    private int maxType;
    private int maxModule;
    private int maxValue;
    double AnimY = 0;
    double AnimmodY = 0;
    double AnimValY = 0;
    private static int colorss;
    public static Color RainbowColor = Color.getHSBColor(HUD.hue / 255.0F, 0.55F, 0.9F);
    private static /* synthetic */ int[] QwQ;

    @Override
    public void init() {
        ModuleType[] arrmoduleType = ModuleType.values();
        int n = arrmoduleType.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleType mt = arrmoduleType[n2];
            if (this.maxType <= FontLoaders.Arial18.getStringWidth(mt.name().toUpperCase())) {
                this.maxType = FontLoaders.Arial18.getStringWidth(mt.name().toUpperCase());
            }
            ++n2;
        }
        for (Module m : ModuleManager.getModules()) {
            if (m.wasRemoved()){
                continue;
            }
            if (this.maxModule > FontLoaders.Arial18.getStringWidth(m.getName().toUpperCase()) + 4) continue;
            this.maxModule = FontLoaders.Arial18.getStringWidth(m.getName().toUpperCase()) + 4;
        }
        for (Module m : ModuleManager.getModules()) {
            if (m.wasRemoved()){
                continue;
            }
            if (m.getValues().isEmpty()) continue;
            for (Value val : m.getValues()) {
                if (this.maxValue > FontLoaders.Arial18.getStringWidth(val.getDisplayName().toUpperCase()) + 4) continue;
                this.maxValue = FontLoaders.Arial18.getStringWidth(val.getDisplayName().toUpperCase()) + 4;
            }
        }
        this.maxModule += 12;
        this.maxValue += 24;
        boolean highestWidth = false;
        this.maxType = Math.max(this.maxType, this.maxModule);
        this.maxModule += this.maxType;
        this.maxValue += this.maxModule;
        EventBus.getInstance().register(this);
    }

    private void resetValuesLength() {
        this.maxValue = 0;
        for (Value val : this.selectedModule.getValues()) {
            int off;
            int n = off = val instanceof Option ? 6 : FontLoaders.Arial18.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
            if (this.maxValue > FontLoaders.Arial18.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
            this.maxValue = FontLoaders.Arial18.getStringWidth(val.getDisplayName().toUpperCase()) + off;
        }
        this.maxValue += this.maxModule;
    }

    double animtab = 0d;

    @EventHandler
    private void renderTabGUI(EventRender2D e) {
        RainbowColor = Color.getHSBColor(HUD.hue / 255.0F, 0.55F, 0.9F);
        Color rainbowcolor2 = Color.getHSBColor(HUD.hue / 255.0F, 1F, 1F);
        Color rainbowcolors = Color.getHSBColor(HUD.hue / 255.0f, 0.4f, 0.8f);
        switch ((HUD.ArrayModeE) HUD.ArrayMode.getValue()) {
            case BlueIceSakura:
                colorss = new Color(rainbowcolor2.getRed(), 190, 255).getRGB();
                break;
            case NEON:
                colorss = new Color(rainbowcolors.getBlue(), rainbowcolors.getGreen(), 255).getRGB();
                break;
            case None:
                if (Client.getClientColor() == new Color(255, 255, 255).getRGB()) {
                    colorss = Client.getBlueColor();
                } else {
                    colorss = Client.getClientColor();
                }
                break;
            case Rainbow:
                colorss = RainbowColor.getRGB();
                break;
            case Rainbow2:
                colorss = HUD.RainbowColor2.getRGB();
                break;
            case Wave:
                colorss = Palette.fade(Client.getClientColor(false), 100, 1).getRGB();
                break;
        }
        if (!TabGui.modes.getValue().equals(TabGui.tabguimode.Novoline)) {
            return;
        }
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }
        if (!ModuleManager.getModuleByClass(TabGui.class).isEnabled()) {
            return;
        }

        CFontRenderer font = FontLoaders.Arial18;
        if (Helper.mc.gameSettings.showDebugInfo || !ModuleManager.getModuleByClass(HUD.class).isEnabled())
            return;
        int categoryY = this.height + 12 + TabGui.y.getValue().intValue();
        int moduleY = categoryY;
        int valueY = categoryY;
        RenderUtil.drawRect(2.0f, categoryY, this.maxType - 25 - 25, categoryY + 12 * ModuleType.values().length, new Color(0, 0, 0, 130).getRGB());
        ModuleType[] moduleArray = ModuleType.values();
        int mA = moduleArray.length;
        int mA2 = 0;
        while (mA2 < mA) {
            ModuleType mt = moduleArray[mA2];
            if (this.selectedType == mt) {
                moduleY = categoryY;
                AnimY = AnimationUtils.animate(categoryY, AnimY, 14f / Minecraft.getDebugFPS());
                RenderUtil.drawGradientSideways(2.5, AnimY + 0.5, (double) this.maxType - 25.5 - 25, (double) (AnimY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, colorss, new Color(0, 0, 0, 0).getRGB());
            }
            if (this.selectedType == mt) {
                font.drawStringWithShadow(mt.name(), 7.0f, categoryY + 3, -1);
            } else {
                font.drawStringWithShadow(mt.name(), 5.0f, categoryY + 3, new Color(180, 180, 180).getRGB());
            }
            categoryY += 12;
            ++mA2;
        }
        if (Double.isNaN(animtab))animtab = 0d;
        if (this.section == Section.MODULES || this.section == Section.VALUES) {
            animtab = AnimationUtil.moveUD(animtab, this.section == Section.MODULES? this.maxModule - 38 : this.maxValue - 25, 10.0f / Minecraft.getDebugFPS(), 5.0f / Minecraft.getDebugFPS());
        }else {
            animtab = AnimationUtil.moveUD(animtab, this.maxType - 20 - 25, 10.0f / Minecraft.getDebugFPS(), 5.0f / Minecraft.getDebugFPS());
        }
        RenderUtil.prepareScissorBox(0, 0, (float) animtab, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.drawRect(this.maxType - 20 - 25, moduleY, this.maxModule - 38, moduleY + 12 * ModuleManager.getModulesInType(this.selectedType).size(), new Color(0, 0, 0, 130).getRGB());
            for (Module m : ModuleManager.getModulesInType(this.selectedType)) {
                if (m.wasRemoved()) {
                    continue;
                }
                if (this.selectedModule == m) {
                    AnimmodY = AnimationUtils.animate(moduleY, AnimmodY, 14f / Minecraft.getDebugFPS());
                    RenderUtil.drawGradientSideways((double) this.maxType - 19.5 - 25, (double) AnimmodY + 0.5, (double) this.maxModule - 38.5, (double) (AnimmodY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, colorss, new Color(0, 0, 0, 0).getRGB());

                    valueY = moduleY;
                }
                if (this.selectedModule == m) {
                    font.drawStringWithShadow(m.getName(), this.maxType - 15 - 25, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                } else {
                    font.drawStringWithShadow(m.getName(), this.maxType - 17 - 25, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                }
                if (!m.getValues().isEmpty()) {
                    Gui.drawRect(this.maxModule - 38, (double) moduleY + 0.5, this.maxModule - 39, (double) (moduleY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, new Color(153, 200, 255).getRGB());
                    if (this.selectedModule == m) {
                        RenderUtil.drawRect(this.maxModule - 32, valueY, this.maxValue - 25, valueY + 12 * this.selectedModule.getValues().size(), new Color(10, 10, 10, 130).getRGB());
                        //AnimValY = (int)AnimationUtils.animate(valueY,AnimValY,14f / Minecraft.getDebugFPS());
                        RenderUtil.drawGradientSideways(this.maxModule - 31.5, AnimValY + 0.5, this.maxValue - 25.5, (AnimValY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2.5, colorss, new Color(0, 0, 0, 0).getRGB());
                        for (Value<?> val : this.selectedModule.getValues()) {
                            if (this.selectedValue == val) {
                                AnimValY = AnimationUtils.animate(valueY, AnimValY, 14f / Minecraft.getDebugFPS());
                            }
                            if (val instanceof Option) {
                                font.drawStringWithShadow(val.getDisplayName(), this.selectedValue == val ? this.maxModule - 27 : this.maxModule - 29, valueY + 3, (Boolean) val.getValue() != false ? new Color(153, 200, 255).getRGB() : 11184810);
                            } else {
                                String toRender = String.format("%s: \u00a77%s", val.getDisplayName(), val.getValue().toString());
                                if (this.selectedValue == val) {
                                    font.drawStringWithShadow(toRender, this.maxModule - 27, valueY + 3, -1);
                                } else {
                                    font.drawStringWithShadow(toRender, this.maxModule - 29, valueY + 3, -1);
                                }
                            }
                            valueY += 12;
                        }
                    }
                }
                moduleY += 12;
            }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @EventHandler
    private void onKey(EventKey e) {
        if (!TabGui.modes.getValue().equals(TabGui.tabguimode.Novoline)){
            return;
        }
        if (!Helper.mc.gameSettings.showDebugInfo) {
            block0 : switch (e.getKey()) {
                case 208: {
                    switch (TabUI.QwQ()[this.section.ordinal()]) {
                        case 1: {
                            ++this.currentType;
                            if (this.currentType > ModuleType.values().length - 1) {
                                this.currentType = 0;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            ++this.currentModule;
                            if (this.currentModule > Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1) {
                                this.currentModule = 0;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            ++this.currentValue;
                            if (this.currentValue > this.selectedModule.getValues().size() - 1) {
                                this.currentValue = 0;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 200: {
                    switch (TabUI.QwQ()[this.section.ordinal()]) {
                        case 1: {
                            --this.currentType;
                            if (this.currentType < 0) {
                                this.currentType = ModuleType.values().length - 1;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            --this.currentModule;
                            if (this.currentModule < 0) {
                                this.currentModule = ModuleManager.getModulesInType(this.selectedType).size() - 1;
                            }
                            this.selectedModule = ModuleManager.getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            --this.currentValue;
                            if (this.currentValue < 0) {
                                this.currentValue = this.selectedModule.getValues().size() - 1;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 205: {
                    switch (TabUI.QwQ()[this.section.ordinal()]) {
                        case 1: {
                            this.currentModule = 0;
                            this.selectedModule = ModuleManager.getModulesInType(this.selectedType).get(this.currentModule);
                            this.section = Section.MODULES;
                            break block0;
                        }
                        case 2: {
                            if (this.selectedModule.getValues().isEmpty()) break block0;
                            this.resetValuesLength();
                            this.currentValue = 0;
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                            this.section = Section.VALUES;
                            break block0;
                        }
                        case 3: {
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue(!((Boolean) this.selectedValue.getValue()));
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc += value.getIncrement().doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) > (Double)value.getMaximum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMinimum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.resetValuesLength();
                        }
                    }
                    break;
                }
                case 28: {
                    switch (TabUI.QwQ()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.selectedModule.setEnabled(!this.selectedModule.isEnabled());
                            break block0;
                        }
                        case 3: {
                            this.section = Section.MODULES;
                        }
                    }
                    break;
                }
                case 203: {
                    switch (TabUI.QwQ()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.section = Section.TYPES;
                            this.currentModule = 0;
                            break block0;
                        }
                        case 3: {
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean)this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers)this.selectedValue;
                                double inc = (Double)value.getValue();
                                inc -= ((Double)value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double)value.getMinimum()) {
                                    inc = (Double)((Numbers)this.selectedValue).getMaximum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode)this.selectedValue;
                                Enum current = (Enum)theme.getValue();
                                int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.maxValue = 0;
                            for (Value val : this.selectedModule.getValues()) {
                                int off;
                                int n = off = val instanceof Option ? 6 : Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
                                if (this.maxValue > Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off) continue;
                                this.maxValue = Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
                            }
                            this.maxValue += this.maxModule;
                        }
                    }
                }
            }
        }
    }

    static /* synthetic */ int[] QwQ() {
        int[] arrn;
        int[] arrn2 = QwQ;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Section.values().length];
        try {
            arrn[Section.MODULES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.TYPES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[Section.VALUES.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        QwQ = arrn;
        return QwQ;
    }

    public static enum Section {
        TYPES,
        MODULES,
        VALUES;
    }

}

