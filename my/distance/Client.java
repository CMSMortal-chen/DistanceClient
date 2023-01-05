package my.distance;

import my.distance.api.value.Value;
import my.distance.manager.CommandManager;
import my.distance.manager.FileManager;
import my.distance.manager.FriendManager;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.modules.render.HUD;
import my.distance.module.modules.render.tabguis.ETBTabUI;
import my.distance.module.modules.render.tabguis.TabUI;
import my.distance.ui.gui.GuiBaned;
import my.distance.ui.gui.GuiGoodBye;
import my.distance.ui.login.AltManager;
import my.distance.ui.fontrenderer.FontManager;
import my.distance.util.ClientSetting;
import my.distance.util.misc.Helper;
import my.distance.util.misc.liquidbounce.RotationUtils;
import my.distance.fastuni.FontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


/**
 * Distance Client
 *
 * @author Mymylesaws(MortalChen)
 */
public class Client {
	public static float Yaw;
	public static float Pitch;
	public static boolean Baned = false;
	public final static String name = "Distance";
	public final static String version = "5.81";
	public static String author = "TeamAtmosphere";
	public static String releaseNumber = "220509";
	public static String releaseType = Main.isbeta? "Beta":"Release";
	public static String releaseVersion = releaseType + version + "_" + releaseNumber;
	public static String distanceVersion = Main.isbeta? "B" + version :"V" + version;
	public static Client instance = new Client();
	public static String title;
	public static boolean isLoaded = false;
	public static String words;
	public static String User;
	public static String userName;
	public static String Pass;
	private ModuleManager modulemanager;
	public static final String[] word = {
			"仍然追不上你的脚步 甚至无法在你身旁守候",
			"已经有很多了 曾经一次又一次幻灭的希望之光。",
			"单手拿着无法弹奏的吉他，我为你唱点什么好呢？",
			"那是曾梦想的世界 所以甘心蛰伏于长夜",
			"你曾说你很像 一条发光的鱼"
	};

	private static CommandManager commandmanager;
	private static AltManager altmanager;
	private static FriendManager friendmanager;
	public static FontManager FontLoaders;
	public boolean LoadedWorld = false;
	public static TrayIcon trayIcon;
	public static RotationUtils RotationUtil;

	public static boolean isIntroFinish = false;

	public void initiate() {
		words = word[new Random().nextInt(word.length)];
		title = name + " " + distanceVersion +" | "+words;
		FontLoader.init();
		FontLoaders = new FontManager();
		Helper.Verify();
		commandmanager = new CommandManager();
		commandmanager.init();
		friendmanager = new FriendManager();
		friendmanager.init();
		this.modulemanager = new ModuleManager();
		this.modulemanager.init();
		Helper.mc.drawSplashScreen(55);
		TabUI tabui = new TabUI();
		tabui.init();
		ETBTabUI ETBtabui = new ETBTabUI();
		ETBtabui.init();
		altmanager = new AltManager();
		AltManager.init();
		AltManager.setupAlts();
		FileManager.init();
		Helper.mc.drawSplashScreen(60);
		Display.setTitle(title);
		isLoaded = true;
		RotationUtil = new RotationUtils();
		ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/assets/minecraft/Distance/ICON/LaunchIcon.png"));
		trayIcon = new TrayIcon(imageIcon.getImage());
		if (SystemTray.isSupported()) {
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip(String.format(name + " " + distanceVersion + " made by %s with \u2764~", author));
			PopupMenu popupMenu = new PopupMenu("Client settings");
			Menu moduleMenu = new Menu("Modules");
			for (Module module : ModuleManager.getModules()) {
				final Module m = module;
				final CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(m.getName(), m.isEnabled());
				checkboxMenuItem.addItemListener(e -> m.setEnabled(!m.isEnabled()));
				//m.setCheckboxMenuItem(checkboxMenuItem);
				moduleMenu.add(checkboxMenuItem);
			}
			popupMenu.add(moduleMenu);
			popupMenu.addSeparator();
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(e -> {
				trayIcon.displayMessage(Client.name + " - Notification", "下次再见！", TrayIcon.MessageType.INFO);
				SystemTray.getSystemTray().remove(trayIcon);
				Helper.mc.displayGuiScreen(new GuiGoodBye());
			});

			popupMenu.add(exitItem);
			trayIcon.setPopupMenu(popupMenu);

			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException ignored) {
			}

			trayIcon.displayMessage(Client.name + distanceVersion, "欢迎回来!", TrayIcon.MessageType.INFO);
		}
	}

	public TrayIcon getTrayIcon(){
		return trayIcon;
	}
	public static String getModuleName(Module mod) {
		String ModuleName = mod.getName();
		String CustomName = mod.getCustomName();
		if (CustomName != null) return CustomName;
		return ModuleName;
	}

	public static void RenderRotate(float yaw,float pitch) {
		Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
		Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
		Pitch = pitch;
	}

	public static int getClientColor() {
		java.awt.Color color = new java.awt.Color(HUD.r.getValue().intValue(), HUD.g.getValue().intValue(), HUD.b.getValue().intValue());
		return color.getRGB();
	}

	public static int getClientColor(int Alpha) {
		java.awt.Color color = new java.awt.Color(HUD.r.getValue().intValue(), HUD.g.getValue().intValue(), HUD.b.getValue().intValue(),Alpha);
		return color.getRGB();
	}

	public static java.awt.Color getClientColor(boolean RGBcolor) {
		java.awt.Color color = new java.awt.Color(HUD.r.getValue().intValue(), HUD.g.getValue().intValue(), HUD.b.getValue().intValue());
		return color;
	}

	public static int getBlueColor() {
		return new java.awt.Color(47, 154, 241).getRGB();
	}
	public static java.awt.Color getBlueColor(int alpha) {
		return new java.awt.Color(47, 154, 241,alpha);
	}

	public static int getBBlueColor() {
		return new java.awt.Color(0, 125, 255).getRGB();
	}


	public ModuleManager getModuleManager() {
		return this.modulemanager;
	}

	public FriendManager getFriendManager() {
		return friendmanager;
	}

	public CommandManager getCommandManager() {
		return commandmanager;
	}

	public AltManager getAltManager() {
		return altmanager;
	}

	public void shutDown() {
		save();
	}
	public static void save() {
		if (!ModuleManager.enabledNeededMod) {
			StringBuilder values = new StringBuilder();
			for (Module m : ModuleManager.getModules()) {
				for (Value v : m.getValues()) {
					values.append(String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator()));
				}
			}
			FileManager.save("Values.txt", values.toString(), false);
			StringBuilder name = new StringBuilder();
			for (Module m : ModuleManager.getModules()) {
				if (m.getCustomName() != null)
					name.append(String.format("%s:%s%s", m.getName(), m.getCustomName(), System.lineSeparator()));
			}
			FileManager.save("CustomName.txt", name.toString(), false);

			StringBuilder enabled = new StringBuilder();
			for (Module m : ModuleManager.getModules()) {
				if (m.isEnabled())
					enabled.append(String.format("%s%s", m.getName(), System.lineSeparator()));
			}
			FileManager.save("Enabled.txt", enabled.toString(), false);
			StringBuilder Hiddens = new StringBuilder();
			for (Module m : ModuleManager.getModules()) {
				if (m.wasRemoved()) Hiddens.append(m.getName()).append(System.lineSeparator());

			}
			FileManager.save("Hidden.txt", Hiddens.toString(), false);
			FileManager.save("NeedBlur.txt", String.valueOf(ClientSetting.enableBlur.getValue()), false);
		}
	}

	public static void doban(){
		Client.Baned = true;
		if (Helper.mc.theWorld != null){
			Helper.mc.theWorld.sendQuittingDisconnectingPacket();
			Helper.mc.loadWorld(null);
		}
		Helper.mc.displayGuiScreen(new GuiBaned());
	}
}
