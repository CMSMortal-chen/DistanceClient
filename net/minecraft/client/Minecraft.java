package net.minecraft.client;

import com.google.common.collect.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.guichaguri.betterfps.BetterFpsClient;
import me.guichaguri.betterfps.BetterFpsConfig;
import my.distance.Client;
import my.distance.api.EventBus;
import my.distance.api.events.Misc.EventKey;
import my.distance.api.events.World.EventAttack;
import my.distance.api.events.World.EventTick;
import my.distance.api.events.World.EventWorldChanged;
import my.distance.api.verify.SHWID;
import my.distance.ui.gui.mainmenu.GuiMainMenu;
import my.distance.ui.gui.GuiNeedBlur;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.ClientSetting;
import my.distance.util.SuperLib;
import my.distance.util.render.RenderUtil;
import my.distance.util.sound.SoundFxPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.shaders.Shaders;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import viamcp.ViaMCP;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Minecraft implements IThreadListener, IPlayerUsage {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;

    /* A 10MiB preallocation to ensure the heap is reasonably sized. */ //removed

    private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;

    /**
     * The player's GameProfile properties
     */
    private final PropertyMap profileProperties;
    private ServerData currentServerData;

    /**
     * The RenderEngine instance used by Minecraft
     */
    public TextureManager renderEngine;

    /**
     * Set to 'this' in Minecraft constructor; used by some settings get methods
     */
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean enableGLErrorChecking = true;
    private boolean hasCrashed;

    /**
     * Instance of CrashReport.
     */
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;

    /**
     * True if the player is connected to a realms server
     */
    private boolean connectedToRealms = false;
    public Timer timer = new Timer(20.0F);

    /**
     * Instance of PlayerUsageSnooper.
     */
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient theWorld;
    public WorldClient prevWorld;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP thePlayer;
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    public Session session;
    private boolean isGamePaused;

    /**
     * The font renderer used for displaying and measuring text
     */
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;

    /**
     * The GuiScreen that's being displayed at the moment.
     */
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;

    /**
     * Mouse left click counter
     */
    public int leftClickCounter;

    /**
     * Display width
     */
    private int tempDisplayWidth;

    /**
     * Display height
     */
    private int tempDisplayHeight;

    /**
     * Instance of IntegratedServer.
     */
    private IntegratedServer theIntegratedServer;

    /**
     * Gui achievement
     */
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;

    /**
     * Skip render world
     */
    public boolean skipRenderWorld;

    /**
     * The ray trace hit that the mouse is over.
     */
    public MovingObjectPosition objectMouseOver;

    /**
     * The game settings that currently hold effect.
     */
    public GameSettings gameSettings;

    /**
     * Mouse helper instance.
     */
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;

    /**
     * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
     * the usage snooping.
     */
    public static int debugFPS;

    /**
     * When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block.
     */
    public int rightClickDelayTimer;
    private String serverName;
    private int serverPort;

    /**
     * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
     */
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();

    /**
     * Join player counter
     */
    private int joinPlayerCounter;

    /**
     * The FrameTimer's instance
     */
    public final FrameTimer frameTimer = new FrameTimer();

    /**
     * Time in nanoseconds of when the class is loaded
     */
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;

    /**
     * The profiler instance
     */
    public final Profiler mcProfiler = new Profiler();

    /**
     * Keeps track of how long the debug crash keycombo (F3+C) has been pressed for, in order to crash after 10 seconds.
     */
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private IStream stream;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
    private long field_175615_aJ = 0L;
    private final Thread mcThread = Thread.currentThread();
    public ModelManager modelManager;

    /**
     * The BlockRenderDispatcher instance that will be used based off gamesettings
     */
    private BlockRendererDispatcher blockRenderDispatcher;

    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
     */
    volatile boolean running = true;

    /**
     * String that shows the debug information
     */
    public String debug = "";
    public boolean field_175613_B = false;
    public boolean field_175614_C = false;
    public boolean field_175611_D = false;
    public boolean renderChunksMany = true;

    /**
     * Approximate time (in ms) of last update to debug string
     */
    long debugUpdateTime = getSystemTime();

    /**
     * holds the current fps
     */
    int fpsCounter;
    long prevFrameTime = -1L;

    /**
     * Profiler currently displayed in the debug screen pie chart
     */
    private String debugProfilerName = "root";
    private long lastFrame = getTime();

    public Minecraft(GameConfiguration gameConfig) {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex)).getResourceMap());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = (new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        logger.info("Setting user: " + this.session.getUsername());
        logger.info("(Session ID is " + this.session.getSessionID() + ")");
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = 1280;
        this.displayHeight = 720;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        if (IntegratedServer.USER_CACHE_FILE.exists()){
            IntegratedServer.USER_CACHE_FILE.delete();
        }
        this.theIntegratedServer = new IntegratedServer(this);

        if (gameConfig.serverInfo.serverName != null) {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public void run() {
        this.running = true;

        try {
            this.startGame();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }


        try {
            while (this.running) {
                if (!this.hasCrashed || this.crashReporter == null) {

                    try {
                        this.runGameLoop();
                    } catch (Throwable var10) {
                        var10.printStackTrace();
                    }

                    continue;
                }

                this.displayCrashReport(this.crashReporter);
            }
        } catch (MinecraftError var12) {
            var12.printStackTrace();

        } catch (ReportedException reportedexception) {
            reportedexception.printStackTrace();
            this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
            this.freeMemory();
            logger.fatal("Reported exception thrown!", reportedexception);
            this.displayCrashReport(reportedexception.getCrashReport());

        } catch (Throwable throwable1) {
            throwable1.printStackTrace();
            CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
            this.freeMemory();
            logger.fatal("Unreported exception thrown!", throwable1);
            this.displayCrashReport(crashreport1);

        } finally {
            this.shutdownMinecraftApplet();
        }
    }


    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void startGame() throws LWJGLException {
        SHWID.verify();
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
            new java.util.Timer().schedule(new TimerTask() {
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
            new java.util.Timer().schedule(new TimerTask() {
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
        try {
            System.out.printf("////////%s is Start Load ViaVersion////////////%n", Client.name);
            System.out.printf("%s正在加载跨版本模块可能会导致一些bug%n", Client.name);
            ViaMCP.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        logger.info("LWJGL Version: " + Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        Shaders.startup(this);
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(0);
        this.initStream();
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null) {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(str -> {
            try {
                return String.format(str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception exception) {
                return "Error: " + exception.getLocalizedMessage();
            }
        });
        this.mouseHelper = new MouseHelper();
        this.drawSplashScreen(2);
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        this.drawSplashScreen(4);
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.drawSplashScreen(6);
        this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        this.drawSplashScreen(8);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.drawSplashScreen(10);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        this.drawSplashScreen(12);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        Client.instance.initiate();
        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Startup, -8);
        this.displayGuiScreen(new GuiNeedBlur());

        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }

        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        } catch (OpenGLException var2) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        this.renderGlobal.makeEntityOutlineShader();
        BetterFpsClient.start(this);
    }

    private void registerMetadataSerializers() {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void initStream() {
        try {
            this.stream = new TwitchStream(this, Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), (Property) null));
        } catch (Throwable throwable) {
            this.stream = new NullStream(throwable);
            logger.error("Couldn't initialize twitch stream");
        }
    }

    private void createDisplay() throws LWJGLException {
        Display.setResizable(true);
        Display.setTitle(String.format("Minecraft 1.8.9 | %s", Client.author));

        try {
            Display.create((new PixelFormat()).withDepthBits(24));
        } catch (LWJGLException lwjglexception) {
            logger.error("Couldn't set pixel format", (Throwable) lwjglexception);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var3) {
            }

            if (this.fullscreen) {
                this.updateDisplayMode();
            }

            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException {
        if (this.fullscreen) {
            Display.setFullscreen(true);
            DisplayMode displaymode = Display.getDisplayMode();
            this.displayWidth = Math.max(1, displaymode.getWidth());
            this.displayHeight = Math.max(1, displaymode.getHeight());
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    private void setWindowIcon() {
        Util.EnumOS util$enumos = Util.getOSType();

        if (util$enumos != Util.EnumOS.OSX) {
            InputStream inputstream = null;
            InputStream inputstream1 = null;

            try {
                inputstream = this.mcDefaultResourcePack.getResourceStream(new ResourceLocation("Distance/ICON/LaunchIcon.png"));
                inputstream1 = this.mcDefaultResourcePack.getResourceStream(new ResourceLocation("Distance/ICON/LaunchIcon.png"));

                if (inputstream != null && inputstream1 != null) {
                    Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
                }
            } catch (IOException ioexception) {
                logger.error((String) "Couldn\'t set icon", (Throwable) ioexception);
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(inputstream1);
            }
        }
    }

    private static boolean isJvm64bit() {
        String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String s : astring) {
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64")) {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    public String getVersion() {
        return this.launchedVersion;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException var2) {
                        ;
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash) {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport crashReportIn) {
        File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());

        if (crashReportIn.getFile() != null) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        } else if (crashReportIn.saveToFile(file2)) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode() {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(resourcepackrepository$entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try {
            this.mcResourceManager.reloadResources(list);
        } catch (RuntimeException runtimeexception) {
            logger.info("Caught error stitching, removing all assigned resourcepacks", (Throwable) runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(list);

        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[]) null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException {
        Set<DisplayMode> set = Sets.newHashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();

        if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX) {
            label52:

            for (DisplayMode displaymode1 : macDisplayModes) {
                boolean flag = true;

                for (DisplayMode displaymode2 : set) {
                    if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight()) {
                        flag = false;
                        break;
                    }
                }

                if (!flag) {
                    Iterator iterator = set.iterator();
                    DisplayMode displaymode3;

                    while (true) {
                        if (!iterator.hasNext()) {
                            continue label52;
                        }

                        displaymode3 = (DisplayMode) iterator.next();

                        if (displaymode3.getBitsPerPixel() == 32 && displaymode3.getWidth() == displaymode1.getWidth() / 2 && displaymode3.getHeight() == displaymode1.getHeight() / 2) {
                            break;
                        }
                    }

                    displaymode = displaymode3;
                }
            }
        }

        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    public void drawSplashScreen(int much) {
        ScaledResolution sr = new ScaledResolution(this);
        int i = sr.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(sr.getScaledWidth() * i, sr.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Color.BLACK.getRGB());
        //FontManager.regular40.drawCenteredString(Client.INSTANCE.getName(), sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 15, -1);
        drawRect(sr.getScaledWidth() / 2 - 30, sr.getScaledHeight() / 2 + 9, sr.getScaledWidth() / 2 + 30, sr.getScaledHeight() / 2 + 10, Color.GRAY.getRGB());
        drawRect(sr.getScaledWidth() / 2 - 30, sr.getScaledHeight() / 2 + 9, sr.getScaledWidth() / 2 - 30 + much, sr.getScaledHeight() / 2 + 10, Color.WHITE.getRGB());
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * i, sr.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        much = much / 10;
        Display.setTitle(String.format("Minecraft 1.8.9 | %s !正在加载，很快就好 " + much + "/6", Client.name));
        this.updateDisplay();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draw with the WorldRenderer
     *
     * @param posX   X position for the render
     * @param posY   Y position for the render
     * @param texU   X position for the texture
     * @param texV   Y position for the texture
     * @param width  Width of the render
     * @param height Height of the render
     * @param red    The red component of the render's color
     * @param green  The green component of the render's color
     * @param blue   The blue component of the render's color
     * @param alpha  The alpha component of the render's color
     */
    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos((double) posX, (double) (posY + height), 0.0D).tex((double) ((float) texU * f), (double) ((float) (texV + height) * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double) (posX + width), (double) (posY + height), 0.0D).tex((double) ((float) (texU + width) * f), (double) ((float) (texV + height) * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double) (posX + width), (double) posY, 0.0D).tex((double) ((float) (texU + width) * f), (double) ((float) texV * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double) posX, (double) posY, 0.0D).tex((double) ((float) texU * f), (double) ((float) texV * f1)).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen guiScreenIn) {
        if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
        }

        if (guiScreenIn == null && this.theWorld == null) {
            guiScreenIn = new GuiMainMenu();
        } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
            guiScreenIn = new GuiGameOver();
        }

        if (guiScreenIn instanceof GuiMainMenu) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = guiScreenIn;

        if (guiScreenIn != null) {
            this.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(this);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            guiScreenIn.setWorldAndResolution(this, i, j);
            this.skipRenderWorld = false;
        } else {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String message) {
        if (this.enableGLErrorChecking) {
            int i = GL11.glGetError();

            if (i != 0) {
                String s = GLU.gluErrorString(i);
                logger.error("########## GL ERROR ##########");
                logger.error("@ " + message);
                logger.error(i + ": " + s);
            }
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet() {
        Client.save();
        try {
            this.stream.shutdownStream();
            logger.info("Stopping!");

            try {
                this.loadWorld((WorldClient) null);
            } catch (Throwable var5) {
                ;
            }

            this.mcSoundHandler.unloadSounds();
        } finally {
            Display.destroy();

            if (!this.hasCrashed) {
                System.exit(0);
            }
        }

        System.gc();
    }

    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop() throws IOException {
        if (Client.isLoaded) {
            Display.setTitle(Client.title);
        }

        long i = System.nanoTime();
        final long currentTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        final double deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        RenderUtil.delta = (float) deltaTime;

        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        } else {
            this.timer.updateTimer();
        }

        this.mcProfiler.startSection("scheduledExecutables");

        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask(this.scheduledTasks.poll(), logger);
            }
        }

        this.mcProfiler.endSection();
        long l = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int j = 0; j < this.timer.elapsedTicks; ++j) {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long i1 = System.nanoTime() - l;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, i);
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(i1);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        Notifications.getManager().updateAndRender();
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.stream.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        this.stream.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long k = System.nanoTime();
        this.frameTimer.addFrame(k - this.startNanoTime);
        this.startNanoTime = k;

        while (getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", debugFPS, RenderChunk.renderChunksUpdated, RenderChunk.renderChunksUpdated != 1 ? "s" : "", (float) this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate, this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : "");
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning()) {
                this.usageSnooper.startSnooper();
            }
        }

        if (this.isFramerateLimitBelowMax()) {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
    }

    public void updateDisplay() {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize() {
        if (!this.fullscreen && Display.wasResized()) {
            int i = this.displayWidth;
            int j = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != i || this.displayHeight != j) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.theWorld == null && this.currentScreen != null ? 120 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            this.renderGlobal.deleteAllDisplayLists();
        } catch (Throwable var3) {
        }

        try {
            System.gc();
            this.loadWorld((WorldClient) null);
        } catch (Throwable var2) {
        }

        System.gc();
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int keyCount) {
        List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (list != null && !list.isEmpty()) {
            Profiler.Result profiler$result = list.remove(0);

            if (keyCount == 0) {
                if (profiler$result.field_76331_c.length() > 0) {
                    int i = this.debugProfilerName.lastIndexOf(".");

                    if (i >= 0) {
                        this.debugProfilerName = this.debugProfilerName.substring(0, i);
                    }
                }
            } else {
                --keyCount;

                if (keyCount < list.size() && !(list.get(keyCount)).field_76331_c.equals("unspecified")) {
                    if (this.debugProfilerName.length() > 0) {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + (list.get(keyCount)).field_76331_c;
                }
            }
        }
    }

    /**
     * Parameter appears to be unused
     */
    private void displayDebugInfo(long elapsedTicksTime) {
        if (this.mcProfiler.profilingEnabled) {
            List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result profiler$result = list.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i = 160;
            int j = this.displayWidth - i - 10;
            int k = this.displayHeight - i * 2;
            GlStateManager.enableBlend();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d0 = 0.0D;

            for (int l = 0; l < list.size(); ++l) {
                Profiler.Result profiler$result1 = list.get(l);
                int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0D) + 1;
                worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int j1 = profiler$result1.getColor();
                int k1 = j1 >> 16 & 255;
                int l1 = j1 >> 8 & 255;
                int i2 = j1 & 255;
                worldrenderer.pos((double) j, (double) k, 0.0D).color(k1, l1, i2, 255).endVertex();

                for (int j2 = i1; j2 >= 0; --j2) {
                    float f = (float) ((d0 + profiler$result1.field_76332_a * (double) j2 / (double) i1) * (double) (float) Math.PI * 2.0D / 100.0D);
                    float f1 = MathHelper.sin(f) * (float) i;
                    float f2 = MathHelper.cos(f) * (float) i * 0.5F;
                    worldrenderer.pos((double) ((float) j + f1), (double) ((float) k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
                }

                tessellator.draw();
                worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int i3 = i1; i3 >= 0; --i3) {
                    float f3 = (float) ((d0 + profiler$result1.field_76332_a * (double) i3 / (double) i1) * (double) (float) Math.PI * 2.0D / 100.0D);
                    float f4 = MathHelper.sin(f3) * (float) i;
                    float f5 = MathHelper.cos(f3) * (float) i * 0.5F;
                    worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                    worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                }

                tessellator.draw();
                d0 += profiler$result1.field_76332_a;
            }

            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String s = "";

            if (!profiler$result.field_76331_c.equals("unspecified")) {
                s = s + "[0] ";
            }

            if (profiler$result.field_76331_c.length() == 0) {
                s = s + "ROOT ";
            } else {
                s = s + profiler$result.field_76331_c + " ";
            }

            int l2 = 16777215;
            this.fontRendererObj.drawStringWithShadow(s, (float) (j - i), (float) (k - i / 2 - 16), l2);
            this.fontRendererObj.drawStringWithShadow(s = decimalformat.format(profiler$result.field_76330_b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s)), (float) (k - i / 2 - 16), l2);

            for (int k2 = 0; k2 < list.size(); ++k2) {
                Profiler.Result profiler$result2 = list.get(k2);
                String s1 = "";

                if (profiler$result2.field_76331_c.equals("unspecified")) {
                    s1 = s1 + "[?] ";
                } else {
                    s1 = s1 + "[" + (k2 + 1) + "] ";
                }

                s1 = s1 + profiler$result2.field_76331_c;
                this.fontRendererObj.drawStringWithShadow(s1, (float) (j - i), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
                this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76332_a) + "%", (float) (j + i - 50 - this.fontRendererObj.getStringWidth(s1)), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
                this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76330_b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s1)), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown() {
        Client.instance.shutDown();
        this.running = false;
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus() {
        if (Display.isActive()) {
            if (!this.inGameHasFocus) {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen) null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
     */
    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick) {
        if (!leftClick) {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();

                if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    public void clickMouse() {
        if (this.leftClickCounter <= 0) {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null) {
                logger.error("Null returned as 'hitResult', this shouldn't happen!");

                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        EventAttack ej = new EventAttack(this.objectMouseOver.entityHit, true);
                        EventBus.getInstance().register(ej);
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;

                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:
                        if (this.playerController.isNotCreative()) {
                            this.leftClickCounter = 10;
                        }
                }
            }
        }
    }

    /**
     * Called when user clicked he's mouse right button (place)
     */
    public void rightClickMouse() {
        if (!this.playerController.getIsHittingBlock()) {
            this.rightClickDelayTimer = 4;
            boolean flag = true;
            ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();

            if (this.objectMouseOver == null) {
                logger.warn("Null returned as 'hitResult', this shouldn't happen!");
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                            flag = false;
                        } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                            flag = false;
                        }

                        break;

                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            int i = itemstack != null ? itemstack.stackSize : 0;

                            if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                                flag = false;
                                this.thePlayer.swingItem();
                            }

                            if (itemstack == null) {
                                return;
                            }

                            if (itemstack.stackSize == 0) {
                                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            } else if (itemstack.stackSize != i || this.playerController.isInCreativeMode()) {
                                this.entityRenderer.itemRenderer.resetEquippedProgress();
                            }
                        }
                }
            }

            if (flag) {
                ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();

                if (itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;
            this.gameSettings.fullScreen = this.fullscreen;

            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        } catch (Exception exception) {
            logger.error("Couldn't toggle fullscreen", (Throwable) exception);
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int width, int height) {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);

        if (this.currentScreen != null) {
            ScaledResolution scaledresolution = new ScaledResolution(this);
            this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Return the musicTicker's instance
     */
    public MusicTicker getMusicTicker() {
        return this.mcMusicTicker;
    }

    /**
     * Runs the current tick.
     */
    public void runTick() throws IOException {
        if (ClientSetting.resetBF.getValue()) {
            new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.KeyConfirm, 5f);
            ClientSetting.resetBF.setValue(false);
            BetterFpsConfig.getConfig().algorithm = ((ClientSetting.betterfps) ClientSetting.betterFPS.getValue()).get();
        }


        if (this.thePlayer != null) {
            EventBus.getInstance().register(new EventTick());
        }
        if (this.rightClickDelayTimer > 0) {
            --this.rightClickDelayTimer;
        }

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused) {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endSection();
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.startSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null) {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused) {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null) {
            if (this.thePlayer.getHealth() <= 0.0F) {
                this.displayGuiScreen((GuiScreen) null);
            } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                this.displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
            this.displayGuiScreen((GuiScreen) null);
        }

        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
            } catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                    public String call() throws Exception {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                } catch (Throwable throwable) {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.addCrashSectionCallable("Screen name", new Callable<String>() {
                        public String call() throws Exception {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport1);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            this.mcProfiler.endStartSection("mouse");

            while (Mouse.next()) {
                int i = Mouse.getEventButton();
                KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState()) {
                    if (this.thePlayer.isSpectator() && i == 2) {
                        this.ingameGUI.getSpectatorGui().func_175261_b();
                    } else {
                        KeyBinding.onTick(i - 100);
                    }
                }

                long i1 = getSystemTime() - this.systemTime;

                if (i1 <= 200L) {
                    int j = Mouse.getEventDWheel();

                    if (j != 0) {
                        if (this.thePlayer.isSpectator()) {
                            j = j < 0 ? -1 : 1;

                            if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                                this.ingameGUI.getSpectatorGui().func_175259_b(-j);
                            } else {
                                float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float) j * 0.005F, 0.0F, 0.2F);
                                this.thePlayer.capabilities.setFlySpeed(f);
                            }
                        } else {
                            this.thePlayer.inventory.changeCurrentItem(j);
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                            this.setIngameFocus();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0) {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");

            while (Keyboard.next()) {
                int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onTick(k);
                }

                if (this.debugCrashKeyPressTime > 0L) {
                    if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                        this.debugCrashKeyPressTime = -1L;
                    }
                } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                    this.debugCrashKeyPressTime = getSystemTime();
                }

                this.dispatchKeypresses();

                if (Keyboard.getEventKeyState()) {
                    if (k == 62 && this.entityRenderer != null) {
                        this.entityRenderer.switchUseShader();
                    }

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        EventBus.getInstance().register(new EventKey(k));
                        if (k == 1) {
                            this.displayInGameMenu();
                        }

                        if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                            this.ingameGUI.getChatGUI().clearChatMessages();
                        }

                        if (k == 31 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }

                        if (k == 17 && Keyboard.isKeyDown(61)) {
                            ;
                        }

                        if (k == 18 && Keyboard.isKeyDown(61)) {
                            ;
                        }

                        if (k == 47 && Keyboard.isKeyDown(61)) {
                            ;
                        }

                        if (k == 38 && Keyboard.isKeyDown(61)) {
                            ;
                        }

                        if (k == 22 && Keyboard.isKeyDown(61)) {
                            ;
                        }

                        if (k == 20 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }

                        if (k == 33 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                        }

                        if (k == 30 && Keyboard.isKeyDown(61)) {
                            this.renderGlobal.loadRenderers();
                        }

                        if (k == 35 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (k == 48 && Keyboard.isKeyDown(61)) {
                            this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                        }

                        if (k == 25 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (k == 59) {
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                        }

                        if (k == 61) {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                        }

                        if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                            ++this.gameSettings.thirdPersonView;

                            if (this.gameSettings.thirdPersonView > 2) {
                                this.gameSettings.thirdPersonView = 0;
                            }

                            if (this.gameSettings.thirdPersonView == 0) {
                                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                            } else if (this.gameSettings.thirdPersonView == 1) {
                                this.entityRenderer.loadEntityShader((Entity) null);
                            }

                            this.renderGlobal.setDisplayListEntitiesDirty();
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                        if (k == 11) {
                            this.updateDebugProfilerName(0);
                        }

                        for (int j1 = 0; j1 < 9; ++j1) {
                            if (k == 2 + j1) {
                                this.updateDebugProfilerName(j1 + 1);
                            }
                        }
                    }
                }
            }

            for (int l = 0; l < 9; ++l) {
                if (this.gameSettings.keyBindsHotbar[l].isPressed()) {
                    if (this.thePlayer.isSpectator()) {
                        this.ingameGUI.getSpectatorGui().func_175260_a(l);
                    } else {
                        this.thePlayer.inventory.currentItem = l;
                    }
                }
            }

            boolean flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.isRidingHorse()) {
                    this.thePlayer.sendHorseInventory();
                } else {
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed()) {
                if (!this.thePlayer.isSpectator()) {
                    this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                }
            }

            while (this.gameSettings.keyBindChat.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat());
            }
            if (this.currentScreen == null && Keyboard.isKeyDown(52) && flag) {
                this.displayGuiScreen(new GuiChat("."));
            }
            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem()) {
                if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                while (this.gameSettings.keyBindAttack.isPressed()) {
                    this.thePlayer.swingItem();
                }

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    ;
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    ;
                }
            } else {
                while (this.gameSettings.keyBindAttack.isPressed()) {
                    this.clickMouse();
                }

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    this.rightClickMouse();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    this.middleClickMouse();
                }
            }

            if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
                this.rightClickMouse();
            }

            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused) {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused) {
                if (this.theWorld.getLastLightningBolt() > 0) {
                    this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
                }

                this.theWorld.updateEntities();
            }
        } else if (this.entityRenderer.isShaderActive()) {
            this.entityRenderer.stopUseShader();
        }

        if (!this.isGamePaused) {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.theWorld != null) {
            if (!this.isGamePaused) {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                try {
                    this.theWorld.tick();
                } catch (Throwable throwable2) {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.theWorld == null) {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.theWorld.addWorldInfoToCrashReport(crashreport2);
                    }

                    throw new ReportedException(crashreport2);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null) {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();

        if ((prevWorld == null && theWorld != null)
                || (prevWorld != null && this.theWorld != null && prevWorld != this.theWorld)) {
            prevWorld = theWorld;
            EventBus.getInstance().register(new EventWorldChanged());
        }
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
        this.loadWorld((WorldClient) null);
        //       System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldSettingsIn != null) {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (worldSettingsIn == null) {
            worldSettingsIn = new WorldSettings(worldinfo);
        }

        try {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String s = this.theIntegratedServer.getUserMessage();

            if (s != null) {
                this.loadingScreen.displayLoadingString(I18n.format(s));
            } else {
                this.loadingScreen.displayLoadingString("");
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException var9) {
                ;
            }
        }

        this.displayGuiScreen((GuiScreen) null);
        SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, (GuiScreen) null));
        networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = networkmanager;
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient worldClientIn) {
        this.loadWorld(worldClientIn, "");
    }

    /**
     * par2Str is displayed on the loading screen to the user unloads the current world first
     */
    public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
        if (worldClientIn == null) {
            NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();

            if (nethandlerplayclient != null) {
                nethandlerplayclient.cleanup();
            }

            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
                this.theIntegratedServer.initiateShutdown();
                this.theIntegratedServer.setStaticInstance();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.theWorld != null) {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData((ServerData) null);
            this.integratedServerIsRunning = false;
        }

        this.mcSoundHandler.stopSounds();
        this.theWorld = worldClientIn;

        if (worldClientIn != null) {
            if (this.renderGlobal != null) {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }

            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(worldClientIn);
            }

            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;

            Client.instance.LoadedWorld = true;
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        //    System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension) {
        this.theWorld.setInitialSpawnLocation();
        this.theWorld.removeAllEntities();
        int i = 0;
        String s = null;

        if (this.thePlayer != null) {
            i = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            s = this.thePlayer.getClientBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.thePlayer;
        this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(s);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(i);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());

        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen((GuiScreen) null);
        }
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    /**
     * Called when user clicked he's mouse middle button (pick block)
     */
    private void middleClickMouse() {
        if (this.objectMouseOver != null) {
            boolean flag = this.thePlayer.capabilities.isCreativeMode;
            int i = 0;
            boolean flag1 = false;
            TileEntity tileentity = null;
            Item item;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if (block.getMaterial() == Material.air) {
                    return;
                }

                item = block.getItem(this.theWorld, blockpos);

                if (item == null) {
                    return;
                }

                if (flag && GuiScreen.isCtrlKeyDown()) {
                    tileentity = this.theWorld.getTileEntity(blockpos);
                }

                Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
                i = block1.getDamageValue(this.theWorld, blockpos);
                flag1 = item.getHasSubtypes();
            } else {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag) {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    item = Items.painting;
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    item = Items.lead;
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame entityitemframe = (EntityItemFrame) this.objectMouseOver.entityHit;
                    ItemStack itemstack = entityitemframe.getDisplayedItem();

                    if (itemstack == null) {
                        item = Items.item_frame;
                    } else {
                        item = itemstack.getItem();
                        i = itemstack.getMetadata();
                        flag1 = true;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
                    EntityMinecart entityminecart = (EntityMinecart) this.objectMouseOver.entityHit;

                    switch (entityminecart.getMinecartType()) {
                        case FURNACE:
                            item = Items.furnace_minecart;
                            break;

                        case CHEST:
                            item = Items.chest_minecart;
                            break;

                        case TNT:
                            item = Items.tnt_minecart;
                            break;

                        case HOPPER:
                            item = Items.hopper_minecart;
                            break;

                        case COMMAND_BLOCK:
                            item = Items.command_block_minecart;
                            break;

                        default:
                            item = Items.minecart;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    item = Items.boat;
                } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
                    item = Items.armor_stand;
                } else {
                    item = Items.spawn_egg;
                    i = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag1 = true;

                    if (!EntityList.entityEggs.containsKey(i)) {
                        return;
                    }
                }
            }

            InventoryPlayer inventoryplayer = this.thePlayer.inventory;

            if (tileentity == null) {
                inventoryplayer.setCurrentItem(item, i, flag1, flag);
            } else {
                ItemStack itemstack1 = this.pickBlockWithNBT(item, i, tileentity);
                inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
            }

            if (flag) {
                int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
                this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
            }
        }
    }

    /**
     * Return an ItemStack with the NBTTag of the TileEntity ("Owner" if the block is a skull)
     *
     * @param itemIn       The item from the block picked
     * @param meta         Metadata of the item
     * @param tileEntityIn TileEntity of the block picked
     */
    private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn) {
        ItemStack itemstack = new ItemStack(itemIn, 1, meta);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        tileEntityIn.writeToNBT(nbttagcompound);

        if (itemIn == Items.skull && nbttagcompound.hasKey("Owner")) {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            itemstack.setTagCompound(nbttagcompound3);
            return itemstack;
        } else {
            itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            itemstack.setTagInfo("display", nbttagcompound1);
            return itemstack;
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
            public String call() throws Exception {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
            public String call() {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
            public String call() {
                return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
            public String call() {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
            public String call() {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>() {
            public String call() throws Exception {
                String s = ClientBrandRetriever.getClientModName();

                if (!s.equals("vanilla")) {
                    return "Definitely; Client brand changed to '" + s + "'";
                } else {
                    return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
                }
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
            public String call() throws Exception {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
            public String call() throws Exception {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : Minecraft.this.gameSettings.resourcePacks) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);

                    if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s)) {
                        stringbuilder.append(" (incompatible)");
                    }
                }

                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
            public String call() throws Exception {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
            public String call() throws Exception {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
            public String call() {
                return OpenGlHelper.getCpu();
            }
        });

        if (this.theWorld != null) {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getMinecraft() {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh() {
        return this.addScheduledTask(new Runnable() {
            public void run() {
                Minecraft.this.refreshResources();
            }
        });
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("fps", debugFPS);
        playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
        playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s);
        playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int i = 0;

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    /**
     * Return the current action's name
     */
    private String getCurrentAction() {
        if (this.theIntegratedServer != null) {
            return this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer";
        } else if (this.currentServerData != null) {
            return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
        } else {
            return "out_of_game";
        }
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(GL11.GL_VERSION));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(GL11.GL_VENDOR));
        playerSnooper.addStatToSnooper("client_brand", "fml,forge");
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", contextcapabilities.GL_ARB_arrays_of_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", contextcapabilities.GL_ARB_base_instance);
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", contextcapabilities.GL_ARB_blend_func_extended);
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", contextcapabilities.GL_ARB_clear_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", contextcapabilities.GL_ARB_color_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", contextcapabilities.GL_ARB_compatibility);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", contextcapabilities.GL_ARB_compressed_texture_pixel_storage);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", contextcapabilities.GL_ARB_depth_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", contextcapabilities.GL_ARB_depth_texture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", contextcapabilities.GL_ARB_draw_buffers);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", contextcapabilities.GL_ARB_draw_buffers_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", contextcapabilities.GL_ARB_draw_elements_base_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", contextcapabilities.GL_ARB_draw_indirect);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", contextcapabilities.GL_ARB_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", contextcapabilities.GL_ARB_explicit_attrib_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", contextcapabilities.GL_ARB_explicit_uniform_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", contextcapabilities.GL_ARB_fragment_layer_viewport);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", contextcapabilities.GL_ARB_fragment_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", contextcapabilities.GL_ARB_fragment_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", contextcapabilities.GL_ARB_fragment_program_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", contextcapabilities.GL_ARB_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", contextcapabilities.GL_ARB_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", contextcapabilities.GL_ARB_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", contextcapabilities.GL_ARB_gpu_shader5);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", contextcapabilities.GL_ARB_half_float_pixel);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", contextcapabilities.GL_ARB_half_float_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", contextcapabilities.GL_ARB_instanced_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", contextcapabilities.GL_ARB_map_buffer_alignment);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", contextcapabilities.GL_ARB_map_buffer_range);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", contextcapabilities.GL_ARB_multisample);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", contextcapabilities.GL_ARB_multitexture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", contextcapabilities.GL_ARB_occlusion_query2);
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", contextcapabilities.GL_ARB_pixel_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", contextcapabilities.GL_ARB_seamless_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", contextcapabilities.GL_ARB_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", contextcapabilities.GL_ARB_shader_stencil_export);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", contextcapabilities.GL_ARB_shader_texture_lod);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", contextcapabilities.GL_ARB_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", contextcapabilities.GL_ARB_shadow_ambient);
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", contextcapabilities.GL_ARB_stencil_texturing);
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", contextcapabilities.GL_ARB_sync);
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", contextcapabilities.GL_ARB_tessellation_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", contextcapabilities.GL_ARB_texture_border_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", contextcapabilities.GL_ARB_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", contextcapabilities.GL_ARB_texture_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", contextcapabilities.GL_ARB_texture_cube_map_array);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", contextcapabilities.GL_ARB_texture_non_power_of_two);
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", contextcapabilities.GL_ARB_uniform_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", contextcapabilities.GL_ARB_vertex_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", contextcapabilities.GL_ARB_vertex_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", contextcapabilities.GL_ARB_vertex_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", contextcapabilities.GL_ARB_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", contextcapabilities.GL_EXT_bindable_uniform);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", contextcapabilities.GL_EXT_blend_equation_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", contextcapabilities.GL_EXT_blend_func_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", contextcapabilities.GL_EXT_blend_minmax);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", contextcapabilities.GL_EXT_blend_subtract);
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", contextcapabilities.GL_EXT_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", contextcapabilities.GL_EXT_framebuffer_multisample);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", contextcapabilities.GL_EXT_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", contextcapabilities.GL_EXT_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", contextcapabilities.GL_EXT_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", contextcapabilities.GL_EXT_gpu_program_parameters);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", contextcapabilities.GL_EXT_gpu_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", contextcapabilities.GL_EXT_multi_draw_arrays);
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", contextcapabilities.GL_EXT_packed_depth_stencil);
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", contextcapabilities.GL_EXT_paletted_texture);
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", contextcapabilities.GL_EXT_rescale_normal);
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", contextcapabilities.GL_EXT_separate_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", contextcapabilities.GL_EXT_shader_image_load_store);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", contextcapabilities.GL_EXT_shadow_funcs);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", contextcapabilities.GL_EXT_shared_texture_palette);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", contextcapabilities.GL_EXT_stencil_clear_tag);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", contextcapabilities.GL_EXT_stencil_two_side);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", contextcapabilities.GL_EXT_stencil_wrap);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", contextcapabilities.GL_EXT_texture_3d);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", contextcapabilities.GL_EXT_texture_array);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", contextcapabilities.GL_EXT_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", contextcapabilities.GL_EXT_texture_integer);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", contextcapabilities.GL_EXT_texture_lod_bias);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", contextcapabilities.GL_EXT_texture_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", contextcapabilities.GL_EXT_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", contextcapabilities.GL_EXT_vertex_weighting);
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
    }

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize() {
        for (int i = 16384; i > 0; i >>= 1) {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            int j = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (j != 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled() {
        return this.gameSettings.snooperEnabled;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData serverDataIn) {
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() {
        if (theMinecraft != null) {
            IntegratedServer integratedserver = theMinecraft.getIntegratedServer();

            if (integratedserver != null) {
                integratedserver.stopServer();
            }
        }
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Session getSession() {
        return this.session;
    }

    public PropertyMap getTwitchDetails() {
        return this.twitchDetails;
    }

    /**
     * Return the player's GameProfile properties
     */
    public PropertyMap getProfileProperties() {
        if (this.profileProperties.isEmpty()) {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType() {
        if (this.thePlayer != null) {
            if (this.thePlayer.worldObj.provider instanceof WorldProviderHell) {
                return MusicTicker.MusicType.NETHER;
            } else if (this.thePlayer.worldObj.provider instanceof WorldProviderEnd) {
                return BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
            } else {
                return this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
            }
        } else {
            return MusicTicker.MusicType.MENU;
        }
    }

    public IStream getTwitchStream() {
        return this.stream;
    }

    public void dispatchKeypresses() {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (i != 0 && !Keyboard.isRepeatEvent()) {
            if (!(this.currentScreen instanceof GuiControls) || ((GuiControls) this.currentScreen).time <= getSystemTime() - 20L) {
                if (Keyboard.getEventKeyState()) {
                    if (i == this.gameSettings.keyBindStreamStartStop.getKeyCode()) {
//                        if (this.getTwitchStream().isBroadcasting())
//                        {
//                            this.getTwitchStream().stopBroadcasting();
//                        }
//                        else if (this.getTwitchStream().isReadyToBroadcast())
//                        {
//                            this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
//                            {
//                                public void confirmClicked(boolean result, int id)
//                                {
//                                    if (result)
//                                    {
//                                        Minecraft.this.getTwitchStream().func_152930_t();
//                                    }
//
//                                    Minecraft.this.displayGuiScreen((GuiScreen)null);
//                                }
//                            }, I18n.format("stream.confirm_start"), "", 0));
//                        }
//                        else if (this.getTwitchStream().func_152928_D() && this.getTwitchStream().func_152936_l())
//                        {
//                            if (this.theWorld != null)
//                            {
//                                this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
//                            }
//                        }
//                        else
//                        {
//                            GuiStreamUnavailable.func_152321_a(this.currentScreen);
//                        }
                    } else if (i == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode()) {
//                        if (this.getTwitchStream().isBroadcasting())
//                        {
//                            if (this.getTwitchStream().isPaused())
//                            {
//                                this.getTwitchStream().unpause();
//                            }
//                            else
//                            {
//                                this.getTwitchStream().pause();
//                            }
//                        }
                    } else if (i == this.gameSettings.keyBindStreamCommercials.getKeyCode()) {
//                        if (this.getTwitchStream().isBroadcasting())
//                        {
//                            this.getTwitchStream().requestCommercial();
//                        }
                    } else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
//                        this.stream.muteMicrophone(true);
                    } else if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
                        this.toggleFullscreen();
                    } else if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
                        this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                    }
                } else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
//                    this.stream.muteMicrophone(false);
                }
            }
        }
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity) {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
        Validate.notNull(callableToSchedule);

        if (!this.isCallingFromMinecraftThread()) {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);

            synchronized (this.scheduledTasks) {
                this.scheduledTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        } else {
            try {
                return Futures.immediateFuture(callableToSchedule.call());
            } catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher() {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public RenderItem getRenderItem() {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public static int getDebugFPS() {
        return debugFPS;
    }

    /**
     * Return the FrameTimer's instance
     */
    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }

    public static Map<String, String> getSessionInfo() {
        Map<String, String> map = Maps.newHashMap();
        map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
        map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
        map.put("X-Minecraft-Version", "1.8.9");
        return map;
    }

    /**
     * Return true if the player is connected to a realms server
     */
    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    /**
     * Set if the player is connected to a realms server
     *
     * @param isConnected The value that set if the player is connected to a realms server or not
     */
    public void setConnectedToRealms(boolean isConnected) {
        this.connectedToRealms = isConnected;
    }
}
