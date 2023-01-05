package my.distance.ui.gui.clikguis.clickgui4;

import java.awt.Color;

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
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.manager.RenderManager;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimeHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class ClickGui extends GuiScreen implements GuiYesNoCallback {
	public static ModuleType currentModuleType = ModuleType.Combat;
	public static Module currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
			? ModuleManager.getModulesInType(currentModuleType).get(0)
			: null;
	public static float startX = 200, startY = 85;
	public int moduleStart = 0;
	public int valueStart = 0;
	boolean previousmouse = true;
	boolean mouse;
	public Opacity opacity = new Opacity(0);
	public int opacityx = 255;
	public float moveX = 0, moveY = 0;
	public CFontRenderer LogoFont= FontLoaders.calibrilite35;
	private float animpos = 75f;
	private final int ClientColors = my.distance.module.modules.render.ClickGui.CustomColor.getValue()? Client.getClientColor() : Client.getBBlueColor();
	TimeHelper AnimationTimer = new TimeHelper();

	int finheight;
	int animheight = 0;
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (SHWID.BITCH != 0){
			RenderManager.doRender();
		}
		if (SHWID.hahaha != 1){
			Fucker.dofuck();
		}
		if (SHWID.id != 1){
			RenderManager.doRender();
			Fucker.dofuck();
		}
		if (SHWID.id2 != 2){
			RenderManager.doRender();
			Fucker.dofuck();
		}
		animpos = AnimationUtil.moveUD(animpos, 1f, 0.1f, 0.1f);
		GlStateManager.rotate(animpos, 0, 0, 0);
		GlStateManager.translate(0, animpos, 0);
		if (isHovered(startX-40, startY, startX + 280, startY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
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
		this.opacity.interpolate((float) opacityx);
			RenderUtil.drawRect(startX - 40, startY, startX + 60, startY + 235,
					ClientColors);
			RenderUtil.drawRect(startX + 60, startY, startX + 170, startY + 235,
					new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
			RenderUtil.drawRect(startX + 170, startY, startX + 280, startY + 235,
					new Color(246, 246, 246, (int) opacity.getOpacity()).getRGB());
			//RenderUtil.drawGradientSideways(startX +60 , startY, startX + 70, startY + 235,
			//		new Color(0, 0, 0, 70).getRGB(),new Color(255, 255, 255, 30).getRGB());

			//RenderUtil.drawGradientSideways(startX +170 , startY, startX + 175, startY + 235,
			//		new Color(0, 0, 0, 50).getRGB(),new Color(255, 255, 255, 30).getRGB());
		LogoFont.drawCenteredString(Client.instance.name,startX+10,startY+10,new Color(255,255,255, (int) opacity.getOpacity()).getRGB());
		FontLoaders.GoogleSans18.drawString(Client.instance.version,startX+35,startY+25,new Color(200,200,200, (int) opacity.getOpacity()).getRGB());
		for (int i = 0; i < ModuleType.values().length; i++) {
			ModuleType[] iterator = ModuleType.values();
			if (iterator[i] == currentModuleType) {
				finheight = i*30;
				//RenderUtil.drawGradientSideways(startX-40,startY+50+animheight,startX+60,startY+75+animheight,new Color(0,80,255, (int) opacity.getOpacity()).getRGB(),new Color(0,150,255, (int) opacity.getOpacity()).getRGB());
					RenderUtil.drawRect(startX - 40, startY + 50 + animheight, startX + 60, startY + 75 + animheight, new Color(66,134,245).getRGB());
				if(animheight<finheight){
					if(finheight - animheight<30) {
						animheight+=2;
					}else{
						animheight+=4;
					}
				}else if(animheight>finheight){
					if(animheight - finheight<30) {
						animheight-=2;
					}else{
						animheight-=4;
					}
				}
				if(animheight==finheight){
					FontLoaders.GoogleSans20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(255,255,255, (int) opacity.getOpacity()).getRGB());
				}else{
					RenderUtil.drawRect(startX-20,startY+50+i*30,startX+60,startY+75+i*30,new Color(255,255,255,0).getRGB());
					FontLoaders.GoogleSans20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(196,196,196, (int) opacity.getOpacity()).getRGB());
				}
			}else{
				RenderUtil.drawRect(startX-20,startY+50+i*30,startX+60,startY+75+i*30,new Color(255,255,255,0).getRGB());
				FontLoaders.GoogleSans20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(196,196,196, (int) opacity.getOpacity()).getRGB());
			}
			try {
				if (this.isCategoryHovered(startX - 40, startY + 50 + i * 30, startX + 60, startY + 75 + i * 40, mouseX,
						mouseY) && Mouse.isButtonDown((int) 0)) {
					currentModuleType = iterator[i];
					currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
							? ModuleManager.getModulesInType(currentModuleType).get(0)
							: null;
					moduleStart = 0;
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		int m = Mouse.getDWheel();
		if (this.isCategoryHovered(startX + 60, startY, startX + 200, startY + 235, mouseX, mouseY)) {
			if (m < 0 && moduleStart < ModuleManager.getModulesInType(currentModuleType).size() - 1) {
				moduleStart++;
			}
			if (m > 0 && moduleStart > 0) {
				moduleStart--;
			}
		}
		if (this.isCategoryHovered(startX + 200, startY, startX + 420, startY + 235, mouseX, mouseY)) {
			if (m < 0 && valueStart < currentModule.getValues().size() - 1) {
				valueStart++;
			}
			if (m > 0 && valueStart > 0) {
				valueStart--;
			}
		}
			FontLoaders.GoogleSans16.drawString(
					currentModule == null ? currentModuleType.toString()
							: currentModuleType.toString() + "/" + currentModule.getName(),
					startX + 70, startY + 15, new Color(0, 0, 0).getRGB());
		if (currentModule != null) {
			float mY = startY + 30;
			for (int i = 0; i < ModuleManager.getModulesInType(currentModuleType).size(); i++) {
				Module module = ModuleManager.getModulesInType(currentModuleType).get(i);
				if (module.wasRemoved()){
					continue;
				}
				if (mY > startY + 220)
					break;
				if (i < moduleStart) {
					continue;
				}

				RenderUtil.drawRect(startX + 75, mY, startX + 185, mY + 2,
						new Color(246, 246, 246, 0).getRGB());
				if (isSettingsButtonHovered(startX + 75, mY,
						startX + 100 + (FontLoaders.GoogleSans18.getStringWidth(module.getName())),
						mY + 8 + FontLoaders.GoogleSans18.getStringHeight(""), mouseX, mouseY)) {
					if(!module.isEnabled()) {
							FontLoaders.GoogleSans18.drawString(module.getName(), startX + 90, mY + 8,
									new Color(20, 100, 200, (int) opacity.getOpacity()).getRGB(), false);
					}else{
						FontLoaders.GoogleSans18.drawString(module.getName(), startX + 90, mY + 8,
								new Color(66,134,245, (int) opacity.getOpacity()).getRGB(), false);
					}
				}else{
					if(module.isEnabled()){
							FontLoaders.GoogleSans18.drawString(module.getName(), startX + 90, mY + 8,
									Client.getBlueColor(), false);
					}else {
						FontLoaders.GoogleSans18.drawString(module.getName(), startX + 90, mY + 8,
								new Color(107, 107, 107, (int) opacity.getOpacity()).getRGB(), false);
					}
				}

				if (!module.isEnabled()) {
					RenderUtil.drawFilledCircle(startX + 75, mY + 10, 3,
							new Color(174, 174, 174, (int) opacity.getOpacity()).getRGB(), 5);
				} else {
						RenderUtil.drawFilledCircle(startX + 75, mY + 10, 3,
								Client.getBlueColor(), 5);
				}
				if (isSettingsButtonHovered(startX + 75, mY,
						startX + 100 + (FontLoaders.GoogleSans18.getStringWidth(module.getName())),
						mY + 8 + FontLoaders.GoogleSans18.getStringHeight(""), mouseX, mouseY)) {
					if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
						module.setEnabled(!module.isEnabled());
						previousmouse = true;
					}
					if (!this.previousmouse && Mouse.isButtonDown((int) 1)) {
						previousmouse = true;
					}
				}

				if (!Mouse.isButtonDown((int) 0)) {
					this.previousmouse = false;
				}
				if (isSettingsButtonHovered(startX + 90, mY,
						startX + 100 + (FontLoaders.GoogleSans20.getStringWidth(module.getName())),
						mY + 8 + FontLoaders.GoogleSans20.getStringHeight(""), mouseX, mouseY) && Mouse.isButtonDown((int) 1)) {
					currentModule = module;
					valueStart = 0;
				}
				mY += 20;
			}
			mY = startY + 30;
			if(currentModule.getValues().size()<1){
				RenderUtil.drawRect(0,0,0,0,-1);
				FontLoaders.GoogleSans20.drawString("NoSettingsHere",startX+185,startY+10,new Color(178,178,178).getRGB());

			}
			for (int i = 0; i < currentModule.getValues().size(); i++) {
				if (mY > startY + 220)
					break;
				if (i < valueStart) {
					continue;
				}
				CFontRenderer font = FontLoaders.GoogleSans16;
				Value value = currentModule.getValues().get(i);
				float x;
				if (value instanceof Option) {
					x = startX + 190.0f;
					Client.FontLoaders.GoogleSans16.drawString(value.getName(), startX + 185, mY + 3, new Color(136, 136, 136).getRGB());
					if ((Boolean) value.getValue()) {
						RenderUtil.drawRoundedRect(x + 65.0f, mY, x + 75.0f, mY + 10.0f, 4, new Color(66, 134, 245).getRGB());
						RenderUtil.drawCircle2(x + 70.0f, mY + 5.0f, 4.0, new Color(255, 255, 255).getRGB());
					} else {
						RenderUtil.drawRoundedRect(x + 65.0f, mY, x + 75.0f, mY + 10.0f, 4, new Color(114, 118, 125).getRGB());
						RenderUtil.drawCircle2(x + 60.0f, mY + 5.0f, 4.0, new Color(164, 168, 175).getRGB());
					}
					if (this.isCheckBoxHovered(x + 55.0f, mY, x + 76.0f, mY + 9.0f, mouseX, mouseY)) {
						if (!this.previousmouse && Mouse.isButtonDown((int)0)) {
							mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
							this.previousmouse = true;
							this.mouse = true;
						}
						if (this.mouse) {
							value.setValue(!((Boolean) value.getValue()));
							this.mouse = false;
						}
					}
					if (!Mouse.isButtonDown((int)0)) {
						this.previousmouse = false;
					}
					mY += 25.0f;
				}
				if (value instanceof Mode) {
					x = startX + 190.0f;
					Client.instance.FontLoaders.GoogleSans16.drawString(value.getName(), startX + 185.0f, mY - 1.0f + 2, new Color(136, 136, 136).getRGB());
					RenderUtil.drawRoundedRect(x - 10.0f, mY + 6.0f, x + 75.0f, mY + 22.0f, 3, new Color(86, 154, 255, (int)this.opacity.getOpacity()).getRGB());
					Client.instance.FontLoaders.GoogleSans18.drawString(((Mode)value).getModeAsString(), x + 30.0f - (float)(font.getStringWidth(((Mode)value).getModeAsString()) / 2), mY + 10.0f + 2, -1);
					if (this.isStringHovered(x - 10.0f, mY + 6.0f, x + 75.0f, mY + 22.0f, mouseX, mouseY)) {
						if (Mouse.isButtonDown((int)0) && !this.previousmouse) {
							mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
							Enum current = (Enum)((Mode)value).getValue();
							int next = current.ordinal() + 1 >= ((Mode)value).getModes().length ? 0 : current.ordinal() + 1;
							value.setValue(((Mode)value).getModes()[next]);
							this.previousmouse = true;
						}
						if (!Mouse.isButtonDown((int)0)) {
							this.previousmouse = false;
						}
					}
					mY += 30.0f;
				}
				if (value instanceof Numbers) {
						x = startX + 190;
						double render = (double) (68.0F
								* (((Number) value.getValue()).floatValue() - ((Numbers) value).getMinimum().floatValue())
								/ (((Numbers) value).getMaximum().floatValue()
								- ((Numbers) value).getMinimum().floatValue()));
						RenderUtil.drawRect((float) x - 11, mY + 7, (float) ((double) x + 70), mY + 8,
								(new Color(213, 213, 213, (int) opacity.getOpacity())).getRGB());
						RenderUtil.drawRect((float) x - 11, mY + 7, (float) ((double) x + render + 0.5D), mY + 8,
								(new Color(88, 182, 255, (int) opacity.getOpacity())).getRGB());
						RenderUtil.circle((float) (x + render + 2D), mY + 7, 2, new Color(0, 144, 255).getRGB());
						font.drawString(value.getName() + ": " + value.getValue(), startX + 185, mY - 3, new Color(136, 136, 136).getRGB());
						if (!Mouse.isButtonDown((int) 0)) {
							this.previousmouse = false;
						}
						if (this.isButtonHovered(x, mY - 4, x + 100, mY + 9, mouseX, mouseY)
								&& Mouse.isButtonDown((int) 0)) {
							if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
								render = ((Numbers) value).getMinimum().doubleValue();
								double max = ((Numbers) value).getMaximum().doubleValue();
								double inc = ((Numbers) value).getIncrement().doubleValue();
								double valAbs = (double) mouseX - ((double) x + 1.0D);
								double perc = valAbs / 68.0D;
								perc = Math.min(Math.max(0.0D, perc), 1.0D);
								double valRel = (max - render) * perc;
								double val = render + valRel;
								val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
								((Numbers) value).setValue(val);
							}
							if (!Mouse.isButtonDown((int) 0)) {
								this.previousmouse = false;
							}
						}
						mY += 25;
				}
			}
		}

	}

	public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*1f && mouseX <= g*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*1f && mouseX <= x2*1f && mouseY >= y*1f && mouseY <= y2*1f) {
			return true;
		}

		return false;
	}

	@Override
	public void onGuiClosed() {
		this.opacity.setOpacity(0);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
