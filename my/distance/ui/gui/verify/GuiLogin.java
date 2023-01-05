package my.distance.ui.gui.verify;

import LemonObfAnnotation.ObfuscationClass;
import my.distance.Client;
import my.distance.manager.FileManager;
import my.distance.module.modules.world.IRC;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.buttons.UIFlatButton;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.gui.GuiWelcome;
import my.distance.ui.jelloparticle.ParticleEngine;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.misc.ObfuscatedUtils;
import my.distance.util.render.Blur;
import my.distance.util.render.Colors;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@ObfuscationClass
public class GuiLogin extends GuiScreen {
	public static PasswordField password;
	public static TextField username;
	public GuiButton loginButton;
	public GuiButton freeButton;
	public static boolean logined = false;
	public static boolean Logging = false;
	public static boolean Passed = false;
	public ParticleEngine pe = new ParticleEngine();
	double anim, anim2, anim3 = 0;

	private static boolean animationFinished = false;

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 1:
				try {
					if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
						String usernames = "[" + Client.name + "]" + username.getText();
						Client.userName = username.getText();
						String passwords = password.getText();
						Logging = true;
						new Thread(() -> IRC.Verify(usernames, passwords)).start();
						logined = true;
					}
				} catch (Throwable var4) {
					var4.printStackTrace();
				}
				break;
			case 3:
				System.exit(0);
		}
	}

	double animIntro1 = 255;
	double animIntro2 = 0;
	double animIntro3 = 0;

	double animIntro4 = 0;
	double animIntro5 = 0;

	double animIntro6 = 0;
	double animIntro7 = 0;

	double animIntro8 = 255;

	TimerUtil timerUtil = new TimerUtil();
	TimerUtil timerUtil2 = new TimerUtil();

	@Override
	public void drawScreen(int var1, int var2, float var3) {
		GL11.glPushMatrix();
		int h = new ScaledResolution(this.mc).getScaledHeight();
		int w = new ScaledResolution(this.mc).getScaledWidth();
		if (!Client.isIntroFinish) {
			RenderUtil.prepareScissorBox((float) ((w / 2f - 30) - animIntro2), (float) ((h / 2f + 9) - animIntro4), (float) ((w / 2f - 30 + 60) + animIntro3), (float) ((h / 2f + 10) + animIntro5));
			RenderUtil.drawRect(-10, -10, width + 10, height + 10, new Color(0, 0, 0).getRGB());
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
		}
		if (logined) {
			anim = AnimationUtils.animate(w, anim, 6.0f / Minecraft.getDebugFPS());
			anim3 = AnimationUtils.animate(w, anim3, 5.5f / Minecraft.getDebugFPS());
			anim2 = AnimationUtils.animate(w, anim2, 4.0f / Minecraft.getDebugFPS());
		}
		BackGroundRenderer.render();
		pe.render(0, 0);
		RenderUtil.drawGradientSideways((w / 3f), 0, w / 3f + 10f, height, new Color(0, 0, 0, 60).getRGB(), new Color(0, 0, 0, 0).getRGB());
		RenderUtil.drawRect(-5f, 0f, w / 3f, h, new Color(255, 255, 255, 32).getRGB());
		Blur.blurAreaBoarderXY(-5f, 0f, w / 3f, h);
		username.drawTextBox();
		password.drawTextBox();
		FontLoaders.GoogleSans15.drawString("Username:", 25, height / 2f - 58, -1);
		FontLoaders.GoogleSans15.drawString("Password:", 25, height / 2f - 18, -1);
		FontLoaders.calibrilite35.drawString(Client.name, 23, (float) this.height / 2 - 97, -1);
		FontLoaders.calibrilite20.drawString("Login", 25 + FontLoaders.calibrilite35.getStringWidth(Client.name) + 3, (float) this.height / 2 - 108.5f + FontLoaders.calibrilite35.getStringHeight(Client.name), -1);
		FontLoaders.GoogleSans16.drawString(Client.name + String.format(" made by \u00a7oMymylesaws\u00a7r (%s)", Client.author), width -
						FontLoaders.GoogleSans16.getStringWidth(Client.name+ String.format(" made by \u00a7oMymylesaws\u00a7r (%s)",Client.author )) - 1,
				height - FontLoaders.GoogleSans16.getHeight() - 5f, -1);

		super.drawScreen(var1, var2, var3);


		RenderUtil.drawRect(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
		RenderUtil.drawRect(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
		RenderUtil.drawRect(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());

		if (anim2 >= w - 5) {
			mc.displayGuiScreen(new GuiWelcome());
		}
		if (!Client.isIntroFinish) {
			if (timerUtil2.hasReached(2700L)) {
				if (animIntro1 != 1) {
					animIntro1 = AnimationUtil.moveUD(animIntro1, 1, 10f / Minecraft.getDebugFPS(), 5f / Minecraft.getDebugFPS());
					timerUtil.reset();
				} else {
					if (timerUtil.hasReached(500)) {
						animIntro2 = AnimationUtil.moveUD(animIntro2, (w / 2f - 30) + 5, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
						animIntro3 = AnimationUtil.moveUD(animIntro3, (w + 5) - (w / 2f - 30 + 60), 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
					}
					if (timerUtil.hasReached(1200)) {
						animIntro4 = AnimationUtil.moveUD(animIntro4, (h / 2f + 9) + 5, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
						animIntro5 = AnimationUtil.moveUD(animIntro5, (h + 5) - (h / 2f + 10), 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
					}

					if (timerUtil.hasReached(1100)) {
						if (timerUtil.hasReached(2500)) {
							animIntro6 = AnimationUtil.moveUD(animIntro6, 0, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
							animIntro7 = AnimationUtil.moveUD(animIntro7, 0, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
							animIntro8 = AnimationUtil.moveUD(animIntro8, 1, 8f / Minecraft.getDebugFPS(), 4f / Minecraft.getDebugFPS());
							if (animIntro6 <= 2 || animIntro7 <= 2) Client.isIntroFinish = true;
						} else {
							animIntro6 = AnimationUtil.moveUD(animIntro6, width, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
							animIntro7 = AnimationUtil.moveUD(animIntro7, height, 6f / Minecraft.getDebugFPS(), 2f / Minecraft.getDebugFPS());
						}
						if ((animIntro6 != 0 || animIntro7 != 0) && animIntro8 >= 1) {
							double xmod = this.width / 2f - (animIntro6 / 2);
							double ymod = this.height / 2f - (animIntro7 / 2);
							GlStateManager.translate(xmod, ymod, 0);
							GlStateManager.scale(animIntro6 / this.width, animIntro7 / this.height, 1);
							FontLoaders.calibrilite50.drawCenteredString(Client.name, (((w / 2f) - 10) - 10) + 0.7f, (((h / 2f) + 1) - (FontLoaders.calibrilite50.getHeight() / 2f)) + 0.7f, new Color(0,0,0, Math.max(Math.min((int) animIntro8,100),1)).getRGB());
							FontLoaders.calibrilite35.drawCenteredString(Client.distanceVersion, ((w / 2f + FontLoaders.calibrilite35.getStringWidth(Client.name)) - 10) + 0.7f, (((h / 2f) + 1) - (FontLoaders.calibrilite50.getHeight() / 2f)) + 0.7f, new Color(0, 0, 0, Math.max(Math.min((int) animIntro8,100),1)).getRGB());

							FontLoaders.calibrilite50.drawCenteredString(Client.name, ((w / 2f) - 10) - 10, ((h / 2f) + 1) - (FontLoaders.calibrilite50.getHeight() / 2f), new Color(255, 255, 255, (int) animIntro8).getRGB());
							FontLoaders.calibrilite35.drawCenteredString(Client.distanceVersion, (w / 2f + FontLoaders.calibrilite35.getStringWidth(Client.name)) - 10, ((h / 2f) + 1) - (FontLoaders.calibrilite50.getHeight() / 2f), new Color(255, 255, 255, (int) animIntro8).getRGB());
							GlStateManager.scale(1, 1, 1);
							GlStateManager.translate(0, 0, 0);
						}
					}
				}
			}
			if (Client.isIntroFinish) {
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
			}

			if (animIntro1 != 1) {
				GlStateManager.translate(0, 0, 0);
				RenderUtil.drawRect(w / 2f - 30, h / 2f + 9, w / 2f - 30 + 60, h / 2f + 10, new Color(255, 255, 255, (int) animIntro1).getRGB());
			}
		}
		GL11.glPopMatrix();

	}

	@Override
	public void initGui() {
		logined = false;
		FontRenderer var1 = this.mc.fontRendererObj;
		int var2 = this.height / 2;
		super.initGui();
		this.loginButton = new UIFlatButton(1, 25, var2 + 50, (((width / 3) - 50) / 2) - 2, 20, "登录", Client.getBlueColor());
		this.freeButton = new UIFlatButton(3, 29 + (((width / 3) - 50) / 2) - 2, var2 + 50, (((width / 3) - 50) / 2) - 2, 20, "退出", Colors.GREY.c);
		this.buttonList.add(this.loginButton);
		this.buttonList.add(this.freeButton);
		username = new TextField(var2, var1, 25, this.height / 2 - 50, (width / 3) - 50, 20);
		password = new PasswordField(var1, 25, this.height / 2 - 10, (width / 3) - 50, 20);
		username.setFocused(true);
		Keyboard.enableRepeatEvents(true);

		List<String> userdata = FileManager.readUser("userdata.txt");
		if (!userdata.isEmpty()){
			String data = ObfuscatedUtils.decodeWithUserPass(userdata.get(0));
			String[] userpass = data.split(":");

			username.setText(userpass[0]);
			password.setText(userpass[1]);
		}
		timerUtil2.reset();
		timerUtil.reset();
	}

	@Override
	protected void keyTyped(char var1, int var2) {
		if (isKeyComboCtrlX(var2)){
			Client.isIntroFinish = false;
			mc.displayGuiScreen(new GuiLogin());
		}
		if (var1 == 9) {
			if (!username.isFocused()) {
				username.setFocused(true);
			} else {
				username.setFocused(username.isFocused());
				password.setFocused(!username.isFocused());
			}
		}

		if (var1 == 13) {
			this.actionPerformed(this.buttonList.get(0));
		}

		username.textboxKeyTyped(var1, var2);
		password.textboxKeyTyped(var1, var2);
	}

	@Override
	protected void mouseClicked(int var1, int var2, int var3) {
		try {
			super.mouseClicked(var1, var2, var3);
		} catch (IOException var5) {
			var5.printStackTrace();
		}

		username.mouseClicked(var1, var2, var3);
		password.mouseClicked(var1, var2, var3);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {
		username.updateCursorCounter();
		password.updateCursorCounter();
	}
}



