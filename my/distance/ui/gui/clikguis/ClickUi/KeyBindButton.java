package my.distance.ui.gui.clikguis.ClickUi;

import java.awt.Color;

import my.distance.fastuni.FastUniFontRenderer;
import my.distance.Client;
import org.lwjgl.input.Keyboard;

import my.distance.module.Module;
import net.minecraft.client.gui.Gui;

public class KeyBindButton extends ValueButton {
	public Module cheat;
	public double opacity = 0.0;
	public boolean bind;

	public KeyBindButton(Module cheat, int x, int y) {
		super(null, x, y);
		this.custom = true;
		this.bind = false;
		this.cheat = cheat;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		Gui.drawRect(0.0, 0.0, 0.0, 0.0, 0);
		Gui.drawRect(this.x - 10, this.y - 4, this.x + 80, this.y + 11, new Color(220, 220, 220).getRGB());
		mfont.drawString("Bind", this.x - 5, this.y + 2, new Color(108, 108, 108).getRGB());
		mfont.drawString(String.valueOf(this.bind ? "" : "") + Keyboard.getKeyName((int) this.cheat.getKey()),
				this.x + 76 - mfont.getStringWidth(Keyboard.getKeyName((int) this.cheat.getKey())), this.y + 2,
				new Color(108, 108, 108).getRGB());
	}

	@Override
	public void key(char typedChar, int keyCode) {
		if (this.bind) {
			this.cheat.setKey(keyCode);
			if (keyCode == 1) {
				this.cheat.setKey(0);
			}
			ClickUi.binding = false;
			this.bind = false;
		}
		super.key(typedChar, keyCode);
	}
	FastUniFontRenderer font = Client.FontLoaders.Chinese18;
	@Override
	public void click(int mouseX, int mouseY, int button) {
		if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
				&& mouseY < this.y + font.FONT_HEIGHT + 5
				&& button == 0) {
			ClickUi.binding = this.bind = !this.bind;
		}
		super.click(mouseX, mouseY, button);
	}
}
