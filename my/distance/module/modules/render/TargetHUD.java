//Code by Mymylesaws
package my.distance.module.modules.render;

import com.ibm.icu.text.NumberFormat;
import my.distance.Client;
import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.fastuni.FastUniFontRenderer;
import my.distance.fastuni.FontLoader;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.combat.KillAura;
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.render.ColorUtils;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class TargetHUD
extends Module {
    private double healthBarWidth;
    private double healthBarWidth2;
    private double introAnim;
    public static double animation = 0;
    private final FastUniFontRenderer font1 = Client.FontLoaders.Chinese16;
    boolean startAnim, stopAnim;
    double r2;
    public EntityLivingBase lastEnt;
    float anim2 = 0f;
    double rect;
    int animAlpha = 0;
    private static final TimerUtil timerUtils = new TimerUtil();

    public DecimalFormat format;
    public static Mode mode = new Mode("Mode", Modes.values(), Modes.Distance);
    public static Mode animMode = new Mode("Animation Mode", AnimMode.values(), AnimMode.Scale);

    public static Numbers<Double> hudx = new Numbers<>("X", 70.0d, 0d, 200d, 1d);
    public static Numbers<Double> hudY = new Numbers<>("Y", 80.0d, 0d, 200d, 1d);

    private static final Option black = new Option("Black", true);
    private static final Option Pvp = new Option("PVP", false);

    private static double animHealth = 0;
    private static final DecimalFormat df = new DecimalFormat("00.0");


    public TargetHUD() {
        super("TargetHUD", new String[]{"TargetInfo"}, ModuleType.Render);
        addValues(hudx, hudY, mode, black, animMode, Pvp);
    }

    @Override
    public void onEnable() {
        animation = 0;
    }

    float width = 0;
    float height = 0;

    boolean introOverCharge = false;

    @EventHandler
    public void onRender(EventRender2D event) {
        if (mc.ingameGUI.getChatGUI().getChatOpen()) {
            nulltarget = false;
            target = mc.thePlayer;
        } else {
            if (KillAura.currentTarget == null) {
                if (Pvp.getValue()) {
                    if (mc.pointedEntity != null) {
                        nulltarget = false;
                        if (mc.pointedEntity instanceof EntityLivingBase) {
                            target = (EntityLivingBase) mc.pointedEntity;
                        } else {
                            nulltarget = true;
                        }
                    } else {
                        nulltarget = true;
                    }
                } else {
                    nulltarget = true;
                }
            } else {
                nulltarget = false;
                target = KillAura.currentTarget;
            }
        }

        GlStateManager.pushMatrix();
        sr = new ScaledResolution(mc);
        if (nulltarget) {
            if (timerUtils.hasReached(1000L)) {
                introOverCharge = false;
                this.introAnim = AnimationUtils.animate(animMode.get().equals(AnimMode.Scale) ? 0f : (sr.getScaledWidth() / 2.0f) - (hudx.getValue().floatValue()), this.introAnim, 14f / Minecraft.getDebugFPS());
            } else {
                if (!introOverCharge && introAnim > 110){
                    introOverCharge = true;
                }
                this.introAnim = AnimationUtils.animate(animMode.get().equals(AnimMode.Scale) ? introOverCharge ? 100f : 140f : 0f, this.introAnim, 14f / Minecraft.getDebugFPS());
            }
        } else {
            timerUtils.reset();
            if (!introOverCharge && introAnim > 110){
                introOverCharge = true;
            }
            this.introAnim = AnimationUtils.animate(animMode.get().equals(AnimMode.Scale) ? introOverCharge ? 100f : 140f : 0f, this.introAnim, 14f / Minecraft.getDebugFPS());
        }
        x = sr.getScaledWidth() / 2.0f + hudx.getValue().floatValue();
        y = sr.getScaledHeight() / 2.0f + hudY.getValue().floatValue();

        if (animMode.get().equals(AnimMode.Scale)) {
            float value = (float) Math.max(0, introAnim / 100f);
            GlStateManager.translate((x + width / 2f) * (1 - value), (y + height / 2f) * (1 - value), 0f);
            GlStateManager.scale(value, value, 0);
        } else {
            GlStateManager.translate(introAnim, 0, 0);
        }

        if (animMode.get().equals(AnimMode.Slide) && introAnim == sr.getScaledWidth() / 2.0f - hudx.getValue().floatValue()) {
            if (mode.get().equals(Modes.Distance)) {
                this.healthBarWidth2 = 92;
                this.healthBarWidth = 92;
            }
        } else if (animMode.get().equals(AnimMode.Scale) && introAnim < 30) {
            if (mode.get().equals(Modes.Distance)) {
                this.healthBarWidth2 = 92;
                this.healthBarWidth = 92;
            }
        } else {
            switch ((Modes) mode.getValue()) {
                case Simple: {
                    simple();
                    break;
                }
                case Distance: {
                    Flat();
                    break;
                }
                case Flux: {
                    FLux();
                    break;
                }
                case Exhibition: {

                    break;
                }
            }
        }
        GlStateManager.popMatrix();
    }

    EntityLivingBase lastTarget;
    EntityLivingBase target;
    ScaledResolution sr = new ScaledResolution(mc);

    boolean nulltarget = false;
    double healthLocationani;
    float x = sr.getScaledWidth() / 2.0f + hudx.getValue().floatValue();
    float y = sr.getScaledHeight() / 2.0f + hudY.getValue().floatValue();

    private void Flat() {
        //Distance TH code by Mymylesaws
        int blackcolor = black.getValue() ? new Color(0, 0, 0, 180).getRGB() : new Color(200, 200, 200, 180).getRGB();
        int blackcolor2 = !black.getValue() ? new Color(0, 0, 0).getRGB() : new Color(200, 200, 200).getRGB();

        float health;
        double hpPercentage;
        Color hurt;
        int healthColor;
        if (nulltarget) {
            health = 0;
            hpPercentage = health / 20;
            hurt = Color.getHSBColor(300f / 360f, ((float) 0 / 10f) * 0.37f, 1f);
            healthColor = ColorUtils.getHealthColor(0, 20).getRGB();
        } else {
            health = target.getHealth();
            hpPercentage = health / target.getMaxHealth();
            hurt = Color.getHSBColor(310f / 360f, ((float) target.hurtTime / 10f), 1f);
            healthColor = ColorUtils.getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB();
        }
        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0);
        double hpWidth = 92.0 * hpPercentage;

        if (nulltarget) {
            this.healthBarWidth2 = AnimationUtil.moveUD((float) this.healthBarWidth2, 0, 7f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
            this.healthBarWidth = AnimationUtils.animate(0, this.healthBarWidth, 14f / Minecraft.getDebugFPS());
        } else {
            this.healthBarWidth2 = AnimationUtil.moveUD((float) this.healthBarWidth2, (float) hpWidth, 7f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
            this.healthBarWidth = AnimationUtils.animate(hpWidth, this.healthBarWidth, 14f / Minecraft.getDebugFPS());
        }
        width = 140.0f;
        height = 38.0f;
        RenderUtil.drawFastRoundedRect(x, y, x + 140.0f, y + 38.0f, 2f, blackcolor);
        RenderUtil.drawRect(x + 40.0, y + 15.0, (x + 40) + 92, y + 25, new Color(0, 0, 0, 49).getRGB());
        animHealth = AnimationUtil.moveUD(animHealth, health, 14f / Minecraft.getDebugFPS(), 4f / Minecraft.getDebugFPS());
        RenderUtil.drawRect(x + 40.0f, y + 15.0f, (int) (x + 40.0f) + (int) this.healthBarWidth2, y + 25.0f, new Color(255, 0, 213, 220).getRGB());
        RenderUtil.drawGradientSideways(x + 40.0f, y + 15.0f, (double) (x + 40.0f) + this.healthBarWidth, y + 25.0f, new Color(0, 81, 179).getRGB(), healthColor);
        if (Double.isNaN(animHealth) || Double.isInfinite(animHealth)) {
            animHealth = 0d;
        }
        String bd = df.format(animHealth);
        font1.drawStringWithShadow(bd, x + 40.0f + 46.0f - (float) font1.getStringWidth(bd) / 2.0f + mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 17.5f, blackcolor2);
        mc.fontRendererObj.drawStringWithShadow("\u2764", x + 40.0f + 46.0f - (float) font1.getStringWidth(bd.toString()) / 2.0f - mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 15.5f, hurt.getRGB());
        FastUniFontRenderer font2 = Client.FontLoaders.Chinese13;
        if (nulltarget) {
            font2.drawStringWithShadow("XYZ:" + 0 + " " + 0 + " " + 0 + "   |   " + "Hurt:" + (false), x + 41f, y + 29f, blackcolor2);
            font1.drawStringWithShadow("(无目标)", x + 40.0f, y + 6.0f, blackcolor2);
        } else {
            font2.drawStringWithShadow("XYZ:" + (int) target.posX + " " + (int) target.posY + " " + (int) target.posZ + "   |   " + "Hurt:" + (target.hurtTime > 0), x + 41f, y + 29f, blackcolor2);
            font1.drawStringWithShadow(target.getName(), x + 40.0f, y + 6.0f, blackcolor2);
            if ((target instanceof EntityPlayer)) {
                mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
                Gui.drawScaledCustomSizeModalRect((int) x + 6, (int) y + 6, 8.0F, 8.0F, 8, 8, 28, 27, 64, 64);
            }
        }
    }

    public void simple() {
        float w = x;
        float h = y;
        String NAME = nulltarget ? "(无目标)" : target.getName();
        String HEALTH = "H:" + (nulltarget ? "0" : df.format(target.getHealth()));
        int width = Math.max(70, FontLoader.msFont16.getStringWidth(NAME) + (FontLoader.msFont16.getStringWidth(HEALTH) / 2));
//        Blur.blurAreaBoarderXY(w, h + 15, w + width + 1, h + 30);
        this.width = width + 1;
        height = 15;

        RenderUtil.drawRect(w, h, w + width + 1, h + 15, new Color(15, 15, 15, 140).getRGB());
        FontLoader.msFont16.drawString(NAME, w + 3, h + 5, -1);
        float healthWidth = (float) ((width + 1) * MathHelper.clamp_double(nulltarget ? 0 : target.getHealth() / (nulltarget ? 20 : target.getMaxHealth()), 0, 1));
        if (lastTarget != target && !nulltarget) {
            lastTarget = target;
            healthBarWidth2 = healthWidth;
        }
        this.healthBarWidth2 = AnimationUtil.moveUD((float) this.healthBarWidth2, healthWidth, 10f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());

        int color;
        float health = nulltarget ? 0 : target.getHealth();
        if (health > 20) {
            health = 20;
        }
        float[] fractions = new float[]{0f, 0.5f, 1f};
        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
        float progress = (health * 5) * 0.01f;
        Color customColor = ColorUtils.blendColors(fractions, colors, progress).brighter();
        color = customColor.getRGB();
        RenderUtil.drawRect(w, h + 14, w + healthBarWidth2, h + 15, ColorUtils.getDarker(new Color(color), 60, 255).getRGB());
        RenderUtil.drawRect(w, h + 14, w + healthWidth, h + 15, color);
    }

    //Flux
    public void FLux() {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 - 5;
        if (KillAura.currentTarget != null) {
            this.FluxBackground(target);
            this.FluxonName(target);
            this.FluxonHead();
            RenderUtil.drawRect(x - 0.3, y - 10, x + 20 + 0.3, y - 9.3, new Color(149, 255, 147).getRGB());

            RenderUtil.drawRect(x - 0.3, y - 10, x, y + 10, new Color(149, 255, 147).getRGB());

            RenderUtil.drawRect(x - 0.3, y + 10, x + 20 + 0.3, y + 10.3, new Color(149, 255, 147).getRGB());

            RenderUtil.drawRect(x + 20, y - 10, x + 20 + 0.6, y + 10, new Color(149, 255, 147).getRGB());
//            RenderUtil.drawRect(x - 0.5, y - 10, x + 20 + 0.5, y - 9.5, new Color(149,255,147).getRGB());
//            RenderUtil.drawRect(x - 0.5, y - 10, x + 20 + 0.5, y - 9.5, new Color(149,255,147).getRGB());
            if (ModuleManager.getModuleByClass(KillAura.class).isEnabled()) {

                EntityLivingBase target1 = KillAura.currentTarget;
                if (target1 != this.lastEnt && target1 != null) {
                    this.lastEnt = target1;
                }
                if (startAnim) {
                    stopAnim = false;
                }
                if (animAlpha == 255 && KillAura.currentTarget == null) {
                    stopAnim = true;
                }
                startAnim = KillAura.currentTarget != null;
                if (startAnim) {
                    if (animAlpha < 255) {
                        animAlpha += 15;
                    }
                }
                if (stopAnim) {
                    if (animAlpha > 0) {
                        animAlpha -= 15;
                    }
                }
                if (KillAura.currentTarget == null && animAlpha < 255) {
                    stopAnim = true;
                }
                EntityLivingBase player = null;
                if (lastEnt != null) {
                    player = lastEnt;
                }
                int c;
                if (player != null && animAlpha >= 135) {
                    double Width = getWidth(KillAura.currentTarget);
                    if (Width < 50.0) {
                        Width = 50.0;
                    }
                    final double healthLocation;


                    if (KillAura.currentTarget.getHealth() > 20)
                        healthLocation = 16 + rect;
                    else
                        healthLocation = ((16 + rect) / 20) * (int) KillAura.currentTarget.getHealth();

                    anim2 = AnimationUtil.moveUD(anim2, (float) healthLocation, 18f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
                    int color = KillAura.currentTarget.getHealth() > 10.0f ? RenderUtil.blend(new Color(-16711936), new Color(-256), 1.0f / KillAura.currentTarget.getHealth() / 2.0f * (KillAura.currentTarget.getHealth() - 10.0f)).getRGB() : RenderUtil.blend(new Color(-256), new Color(-65536), 0.1f * KillAura.currentTarget.getHealth()).getRGB();

                    r2 = ((16 + rect) / 20) * KillAura.currentTarget.getTotalArmorValue();
                    //health


                    Gui.drawRect(x + 7,
                            y + 13,
                            x + 23 + rect,
                            y + 15, new Color(60, 60, 60).getRGB());

                    if (!((x + 7) == (x + 7 + anim2))) {
                        RenderUtil.drawFastRoundedRect(x + 7,
                                y + 13,
                                x + 7 + anim2,
                                y + 15, 1, new Color(255, 213, 0, 201).getRGB());
                    }
                    if (!((x + 7) == (x + 7 + healthLocation))) {
                        RenderUtil.drawFastRoundedRect((float) (x + 7),
                                (float) (y + 13),
                                (float) (x + 7 + healthLocation),
                                y + 15, 1, new Color(47, 190, 130).getRGB());
                    }


                    RenderUtil.drawFastRoundedRect((float) (x + 7),
                            (float) (y + 18),
                            (float) (x + 23 + rect),
                            y + 20,1, new Color(60, 60, 60).getRGB());


                    if (!((x + 7) == (x + 7 + r2))) {
                        RenderUtil.drawFastRoundedRect((float) (x + 7),
                                (float) (y + 18),
                                (float) (x + 7 + r2),
                                y + 20, 1, new Color(87, 130, 189).getRGB());
                    }

                }
            }
        }
    }

    private int getWidth(EntityLivingBase target) {
        return 38 + FontLoaders.GoogleSans18.getStringWidth(target.getName());
    }

    private void FluxBackground(EntityLivingBase target) {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 - 5;

        double hea = target.getHealth();
        double f1 = new com.ibm.icu.math.BigDecimal(hea).setScale(1, com.ibm.icu.math.BigDecimal.ROUND_HALF_UP).doubleValue();

        if (FontLoaders.GoogleSans18.getStringWidth(target.getName()) > FontLoaders.GoogleSans14.getStringWidth("Health:" + f1 + ""))
            rect = FontLoaders.GoogleSans18.getStringWidth(target.getName());
        if (FontLoaders.GoogleSans18.getStringWidth(target.getName()) == FontLoaders.GoogleSans14.getStringWidth("Health:" + f1 + ""))
            rect = FontLoaders.GoogleSans18.getStringWidth(target.getName());
        if (FontLoaders.GoogleSans18.getStringWidth(target.getName()) < FontLoaders.GoogleSans14.getStringWidth("Health:" + f1 + ""))
            rect = FontLoaders.GoogleSans14.getStringWidth("Health:" + f1 + "");

        RenderUtil.drawFastRoundedRect(x - 3,
                y - 13,
                (int) (x + 25 + rect) + 1,
                y - 20 + 28 + 16, 2, new Color(40, 40, 40, 200).getRGB());
    }

    private void FluxonHead() {
        if (!(KillAura.currentTarget instanceof EntityPlayer)) {
            return;
        }
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 - 5;
        mc.getTextureManager().bindTexture(((AbstractClientPlayer) KillAura.currentTarget).getLocationSkin());

        Gui.drawScaledCustomSizeModalRect(x, y - 10, 8.0F, 8.0F, 8, 8, 20, 20, 64, 64);


    }

    private void FluxonName(EntityLivingBase target) {
        ScaledResolution res = new ScaledResolution(mc);
        int x = res.getScaledWidth() / 2 + 30;
        int y = res.getScaledHeight() / 2 + 30;
        final CFontRenderer font2 = FontLoaders.GoogleSans14;

        final CFontRenderer font3 = FontLoaders.ICON10;
        Client.FontLoaders.Chinese18.drawStringWithShadow(target.getName(), x + 20 + 3, y - 45 + 3, -1);

        double hea = target.getHealth();

        String str1 = String.format("%.1f", hea);

        double f1 = new com.ibm.icu.math.BigDecimal(hea).setScale(1, com.ibm.icu.math.BigDecimal.ROUND_HALF_UP).doubleValue();

        font2.drawStringWithShadowNew("", x - 20 + 50 + 2 + 2 + font2.getStringWidth("Health: " + f1 + ""), y - 25 + 20 + 6, -1);
        font2.drawStringWithShadowNew("Health:" + f1 + "", x + 20 + 2, y - 40 + font2.getStringHeight("A") + 2, -1);

        font3.drawString("s", x, y - 30 + FontLoaders.GoogleSans18.getStringHeight("A") + 1, Color.getHSBColor(1f, KillAura.currentTarget.hurtTime / 10f, 0.9f).getRGB());

        font3.drawString("r", x, y - 30 + FontLoaders.GoogleSans18.getStringHeight("A") + 6.5f, -1);
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];

        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {

        }

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Color color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can\'t be null");
        } else if (colors == null) {
            throw new IllegalArgumentException("Colours can\'t be null");
        } else if (fractions.length == colors.length) {
            int[] indicies = getFractionIndicies(fractions, progress);
            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            color = blend(colorRange[0], colorRange[1], (double) (1.0F - weight));
            return color;
        } else {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0F - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0F) {
            red = 0.0F;
        } else if (red > 255.0F) {
            red = 255.0F;
        }

        if (green < 0.0F) {
            green = 0.0F;
        } else if (green > 255.0F) {
            green = 255.0F;
        }

        if (blue < 0.0F) {
            blue = 0.0F;
        } else if (blue > 255.0F) {
            blue = 255.0F;
        }

        Color color3 = null;

        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException var14) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format((double) red) + "; " + nf.format((double) green) + "; " + nf.format((double) blue));
            var14.printStackTrace();
        }

        return color3;
    }

    private double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return (double) Math.round(val * one) / one;
    }

    enum AnimMode{
        Slide,
        Scale
    }

    enum Modes {
        Distance,
        Flux,
        Exhibition,
        Simple
    }
}


