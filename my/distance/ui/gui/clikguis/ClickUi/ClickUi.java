package my.distance.ui.gui.clikguis.ClickUi;

import com.google.common.collect.Lists;
import my.distance.Client;
import my.distance.api.verify.SHWID;
import my.distance.manager.Fucker;
import my.distance.manager.RenderManager;
import my.distance.module.ModuleType;
import my.distance.util.anim.AnimationUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ClickUi extends GuiScreen {
	public static ArrayList<Window> windows = Lists.newArrayList();
	public double opacity = 0.0;
	public int scrollVelocity;
	public static boolean binding = false;

	public ClickUi() {
		if (windows.isEmpty()) {
			int x = 5;
			ModuleType[] arrmoduleType = ModuleType.values();
			int n = arrmoduleType.length;
			int n2 = 0;
			while (n2 < n) {
				ModuleType c = arrmoduleType[n2];
				windows.add(new Window(c, x, 5));
				x += 105;
				++n2;
			}
		}
	}
	private float animpos = 75f;
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
		this.drawDefaultBackground();
		GlStateManager.rotate(animpos, 0, 0, 0);
		GlStateManager.translate(0, animpos, 0);
		this.opacity = this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0;
		GlStateManager.pushMatrix();
		ScaledResolution scaledRes = new ScaledResolution(this.mc);
		float scale = (float) scaledRes.getScaleFactor() / (float) Math.pow(scaledRes.getScaleFactor(), 2.0);
		windows.forEach(w -> w.render(mouseX, mouseY));
		GlStateManager.popMatrix();
		if (Mouse.hasWheel()) {
			int wheel = Mouse.getDWheel();
			this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 120 : 0);
		}
		windows.forEach(w -> w.mouseScroll(mouseX, mouseY, this.scrollVelocity));
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		windows.forEach(w -> w.click(mouseX, mouseY, mouseButton));
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 && !binding) {
			this.mc.displayGuiScreen(null);
			return;
		}
		windows.forEach(w -> w.key(typedChar, keyCode));
	}

	public void initGui() {
		super.initGui();
	}

	public void onGuiClosed() {
		Client.instance.shutDown();
		this.mc.entityRenderer.stopUseShader();
		this.mc.entityRenderer.isShaderActive();
	}

	public synchronized void sendToFront(Window window) {
		int panelIndex = 0;
		int i = 0;
		while (i < windows.size()) {
			if (windows.get(i) == window) {
				panelIndex = i;
				break;
			}
			++i;
		}
		Window t = windows.get(windows.size() - 1);
		windows.set(windows.size() - 1, windows.get(panelIndex));
		windows.set(panelIndex, t);
	}
}
