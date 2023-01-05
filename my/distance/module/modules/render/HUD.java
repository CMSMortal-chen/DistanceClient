package my.distance.module.modules.render;

import com.google.common.collect.Lists;
import my.distance.Client;
import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.verify.SHWID;
import my.distance.fastuni.FastUniFontRenderer;
import my.distance.manager.Fucker;
import my.distance.manager.ModuleManager;
import my.distance.manager.RenderManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.player.AutoTP;
import my.distance.module.modules.world.Scaffold;
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.jello.Compass;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.Vec3;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.anim.Palette;
import my.distance.util.misc.Helper;
import my.distance.util.render.Colors;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class HUD extends Module {
    private static CFontRenderer fontarry;
    public static Option hideRender = new Option("HideRender", false);
    public static Numbers<Double> ArrayGap = new Numbers<>("ArraylistGap", 0.0D, -10.0D, 10.0D, 1D);
    private final Option info = new Option("Information", true);
    public static Mode ArrayMode = new Mode("ArrayColorMode", ArrayModeE.values(), ArrayModeE.None);
    public static Mode ArrayFontMode = new Mode("ArrayFont", ArrayFont.values(), ArrayFont.GoogleSans16);

    public static Option customlogo = new Option("Logo", true);
    public static Option lhp = new Option("LowHPWarning", true);
    public static Option Arraylists = new Option("ArrayList", true);
    public static Option ArrayShadow = new Option("ArrayShadow", true);

    public static Numbers<Double> RainbowSpeed = new Numbers<>("RainbowSpeed", 6.0D, 1.0D,
            20.0D, 1D);
    public static Option clientCape = new Option("Cape", true);

    public static Mode Widget = new Mode("Widget", widgetE.values(), widgetE.None);
    public static Numbers<Double> r = new Numbers<>("Red", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> g = new Numbers<>("Green", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> b = new Numbers<>("Blue", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> a = new Numbers<>("Alpha", 255.0, 0.0, 255.0, 1.0);
    public static Numbers<Double> Arraybackground = new Numbers<>("ArrayAlpha", 60.0, 0.0, 255.0, 1.0);
    public static Mode logomode = new Mode("LogoMode", HUD.logomodeE.values(), logomodeE.Distance);
    public static Mode RectMode = new Mode("RectMode", RectModes.values(), RectModes.None);
    private final Option CompassValue = new Option("Compass", false);

    public static boolean firstrun = true;
    public static boolean shouldMove;
    public static float hue = 0.0F;
    public static Color RainbowColor = Color.getHSBColor(hue / 255.0F, 0.55F, 0.9F);
    public static Color RainbowColors = Color.getHSBColor(hue / 255.0F, 0.4f, 0.8f);
    public static Color RainbowColor2 = Client.getClientColor(true);
    public Compass compass = new Compass(325, 325, 1, 2, true);
    boolean lowhealth = false;
    boolean arryfont;
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    float animLogoX = 2;
    float animLogoY = 3;
    boolean firstTime = true;
    public TimerUtil timer = new TimerUtil();
    public float animation = 0;

    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Render);
        this.addValues(logomode, Widget, RainbowSpeed, Arraylists, ArrayGap,
                hideRender, RectMode, info, ArrayShadow, ArrayFontMode, ArrayMode, customlogo,
                CompassValue, lhp, r, g, b, a, clientCape, Arraybackground);
    }

    @EventHandler
    private void Render3d(EventRender3D e) {
        if (AutoTP.path != null && ModuleManager.getModuleByClass(AutoTP.class).isEnabled()) {
            for (int i = 0; i < AutoTP.path.size(); i++) {
                try {
                    for (Vec3 pos : AutoTP.path) {
                        if (pos != null)
                            //GL11.glEnable(3042);
                            RenderUtil.drawPath(pos);
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    int addY;

    @EventHandler
    private void renderHud(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);

        compass = new Compass(325, 325, 1, 4, true);
        if (Main.isbeta && firstrun) {
            Helper.sendMessage("Distance Beta tips:");
            Helper.sendMessage("");
        }
        if (firstrun) firstrun = false;

        RainbowColors = Color.getHSBColor(hue / 255.0F, 0.4f, 0.8f);
        Color rainbowcolors = Color.getHSBColor(hue / 255.0f, 0.4f, 0.8f);
        Color rainbowcolors2 = Color.getHSBColor(hue / 255.0f, 1f, 1f);
        Color color2222 = Color.getHSBColor(hue / 255.0F, 0.55F, 0.9F);
        int c2222 = color2222.getRGB();
        int colorXD;
        if (CompassValue.getValue()) {
            compass.draw(sr);
        }
        if (ArrayMode.getValue().equals(ArrayModeE.Wave)) {
            colorXD = Palette.fade(Client.getClientColor(false), (addY + 11) / 11, 16).getRGB();
        } else if (ArrayMode.getValue().equals(ArrayModeE.Rainbow)) {
            colorXD = c2222;
        } else if (ArrayMode.getValue().equals(ArrayModeE.Rainbow2)) {
            colorXD = RainbowColor2.getRGB();
        } else if (ArrayMode.getValue().equals(ArrayModeE.BlueIceSakura)) {
            colorXD = new Color(rainbowcolors2.getRed(), 190, 255).getRGB();
        } else if (ArrayMode.getValue().equals(ArrayModeE.NEON)) {
            colorXD = new Color(rainbowcolors.getRed(), rainbowcolors.getGreen(), 255).getRGB();
        } else {
            colorXD = Client.getClientColor();
        }
        if (SHWID.BITCH != 0) {
            RenderManager.doRender();
        }
        if (SHWID.hahaha != 1) {
            Fucker.dofuck();
        }
        if (SHWID.id != 1) {
            RenderManager.doRender();
            Fucker.dofuck();
        }
        if (SHWID.id2 != 2) {
            RenderManager.doRender();
            Fucker.dofuck();
        }
        hue += RainbowSpeed.getValue().floatValue() / 5.0F;
        if (hue > 255.0F) {
            hue = 0.0F;
        }
        float h = hue;
        if (lhp.getValue()) {
            if (mc.thePlayer.getHealth() < 6 && !lowhealth) {
                Notifications.getManager().post("Warning!","当前血量过低！", Notifications.Type.WARNING);
                lowhealth = true;
            }
            if (mc.thePlayer.getHealth() > 6 && lowhealth) {
                lowhealth = false;
            }
        }
        CFontRenderer font = FontLoaders.GoogleSans18;

//        if (MusicManager.INSTANCE.lyric && MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING){
//            FastUniFontRenderer lyricFont = FontLoader.msFont25;
//            int addonYlyr = MusicPlayer.musicPosYlyr.getValue().intValue();
//            //Lyric
//            int borderCol = new Color(238, 171, 227).getRGB();
//            int col = new Color(0xffE8DEFF).getRGB();
//
//            lyricFont.drawCenterOutlinedString(MusicManager.INSTANCE.lrcCur.contains("_EMPTY_") ? "": MusicManager.INSTANCE.lrcCur, sr.getScaledWidth() / 2f - 0.5f, sr.getScaledHeight() - 140 - 80 + addonYlyr, borderCol, col);
//
//            lyricFont.drawCenterOutlinedString(MusicManager.INSTANCE.tlrcCur.contains("_EMPTY_") ? "": MusicManager.INSTANCE.tlrcCur, sr.getScaledWidth() / 2f, sr.getScaledHeight() - 120 + 0.5f - 80 + addonYlyr, new Color(0x595959).getRGB(), col);
//
//        }
//        if ((MusicManager.showMsg)) {
//            Notifications.getManager().post("正在播放",MusicManager.INSTANCE.getCurrentTrack().name + " - " + MusicManager.INSTANCE.getCurrentTrack().artists,5000L, Notifications.Type.MUSIC);
//            MusicManager.showMsg = false;
//        }

        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && Scaffold.renderBlockCount) {
            Scaffold.renderBlock();
        }


        String name;

        if (!Widget.getValue().equals(widgetE.None) && !Widget.getValue().equals(widgetE.Astolfo)) {
            int widgetwidth = ((widgetE)Widget.getValue()).width;
            int widgetheight = ((widgetE)Widget.getValue()).height;
            int id = ((widgetE)Widget.getValue()).id;
            widgetwidth *= 0.25;
            widgetheight *= 0.25;
            RenderUtil.drawCustomImage(RenderUtil.width() - 100 - widgetwidth, RenderUtil.height() - widgetheight - (mc.ingameGUI.getChatGUI().getChatOpen() ? 14 : 0), widgetwidth, widgetheight, new ResourceLocation("Distance/widget/" + id + ".png"));
        }else if (Widget.getValue().equals(widgetE.Astolfo)){
            RenderUtil.drawImage(new ResourceLocation("Distance/AstolfoTrifasSprite.png"), RenderUtil.width() - 160, RenderUtil.height() - 70, 256, 256);
        }
            if (customlogo.getValue()) {
            HUD.shouldMove = true;
            switch ((logomodeE) logomode.getValue()) {
                case Dark_Distance:
                case Distance:
                    CFontRenderer dfont1 = FontLoaders.calibrilite50;
                    CFontRenderer dfont2 = FontLoaders.calibrilite15s;
                    if (animLogoX != (mc.gameSettings.showDebugInfo ? sr.getScaledWidth() / 2f : 2f) || animLogoY != (mc.gameSettings.showDebugInfo ? 80f : 3f)) {
                        animLogoX = AnimationUtil.moveUD(animLogoX, mc.gameSettings.showDebugInfo ? (sr.getScaledWidth() / 2f) - dfont1.getStringWidth("Distance") / 2f: 2f, 10f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
                        animLogoY = AnimationUtil.moveUD(animLogoY, mc.gameSettings.showDebugInfo ? 80f : 3f, 10f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
                    }
                    GlStateManager.enableBlend();
                    RenderUtil.drawImage(new ResourceLocation("Jello/shadow.png"), -20, 0, dfont1.getStringWidth("Distance") + 35, dfont1.getStringHeight("Distance") + 1);

                    dfont1.drawString(Client.name, animLogoX, animLogoY, new Color(255, 255, 255).getRGB());
                    dfont2.drawString(Client.distanceVersion, animLogoX + 2f, animLogoY + 24, new Color(255, 255, 255).getRGB());
                    //GL11.glDisable(3042);
                    break;
                case Jigsaw: {
                    FastUniFontRenderer font1 = Client.FontLoaders.KomikaTitleBold50;
                    if (Client.getClientColor(180) == new Color(255, 255, 255, 180).getRGB()) {
                        font1.drawStringWithShadow(Client.name, 4.0f, 22, new Color(255, 255, 255).getRGB(), Client.getBBlueColor(), 2);
                    } else {
                        font1.drawStringWithShadow(Client.name, 4.0f, 22, new Color(255, 255, 255).getRGB(), Client.getClientColor(180), 2);
                    }
                    break;
                }
                case Novoline: {
                    int ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 25 : 10;
                    name = "\247l" + Client.name.charAt(0);
                    String text = EnumChatFormatting.WHITE + "XYZ: " + EnumChatFormatting.GRAY + MathHelper.floor_double(mc.thePlayer.posX) + " " + EnumChatFormatting.GRAY + MathHelper.floor_double(mc.thePlayer.posY) + " " + EnumChatFormatting.GRAY + MathHelper.floor_double(mc.thePlayer.posZ) + EnumChatFormatting.WHITE + " | " + EnumChatFormatting.WHITE + "FPS: " + EnumChatFormatting.GRAY + Minecraft.debugFPS + EnumChatFormatting.WHITE + " | " + EnumChatFormatting.WHITE + "User: " + EnumChatFormatting.GREEN + Client.userName;
                    FontLoaders.GoogleSans18.drawStringWithShadow(name, 3.0F, 3.0F, colorXD);
                    FontLoaders.GoogleSans18.drawStringWithShadow(text, 3.0f, sr.getScaledHeight() - ychat, new Color(12, 12, 17).getRGB());
                    String ok = Client.name.substring(1);
                    font.drawStringWithShadow(ok + " " + EnumChatFormatting.GRAY + Client.version + " \2477(\247r" + df.format(new Date()) + "\2477)\247r", font.getStringWidth(name) + 3.5F, 3.0F, new Color(255, 255, 255).getRGB());
                    //GL11.glDisable(3042);
                    break;
                }
            }
        }
        if (!mc.gameSettings.showDebugInfo) {
            // ArrayList
            arryfont = true;
            switch ((ArrayFont) ArrayFontMode.getValue()) {
                case Jello18:
                    fontarry = FontLoaders.Jello18;
                    break;
                case GoogleSans16:
                    fontarry = FontLoaders.GoogleSans16;
                    break;
                case GoogleSans18:
                    fontarry = FontLoaders.GoogleSans18;
                    break;
                case Jello16:
                    fontarry = FontLoaders.Jello16;
                    break;
                case Baloo16:
                    fontarry = FontLoaders.Baloo16;
                    break;
                case Baloo18:
                    fontarry = FontLoaders.Baloo18;
                    break;
                case Minecraft:
                    arryfont = false;
            }
            if (fontarry == null) {
                fontarry = FontLoaders.GoogleSans16;
            }
            int[] counter = {0};
            if (Arraylists.getValue()) {
                ArrayList<Module> sorted = new ArrayList<>();
                for (Module m : ModuleManager.getModules()) {
                    if ((m.getType().equals(ModuleType.Render) && !m.getName().equalsIgnoreCase("PacketMotior") && hideRender.getValue()) || m.wasRemoved() || (!m.isEnabled() && !m.getRender()))
                        continue;
                    sorted.add(m);
                }
                if (!arryfont) {
                    sorted.sort((o1, o2) -> mc.fontRendererObj
                            .getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName(o2)
                                    : String.format("%s %s", Client.getModuleName(o2), o2.getSuffix()))
                            - mc.fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName(o1)
                            : String.format("%s %s", Client.getModuleName(o1), o1.getSuffix())));
                } else {
                    sorted.sort((o1, o2) -> fontarry
                            .getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName(o2)
                                    : String.format("%s %s", Client.getModuleName(o2), o2.getSuffix()))
                            - fontarry.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName(o1)
                            : String.format("%s %s", Client.getModuleName(o1), o1.getSuffix())));
                }
                int nextY = 0;
                double lastX = 0;
                float width;
                boolean first = true;
                Color color = new Color(r.getValue().intValue(), g.getValue().intValue(), b.getValue().intValue(), a.getValue().intValue());
                for (Module m : sorted) {
                    Color rainbowcolor = Color.getHSBColor(h / 255.0f, 0.4f, 0.8f);
                    Color rainbowcolor2 = Color.getHSBColor(h / 255.0f, 0.6f, 1f);
                    color = new Color(r.getValue().intValue(), g.getValue().intValue(), b.getValue().intValue(), a.getValue().intValue());
                    if (h > 255.0F) {
                        h = 0.0F;
                    }
                    if (ArrayMode.getValue().equals(ArrayModeE.Wave)) {
                        color = Palette.fade(Client.getClientColor(false), (int) ((nextY - m.posYRend) / 11), 11);
                    }
                    if (ArrayMode.getValue().equals(ArrayModeE.Rainbow)) {
                        color = Color.getHSBColor(h / 255.0f, 0.5f, 0.9f);
                    }
                    if (ArrayMode.getValue().equals(ArrayModeE.Rainbow2)) {
                        color = RenderUtil.rainbow(counter[0] * -50, true);
                        RainbowColor2 = color;
                    }
                    if (ArrayMode.getValue().equals(ArrayModeE.BlueIceSakura)) {
                        color = new Color(rainbowcolor2.getRed(), 180, 255);
                    }
                    if (ArrayMode.getValue().equals(ArrayModeE.NEON)) {
                        color = new Color(rainbowcolor.getRed(), rainbowcolor.getGreen(), 255);
                    }
                    m.lastY = m.posY;
                    m.posY = nextY;
                    m.onRenderArray();
                    name = m.getSuffix().isEmpty() ? Client.getModuleName(m)
                            : (String.format("%s %s", Client.getModuleName(m), m.getSuffix()));
                    double x = RenderUtil.width() - m.getX();
                    if (!RectMode.getValue().equals(RectModes.Right)) {
                        if (arryfont) {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + fontarry.getStringWidth(name), nextY + m.posYRend + 11 + ArrayGap.getValue(), new Color(0, 0, 0, Arraybackground.getValue().intValue()).getRGB());
                        } else {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + mc.fontRendererObj.getStringWidth(name), nextY + m.posYRend + 11 + ArrayGap.getValue(), new Color(0, 0, 0, Arraybackground.getValue().intValue()).getRGB());
                        }
                    } else {
                        if (arryfont) {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + fontarry.getStringWidth(name) + 1, nextY + m.posYRend + 11 + ArrayGap.getValue(), new Color(0, 0, 0, Arraybackground.getValue().intValue()).getRGB());
                        } else {
                            RenderUtil.drawRect(x - 4, nextY + m.posYRend, x + mc.fontRendererObj.getStringWidth(name) + 1, nextY + m.posYRend + 11 + ArrayGap.getValue(), new Color(0, 0, 0, Arraybackground.getValue().intValue()).getRGB());
                        }
                    }

                    if (RectMode.getValue().equals(RectModes.Full)) {
                        double i = (lastX - 4 - 1) - (lastX - 3 - 1 - (x - 4 - 1));
                        boolean b = (lastX - 4 - 1) > i;
                        if (arryfont) {
                            if (first) {
                                RenderUtil.drawRect(x - 5, 0, x + fontarry.getStringWidth(name), 1, color.getRGB());
                            }
                        } else {
                            if (first) {
                                RenderUtil.drawRect(x - 5, 0, x + mc.fontRendererObj.getStringWidth(name), 1, color.getRGB());
                            }
                        }
                        RenderUtil.drawRect(x - 5, nextY + m.posYRend, x - 4, nextY + m.posYRend + 11 + ArrayGap.getValue(), color.getRGB());
                        if (!first) {
                            RenderUtil.drawRect(lastX - 4 - 1 + (b ? 1 : 0), nextY + m.posYRend - 1 + (b ? 0 : 1), i + 1, nextY + m.posYRend + (b ? 0 : 1), color.getRGB());
                        }
                    } else if (RectMode.getValue().equals(RectModes.Left)) {
                        RenderUtil.drawRect(x - 5, nextY + m.posYRend, x - 4, nextY + m.posYRend + 11 + ArrayGap.getValue(), color.getRGB());
                    } else if (RectMode.getValue().equals(RectModes.Right)) {
                        RenderUtil.drawRect(x + fontarry.getStringWidth(name) + 1, nextY + m.posYRend, x + fontarry.getStringWidth(name) + 2, nextY + m.posYRend + 11 + ArrayGap.getValue(), color.getRGB());
                    }
                    if (!arryfont) {
                        width = mc.fontRendererObj.getStringWidth(name);
                        if (ArrayShadow.getValue())
                            mc.fontRendererObj.drawStringWithShadow(name, (float) (x - 1), (float) (nextY + m.posYRend + (ArrayGap.getValue() / 2) + 1), color.getRGB());
                        else
                            mc.fontRendererObj.drawString(name, (int) (x - 1), (int) (nextY + m.posYRend + (ArrayGap.getValue()) + 1), color.getRGB());
                    } else {
                        width = fontarry.getStringWidth(name);
                        if (ArrayShadow.getValue())
                            fontarry.drawStringWithShadow(name, x - 1, nextY + m.posYRend + (ArrayGap.getValue() / 2) + 3, color.getRGB());
                        else
                            fontarry.drawString(name, (float) (x - 1), (float) (nextY + m.posYRend + (ArrayGap.getValue() / 2) + 3), color.getRGB());
                    }
                    if (RectMode.getValue().equals(RectModes.Right)) {
                        width += 2;
                    }
                    lastX = x;
                    first = false;
                    h += 9.0F;
                    if (!arryfont) {
                        if ((m.getX() != width) && (m.getRender() && m.isEnabled())) {
                            m.setX(AnimationUtils.animate(width, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() > 0) && (m.getRender() && !m.isEnabled())) {
                            m.setX(AnimationUtils.animate(0, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() <= 0)) {
                            m.setRender(false);
                        }
                        nextY += 11 + ArrayGap.getValue();
                    } else {
                        if ((m.getX() != width) && (m.getRender() && m.isEnabled())) {
                            m.setX(AnimationUtils.animate(width, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() > -5) && (m.getRender() && !m.isEnabled())) {
                            m.setX(AnimationUtils.animate(-7, m.getX(), (14.4f / (float) Minecraft.getDebugFPS())));
                        }
                        if ((m.getX() <= -5)) {
                            m.setRender(false);
                        }
                        nextY += 11 + ArrayGap.getValue();
                    }
                    addY = nextY;
                    counter[0]++;
                    if (sorted.size() == counter[0]) {
                        if (RectMode.getValue().equals(RectModes.Full) || RectMode.getValue().equals(RectModes.Bottom)) {
                            RenderUtil.drawRect(lastX - (RectMode.getValue().equals(RectModes.Full) ? 4 : 3) - 1, nextY + m.posYRend, RenderUtil.width(), nextY + m.posYRend + 1, color.getRGB());
                        }
                    }
                }
            }
            int ping;
            String pings;
            double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            double lastDist = Math.sqrt(xDif * xDif + zDif * zDif) * 20.0D;
            String text2 = (EnumChatFormatting.GRAY + "Bps: " + EnumChatFormatting.WHITE + String.format("%.2f", lastDist) + "b/s");
            try {
                ping = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
            } catch (NullPointerException e) {
                ping = 0;
            }
            if (ping == 0) pings = "N/A";
            else
                pings = ping + "ms";
            if (mc.thePlayer != null) {
                String text = EnumChatFormatting.GRAY + "X:"
                        + EnumChatFormatting.WHITE + " " +
                        MathHelper.floor_double(mc.thePlayer.posX) + " " +
                        EnumChatFormatting.GRAY + "Y:"
                        + EnumChatFormatting.WHITE + " "
                        + MathHelper.floor_double(mc.thePlayer.posY) + " " +
                        EnumChatFormatting.GRAY
                        + "Z:" + EnumChatFormatting.WHITE
                        + " " + MathHelper.floor_double(mc.thePlayer.posZ);
                int ychat;
                //String vertext = (Object)((Object)EnumChatFormatting.WHITE) + Client.RELTYPE + (Object)((Object)EnumChatFormatting.GRAY) + "-" + Client.RELclientVersion;
                ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10;
                if (this.info.getValue()) {
                    //font2.drawStringWithShadow(vertext, new ScaledResolution(this.mc).getScaledWidth() - font2.getStringWidth(vertext) - 2, new ScaledResolution(this.mc).getScaledHeight() - font.getHeight() + y - 12 - ychat,new Color(255,255,255).getRGB());
                    if (!(logomode.getValue().equals(logomodeE.Novoline))) {
                        font.drawCenteredStringWithShadow(text, sr.getScaledWidth() / 2f, font.getStringHeight(text) + 5, new Color(12, 12, 17).getRGB());
                        font.drawStringWithShadow(EnumChatFormatting.GRAY + "FPS: " + EnumChatFormatting.WHITE + Minecraft.debugFPS + EnumChatFormatting.GRAY + " Ping: " + EnumChatFormatting.WHITE + pings
                                + " " + text2, 4.0, sr.getScaledHeight() - ychat, Colors.WHITE.c);
                    }
                    this.drawPotionStatus(sr);
                }
            }
        }
    }

    public static int PotY;

    private void drawPotionStatus(ScaledResolution sr) {
        int y = 0;
        FastUniFontRenderer fonts = Client.FontLoaders.Chinese16;
        Iterator<PotionEffect> localIterator1 = mc.thePlayer.getActivePotionEffects().iterator();
        List<PotionEffect> myList = Lists.newArrayList(localIterator1);
        myList.sort((o1, o2) -> {
            String str1 = I18n.format(o1.getEffectName());
            str1 = str1 + getAmplifier(o1.getAmplifier());

            String str2 = I18n.format(o2.getEffectName());
            str2 = str2 + getAmplifier(o2.getAmplifier());

            if (o1.getDuration() < 600 && o1.getDuration() > 300) {
                str1 = str1 + "\u00a77:\u00a76 " + Potion.getDurationString(o1);
            } else if (o1.getDuration() < 300) {
                str1 = str1 + "\u00a77:\u00a7c " + Potion.getDurationString(o1);
            } else if (o1.getDuration() > 600) {
                str1 = str1 + "\u00a77:\u00a77 " + Potion.getDurationString(o1);
            }
            if (o2.getDuration() < 600 && o2.getDuration() > 300) {
                str2 = str2 + "\u00a77:\u00a76 " + Potion.getDurationString(o2);
            } else if (o2.getDuration() < 300) {
                str2 = str2 + "\u00a77:\u00a7c " + Potion.getDurationString(o2);
            } else if (o2.getDuration() > 600) {
                str2 = str2 + "\u00a77:\u00a77 " + Potion.getDurationString(o2);
            }
            return fonts.getStringWidth(str1)
                    - fonts.getStringWidth(str2);
        });
        Collections.reverse(myList);
        for (PotionEffect effect : myList) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName());
            PType = PType + getAmplifier(effect.getAmplifier());
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = PType + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = PType + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = PType + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : -10;
            fonts.drawStringWithShadow(PType, sr.getScaledWidth() - fonts.getStringWidth(PType) - 2, sr.getScaledHeight() - fonts.FONT_HEIGHT + y - 10 - ychat, potion.getLiquidColor());
            y -= 10;
        }
        PotY = y;
    }

    @EventHandler
    public void onDisable() {
        PotY = 0;
    }

    public String getAmplifier(int count) {
        String Amplifier = "";
        switch (count) {
            case 0: {
                Amplifier = " I";
                break;
            }
            case 1: {
                Amplifier = " II";
                break;
            }
            case 2: {
                Amplifier = " III";
                break;
            }
            case 3: {
                Amplifier = " IV";
                break;
            }
            case 4: {
                Amplifier = " V";
                break;
            }
            case 5: {
                Amplifier = " VI";
                break;
            }
            case 6: {
                Amplifier = " VII";
                break;
            }
            case 7: {
                Amplifier = " VIII";
                break;
            }
            case 8: {
                Amplifier = " IX";
                break;
            }
            case 9: {
                Amplifier = " X";
                break;
            }
        }
        if (count > 9 || count == -1) {
            Amplifier = " X+";
        }
        return Amplifier;
    }

    public enum logomodeE {
        Novoline,
        Jigsaw,
        Distance,
        Dark_Distance

    }

    public enum ArrayFont {
        GoogleSans16,
        GoogleSans18,
        Jello18,
        Jello16,
        Baloo16,
        Baloo18,
        Minecraft
    }

    public enum ArrayModeE {
        Wave,
        Rainbow,
        Rainbow2,
        NEON,
        BlueIceSakura,
        None
    }

    public enum RectModes {
        None,
        Full,
        Right,
        Left,
        Bottom
    }

    public enum widgetE {
        None(0,0,0),
        Astolfo(0,0,0),
        Widget_1(1,505,512),
        Widget_2(2,494,512),
        Widget_3(3,489,512),
        Widget_4(4,464,512),
        Widget_5(5,512,493),
        Widget_6(6,505,512),
        Widget_7(7,428,512),
        Widget_8(8,460,512),
        Widget_9(9,512,472),
        Widget_10(10,486,512),
        Widget_11(11,489,512),
        Widget_12(12,446,512),
        Widget_13(13,512,480),
        Widget_14(14,493,512),
        Widget_15(15,512,489),
        Widget_16(16,512,518),
        Widget_17(17,512,485),
        Widget_18(18,512,500),
        Widget_19(19,512,485),
        Widget_20(20,512,482),
        Widget_21(21,512,509);

        final int width;
        final int height;
        final int id;
        widgetE(int id, int width,int height){
            this.id = id;
            this.width = width;
            this.height = height;
        }
    }
}

