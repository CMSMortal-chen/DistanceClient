package my.distance.ui.gui.clikguis.clickgui3;

import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.api.verify.SHWID;
import my.distance.Client;
import my.distance.manager.Fucker;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.font.FontLoaders;
import my.distance.manager.RenderManager;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.anim.Palette;
import my.distance.util.render.ColorUtils;
import my.distance.util.render.RenderUtil;
import my.distance.util.render.gl.GLUtils;
import my.distance.util.SuperLib;
import my.distance.fastuni.FastUniFontRenderer;
import my.distance.fastuni.FontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Objects;

public class ClientClickGui extends GuiScreen {
	public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	public static ModuleType currentModuleType = ModuleType.Combat;
	public static Module currentModule = ModuleManager.getModulesInType(currentModuleType).get(0);
	public static float startX = sr.getScaledWidth() / 2f - 580f / 2f, startY = sr.getScaledHeight() / 2f - 350f / 2f;
	public static int moduleStart = 0;
	public static int valueStart = 0;
	boolean previousmouse = true;
	boolean exiting = false;
	boolean mouse;
	public float moveX = 0, moveY = 0;
	boolean bind = false;
	public static float alpha = 0;
	double AnimTypeY = 0;

	private float animpos = 190;
	boolean overCharge = false;
	boolean isDone = false;
	float yAnim = 0;
	float yAnimValue = 0;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (exiting && animpos >= 130) {
			Client.save();
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
			try {
				Mouse.setNativeCursor(null);
			} catch (Throwable ignore) {
			}
		}
		if (!isDone) {
			alpha = SuperLib.getAnimationState(alpha, exiting ? 0f : 255f, 120000f / Minecraft.getDebugFPS());
			if (overCharge) {
				animpos = AnimationUtil.moveUD(animpos, exiting ? 180f : 100f, 10f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
			} else {
				animpos = AnimationUtil.moveUD(animpos, exiting ? 180f : 80f, 10f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
			}
			if (animpos <= 94f) {
				overCharge = true;
			}
		}
		if (animpos == 80) {
			isDone = true;
		}
		GlStateManager.translate(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, 0);
		GlStateManager.scale(animpos / 100f, animpos / 100f, 0f);
		GlStateManager.translate(-sr.getScaledWidth() / 2f, -sr.getScaledHeight() / 2f, 0);
		//GlStateManager.color(1f,1f,1f,animpos / 100f);
		sr = new ScaledResolution(mc);
		if (!exiting) {
			hideCursor();
		}
		int color4 = Client.getClientColor((int) alpha);

		RenderUtil.drawRect(startX, startY, startX + (float) 550, startY + (float) 350, new Color(24, 24, 24, Math.max(0, (int) alpha - 18)).getRGB());

		RenderUtil.drawRect(startX + 120, startY + 48, startX + 510, startY + 318, new Color(10, 10, 10, Math.max(0, (int) alpha - 210)).getRGB());
		RenderUtil.drawRect(startX + 270, startY + 48, startX + 510, startY + 318, new Color(10, 10, 10, Math.max(0, (int) alpha - 210)).getRGB());

		FontLoaders.calibrilite32.drawCenteredString(Client.name, startX + 47, startY + 21, new Color(255, 255, 255, (int) alpha).getRGB());
		FontLoaders.calibrilite18.drawCenteredString((Main.isbeta ? "Beta " : "V") + Client.version, startX + 47 + FontLoaders.calibrilite32.getStringWidth(Client.name) - 14 + (Main.isbeta ? 6 : 0), startY + 25, color4);
		Client.FontLoaders.Chinese15.drawString(Client.words, (int) startX + 15, (int) startY + 332, new Color(180, 180, 180, (int) alpha).getRGB());

		int j = 60;
		int l = 45;
		float k = startY + 25;
		float xx = startX + 10;
		float typey;
		for (int i = 0; i < ModuleType.values().length; i++) {
			ModuleType[] iterator = ModuleType.values();
			if (iterator[i] == currentModuleType) {
				typey = k + 12 + j + i * l;
				AnimTypeY = AnimationUtils.animate(typey, AnimTypeY, (14.4f / (float) Minecraft.getDebugFPS()));
				RenderUtil.drawRect(xx, AnimTypeY - 22, xx + 1, AnimTypeY - 1, Client.getClientColor((int) alpha));
			}
			boolean movement = Objects.equals(iterator[i].toString(), "Movement");
			float y = k - 10 + j + i * l;
			float y2 = k + 20 + j + i * l;
			if (!movement) {
				FontLoaders.GoogleSans24.drawString(iterator[i].toString(), xx + (iterator[i] == currentModuleType ? 27 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 27 : 25), k + 56 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			}
			if (Objects.equals(iterator[i].toString(), "Combat")) {
				FontLoaders.NovICON24.drawString("H", xx + (iterator[i] == currentModuleType ? 10 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 10 : 8), k + 57 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			} else if (Objects.equals(iterator[i].toString(), "Render")) {
				FontLoaders.NovICON24.drawString("F", xx + (iterator[i] == currentModuleType ? 10 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 10 : 8), k + 57 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			} else if (movement) {
				FontLoaders.GoogleSans24.drawString("Move", xx + (iterator[i] == currentModuleType ? 27 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 27 : 25), k + 56 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
				FontLoaders.NovICON24.drawString("I", xx + (iterator[i] == currentModuleType ? 10 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 10 : 8), k + 57 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			} else if (Objects.equals(iterator[i].toString(), "Player")) {
				FontLoaders.NovICON24.drawString("C", xx + (iterator[i] == currentModuleType ? 10 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 10 : 8), k + 57 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			} else if (Objects.equals(iterator[i].toString(), "World")) {
				FontLoaders.NovICON24.drawString("E", xx + (iterator[i] == currentModuleType ? 10 : this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY) ? 10 : 8), k + 57 + l * i,
						new Color(255, 255, 255, (int) alpha).getRGB());
			}
			if (this.isCategoryHovered(xx + 8, y, xx + 80, y2, mouseX, mouseY)
					&& Mouse.isButtonDown(0)) {
				currentModuleType = iterator[i];
				currentModule = ModuleManager.getModulesInType(currentModuleType).get(0);
				moduleStart = 0;
				valueStart = 0;
				for (int x1 = 0; x1 < currentModule.getValues().size(); x1++) {
					Value value = currentModule.getValues().get(x1);
				}
			}
		}

		int m = Mouse.getDWheel();
		if (this.isCategoryHovered(startX + 120, startY + 40, startX + 270, startY + 315, mouseX, mouseY)) {
			if (m < 0 && moduleStart < ModuleManager.getModulesInType(currentModuleType).size() - 9) {
				moduleStart++;
			}
			if (m > 0 && moduleStart > 0) {
				moduleStart--;
			}
		}
		if (this.isCategoryHovered(startX + 270, startY + 50, startX + 510, startY + 315, mouseX, mouseY)) {
			if (m < 0 && valueStart < currentModule.getValues().size() - 7) {
				valueStart++;
			}
			if (m > 0 && valueStart > 0) {
				valueStart--;
			}
		}
		float mY = startY + 12;
		for (int i = 0; i < ModuleManager.getModulesInType(currentModuleType).size(); i++) {
			Module module = ModuleManager.getModulesInType(currentModuleType).get(i);
			if (module.wasRemoved()) {
				continue;
			}
			if (moduleStart - (25 * moduleStart) + mY > startY + 250) {
				module.clickAnim = 0;
			}
			if (i < moduleStart) {
				module.clickAnim = 0;
			}
			yAnim = AnimationUtil.moveUD(yAnim, (moduleStart - (25 * moduleStart)) * 100, 1f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
			module.clickAnim = AnimationUtil.moveUD(module.clickAnim, module.isEnabled() ? 4 : 0, 10f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
			if (isSettingsButtonHovered(startX + 120, yAnim / 100f + mY + 45, startX + 270, yAnim / 100f + mY + 45 + 25, mouseX, mouseY)) {
				module.hoverOpacity = AnimationUtil.moveUD(module.hoverOpacity, 20, 14f / Minecraft.getDebugFPS(), 6f / Minecraft.getDebugFPS());
			} else {
				module.hoverOpacity = AnimationUtil.moveUD(module.hoverOpacity, 0, 8f / Minecraft.getDebugFPS(), 3f / Minecraft.getDebugFPS());
			}
			RenderUtil.prepareScissorBox(0, startY + 57, width, startY + 310);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			if (isSettingsButtonHovered(0, startY + 57, width, startY + 310, mouseX, mouseY)) {
				RenderUtil.drawRect(startX + 120, yAnim / 100f + mY + 45, startX + 270, yAnim / 100f + mY + 45 + 25, new Color(1f, 1f, 1f, (float) module.hoverOpacity / 100).getRGB());
			}
			if (yAnim / 100f + mY + 45 >= startY + 40 && yAnim / 100f + mY + 45 + 25 <= startY + 330) {
				GLUtils.startSmooth();
				Gui.drawFilledCircle(startX + 140, yAnim / 100f + mY + 58, 4f - module.clickAnim, new Color(80, 80, 80, (int) alpha).getRGB(), 5);
				Gui.drawFilledCircle(startX + 140, yAnim / 100f + mY + 58, module.clickAnim, Client.getBlueColor((int) alpha).getRGB(), 5);
				GLUtils.endSmooth();
				FontLoaders.GoogleSans18.drawString(module.getName(), startX + 150 + (module.hoverOpacity / 2f), yAnim / 100f + mY + 55,
						new Color(255, 255, 255, (int) alpha).getRGB());
			}
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			if (isSettingsButtonHovered(0, startY + 57, width, startY + 310, mouseX, mouseY)) {
				if (isSettingsButtonHovered(startX + 120, yAnim / 100f + mY + 45, startX + 270, yAnim / 100f + mY + 70, mouseX, mouseY)) {
					if (!this.previousmouse && Mouse.isButtonDown(0)) {
						module.setEnabled(!module.isEnabled());
						previousmouse = true;
					}
					if (!this.previousmouse && Mouse.isButtonDown(1)) {
						previousmouse = true;
					}
				}

				if (!Mouse.isButtonDown(0)) {
					this.previousmouse = false;
				}
				if (isSettingsButtonHovered(startX + 120, yAnim / 100f + mY + 45, startX + 270, yAnim / 100f + mY + 45 + 25, mouseX, mouseY)
						&& Mouse.isButtonDown(1)) {
					for (int c = 0; c < currentModule.getValues().size(); c++) {
						Value value = currentModule.getValues().get(c);
					}
					currentModule = module;
					valueStart = 0;
				}
			}
			mY += 25;
		}
		mY = startY + 12;
		FastUniFontRenderer font = Client.FontLoaders.GoogleSans16;
		RenderUtil.prepareScissorBox(0, startY + 57, width, startY + 270);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		if (currentModule.getValues().isEmpty()) {
			float x = startX + 410;
			FontLoader.msFont16.drawCenteredStringWithShadow("这里没有可以给你调的数值捏 (\u00B4 \u2022\u03C9\u2022)", x - 15, startY + 12 + 50 + 3,
					new Color(150, 150, 150, (int) alpha).getRGB());
		} else {
			for (int i = 0; i < currentModule.getValues().size(); i++) {
				yAnimValue = AnimationUtil.moveUD(yAnimValue, (valueStart - (25 * valueStart)) * 100, 1f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
				Value value = currentModule.getValues().get(i);
				if ((yAnimValue) / 100 + mY > startY + 220) {
					if (value instanceof Option) {
						((Option) value).anim = 0;
					}
				}
				if (i < valueStart) {
					if (value instanceof Option) {
						((Option) value).anim = 0;
					}
				}

				if (yAnimValue / 100f + mY + 45 >= startY + 40 && yAnimValue / 100f + mY + 45 + 25 <= startY + 290) {
					if (value instanceof Numbers) {
						float x = startX + 410;
						double render = 68.0F
								* (((Number) value.getValue()).floatValue() - ((Numbers) value).getMinimum().floatValue())
								/ (((Numbers) value).getMaximum().floatValue()
								- ((Numbers) value).getMinimum().floatValue());
						RenderUtil.drawFastRoundedRect(x, (yAnimValue / 100) + mY + 54, (int) ((double) x + 75), (yAnimValue / 100) + mY + 57,
								1, isButtonHovered(x, (yAnimValue / 100) + mY + 51, x + 100, (yAnimValue / 100) + mY + 60, mouseX, mouseY) ? new Color(40, 40, 40, (int) alpha).getRGB() : (new Color(30, 30, 30, (int) alpha)).getRGB());
						RenderUtil.drawFastRoundedRect(x, (yAnimValue / 100) + mY + 54, (int) ((double) x + render + 6.5D), (yAnimValue / 100) + mY + 57, 1, Client.getBlueColor((int) alpha).getRGB());
						//Gui.drawFilledCircle((float) ((double) x + render + 2D) + 3, (yAnimValue) / 100+ mY + 53, 1.5, Client.getBlueColor(), 5);
						font.drawStringWithShadow(value.getName(), startX + 290, (yAnimValue / 100) + mY + 50 + 3,
								new Color(175, 175, 175, (int) alpha).getRGB());
						font.drawStringWithShadow(value.getValue().toString(), x - 2 - font.getStringWidth(value.getValue().toString()), (yAnimValue / 100) + mY + 50 + 3,
								new Color(255, 255, 255, (int) alpha).getRGB());
						if (!Mouse.isButtonDown(0)) {
							this.previousmouse = false;
						}
						if (this.isButtonHovered(x, (yAnimValue / 100) + mY + 51, x + 100, (yAnimValue / 100) + mY + 60, mouseX, mouseY)
								&& Mouse.isButtonDown((int) 0)) {
							if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
								render = ((Numbers) value).getMinimum().doubleValue();
								double max = ((Numbers) value).getMaximum().doubleValue();
								double inc = ((Numbers) value).getIncrement().doubleValue();
								double valAbs = (double) mouseX - ((double) x + 4d);
								double perc = valAbs / 68.0D;
								perc = Math.min(Math.max(0.0D, perc), 1.0D);
								double valRel = (max - render) * perc;
								double val = render + valRel;
								val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
								value.setValue(val);
							}
							if (!Mouse.isButtonDown(0)) {
								this.previousmouse = false;
							}
						}
					}
					if (value instanceof Option) {
						float x = startX + 412;
						int xxx = 65;
						int x2x = 65;
						((Option) value).anim = AnimationUtil.moveUD(((Option) value).anim, (boolean) value.getValue() ? 5f : 0f, 18f / Minecraft.getDebugFPS(), 7f / Minecraft.getDebugFPS());

						font.drawStringWithShadow(value.getName(), startX + 290, (yAnimValue / 100) + mY + 50 + 3,
								new Color(175, 175, 175, (int) alpha).getRGB());
						//Gui.drawRect(x + xx, (yAnimValue / 100) + mY + 50, x + x2x, (yAnimValue / 100) + mY + 59, isCheckBoxHovered(x + xx - 5, (yAnimValue / 100) + mY + 50, x + x2x + 6, (yAnimValue / 100) + mY + 59, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(20, 20, 20, alpha).getRGB());
						GLUtils.startSmooth();
						//Gui.drawFilledCircle(x + xx, (yAnimValue / 100) + mY + 54.5, 4.5, isCheckBoxHovered(x + xx - 5, (yAnimValue / 100) + mY + 50, x + x2x + 6, (yAnimValue / 100) + mY + 59, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(20, 20, 20, alpha).getRGB(), 10);
						//Gui.drawFilledCircle(x + x2x, (yAnimValue / 100) + mY + 54.5, 4.5, isCheckBoxHovered(x + xx - 5, (yAnimValue / 100) + mY + 50, x + x2x + 6, (yAnimValue / 100) + mY + 59, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(20, 20, 20, alpha).getRGB(), 10);
						Gui.drawFilledCircle(x + x2x, (yAnimValue / 100) + mY + 54.5, 5 - ((Option) value).anim, new Color(56, 56, 56, (int) alpha).getRGB(), 10);
						Gui.drawFilledCircle(x + x2x, (yAnimValue / 100) + mY + 54.5, ((Option) value).anim, Client.getBlueColor((int) alpha).getRGB(), 10);
						GLUtils.endSmooth();
						if (this.isCheckBoxHovered(x + xxx - 5, (yAnimValue / 100) + mY + 50, x + x2x + 6, (yAnimValue / 100) + mY + 59, mouseX, mouseY)) {
							if (!this.previousmouse && Mouse.isButtonDown(0)) {
								this.previousmouse = true;
								this.mouse = true;
							}

							if (this.mouse) {
								value.setValue(!(boolean) value.getValue());
								this.mouse = false;
							}
						}
						if (!Mouse.isButtonDown(0)) {
							this.previousmouse = false;
						}
					}
					if (value instanceof Mode) {
						float x = startX + 410;
						font.drawStringWithShadow(value.getName(), startX + 290, (yAnimValue / 100) + mY + 52 + 3,
								new Color(175, 175, 175, (int) alpha).getRGB());

						RenderUtil.drawRoundedRect(x, (yAnimValue / 100) + mY + 45, x + 75, (yAnimValue / 100) + mY + 65, 3, new Color(30, 30, 30, (int) alpha).getRGB());

						RenderUtil.drawRoundedRect(x, (yAnimValue / 100) + mY + 45, x + 13, (yAnimValue / 100) + mY + 65, 1, isStringHovered(x, (yAnimValue / 100) + mY + 45, x + 13, (yAnimValue / 100) + mY + 65, mouseX, mouseY) ? new Color(101, 175, 255, (int) alpha).getRGB() : new Color(0, 141, 255, (int) alpha).getRGB());
						font.drawCenteredString("<", x + 15f / 2f, (yAnimValue / 100) + mY + 53, new Color(255, 255, 255, (int) alpha).getRGB());

						RenderUtil.drawRoundedRect(x + 75 - 13, (yAnimValue / 100) + mY + 45, x + 75, (yAnimValue / 100) + mY + 65, 1, isStringHovered(x + 75 - 13, (yAnimValue / 100) + mY + 45, x + 75, (yAnimValue / 100) + mY + 65, mouseX, mouseY) ? new Color(101, 175, 255, (int) alpha).getRGB() : new Color(0, 141, 255, (int) alpha).getRGB());
						font.drawCenteredString(">", x + 75 - (11f / 2f), (yAnimValue / 100) + mY + 53, new Color(255, 255, 255, (int) alpha).getRGB());

						FontLoaders.GoogleSans14.drawCenteredStringWithShadow((((Enum) value.getValue()).ordinal() + 1) + "/" + (((Mode) value).getModes().length),
								(x + 38), (yAnimValue / 100) + mY + 53 + 9, new Color(255, 255, 255, Math.max(0, (int) alpha - (255 - 130))).getRGB());

						font.drawCenteredStringWithShadow(((Mode) value).getModeAsString(),
								(float) (x + 38), (yAnimValue / 100) + mY + 53, new Color(255, 255, 255).getRGB());

						if (this.isStringHovered(x, (yAnimValue / 100) + mY + 45, x + 13, (yAnimValue / 100) + mY + 65, mouseX, mouseY)) {
							if (Mouse.isButtonDown((int) 0) && !this.previousmouse) {
								Enum current = (Enum) value.getValue();
								int next = current.ordinal() - 1 <= -1 ? ((Mode) value).getModes().length - 1
										: current.ordinal() - 1;
								value.setValue(((Mode) value).getModes()[next]);
								this.previousmouse = true;
							}

						}
						if (this.isStringHovered(x + 75 - 13, (yAnimValue / 100) + mY + 45, x + 75, (yAnimValue / 100) + mY + 65, mouseX, mouseY)) {
							if (Mouse.isButtonDown((int) 0) && !this.previousmouse) {
								Enum current = (Enum) value.getValue();
								int next = current.ordinal() + 1 >= ((Mode) value).getModes().length ? 0
										: current.ordinal() + 1;
								value.setValue(((Mode) value).getModes()[next]);
								this.previousmouse = true;
							}

						}
					}
				}
				mY += 25;
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		float x = startX + 405;
		float yyy = startY + 240;
		RenderUtil.drawRoundedRect(x + 5, yyy + 45, x + 75, yyy + 65, 1, isHovered(x + 2, yyy + 45, x + 78, yyy + 65, mouseX, mouseY) ? new Color(80, 80, 80, (int) alpha).getRGB() : new Color(56, 56, 56, (int) alpha).getRGB());
		font.drawStringWithShadow(Keyboard.getKeyName((int) currentModule.getKey()),
				(float) (x + 40 - font.getStringWidth(Keyboard.getKeyName((int) currentModule.getKey())) / 2),
				yyy + 53 + 1, new Color(255, 255, 255, (int) alpha).getRGB());
		font.drawStringWithShadow("Bind", startX + 290, yyy + 52 + 1, new Color(170, 170, 170, (int) alpha).getRGB());
		//Gui.drawRect(x + 5, yyy + 45, x + 75, yyy + 65, isHovered(x + 2, yyy + 45, x + 78, yyy + 65, mouseX, mouseY) ? new Color (80,80,80, alpha).getRGB() :new Color(56, 56, 56, alpha).getRGB());

		if ((isHovered(startX, startY, startX + 550, startY + 50, mouseX, mouseY) || isHovered(startX, startY + 315, startX + 550, startY + 350, mouseX, mouseY) || isHovered(startX + 530, startY, startX + 550, startY + 350, mouseX, mouseY)) && Mouse.isButtonDown(0)) {
			if (moveX == 0 && moveY == 0) {
				moveX = mouseX - startX;
				moveY = mouseY - startY;
			} else {
				startX = mouseX - moveX;
				startY = mouseY - moveY;
			}
			this.previousmouse = true;
		} else if (moveX != 0 || moveY != 0) {
			moveX = 0;
			moveY = 0;
		}
		GL11.glPushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.scale(1,1,1);
		GlStateManager.translate(0,0,0);
		FontLoaders.ICON20.drawStringWithShadow("p", mouseX - 1, mouseY + 2, ColorUtils.transparency(Palette.fade(new Color(255, 255, 255)), alpha));
		GlStateManager.scale(1,1,1);
		GlStateManager.translate(0,0,0);
		GlStateManager.disableBlend();
		GL11.glPopMatrix();
	}

	public void initGui() {
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
		for (int i = 0; i < currentModule.getValues().size(); i++) {
			Value value = currentModule.getValues().get(i);
			if (value instanceof Option) {
				((Option) value).anim = 0;
			}
		}
		for (Module m : ModuleManager.getModules()) {
			m.clickAnim = 0;
		}

		super.initGui();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.bind) {
			currentModule.setKey(keyCode);
			if (keyCode == 1) {
				currentModule.setKey(0);
			}
			this.bind = false;
		} else if (keyCode == 1) {
			exiting = true;
		}
		if (isKeyComboCtrlX(keyCode)) {
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
			try {
				Mouse.setNativeCursor(null);
			} catch (Throwable ignore) {
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		float x = startX + 405;
		float mY = startY + 30;
		for (int i = 0; i < currentModule.getValues().size(); i++) {
			if (mY > startY + 350)
				break;
			if (i < valueStart) {
				continue;
			}
			Value value = currentModule.getValues().get(i);
			if (value instanceof Numbers) {
				mY += 20;
			}
			if (value instanceof Option) {

				mY += 20;
			}
			if (value instanceof Mode) {

				mY += 25;
			}
		}
		float x1 = startX + 320;
		float yyy = startY + 240;
		if (isHovered(x + 5, yyy + 45, x + 75, yyy + 65, mouseX, mouseY)) {
			this.bind = true;
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	org.lwjgl.input.Cursor emptyCursor;

	private void hideCursor() {
		if (emptyCursor == null) {
			if (Mouse.isCreated()) {
				int min = org.lwjgl.input.Cursor.getMinCursorSize();
				IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
				try {
					emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Could not create empty cursor before Mouse object is created");
			}
		}
		try {
			Mouse.setNativeCursor(Mouse.isInsideWindow() ? emptyCursor : null);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

	@Override
	public void onGuiClosed() {
		alpha = 0;
		try {
			Mouse.setNativeCursor(null);
		} catch (Throwable ignore) {
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
