package my.distance.module.modules.render.tabguis;

import java.awt.Color;
import java.util.List;

import my.distance.api.Priority;
import my.distance.api.EventBus;
import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventKey;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Option;
import my.distance.api.value.Numbers;
import my.distance.api.value.Value;
import my.distance.api.value.Mode;
import my.distance.manager.Manager;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.render.HUD;
import my.distance.module.modules.render.TabGui;
import my.distance.ui.font.CFontRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.util.anim.AnimationUtil;
import my.distance.util.anim.AnimationUtils;
import my.distance.util.misc.Helper;
import my.distance.util.math.MathUtil;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class TabUI implements Manager {
	private Section section = Section.TYPES;
	private ModuleType selectedType = ModuleType.values()[0];
	private Module selectedModule = null;
	private Value selectedValue = null;
	private int currentType = 0;
	private int currentModule = 0;
	private int currentValue = 0;
	private int maxType;
	private int maxModule;
	private int maxValue;
	double AnimY = 0;
	double AnimmodY = 0;
	double AnimValY = 0;
	float translationFactor = (14.4f / (float) Minecraft.getDebugFPS());
	private static int[] QwQ;

	@Override
	public void init() {
		CFontRenderer font = FontLoaders.GoogleSans18;
		ModuleType[] arrmoduleType = ModuleType.values();
		int n = arrmoduleType.length;
		int n2 = 0;
		while (n2 < n) {
			ModuleType mt = arrmoduleType[n2];
			if (this.maxType <= font.getStringWidth(mt.name().toUpperCase())) {
				this.maxType = font.getStringWidth(mt.name().toUpperCase());
			}
			++n2;
		}
		for (Module m : ModuleManager.getModules()) {
			if (m.wasRemoved()){
				continue;
			}
			if (this.maxModule > font.getStringWidth(m.getName().toUpperCase()) + 4)
				continue;
			this.maxModule = font.getStringWidth(m.getName().toUpperCase()) + 4;
		}
		for (Module m : ModuleManager.getModules()) {
			if (m.wasRemoved()){
				continue;
			}
			if (m.getValues().isEmpty())
				continue;
			for (Value<? extends Object> val : m.getValues()) {
				if (this.maxValue > font.getStringWidth(val.getDisplayName().toUpperCase()) + 4)
					continue;
				this.maxValue = font.getStringWidth(val.getDisplayName().toUpperCase()) + 4;
			}
		}
		this.maxModule += 12;
		this.maxValue += 24;
		boolean highestWidth = false;
		this.maxType = this.maxType < this.maxModule ? this.maxModule : this.maxType;
		this.maxModule += this.maxType;
		this.maxValue += this.maxModule;
		animtab = 0d;
		EventBus.getInstance().register(this);
	}

	private void resetValuesLength() {
		this.maxValue = 0;
		for (Value<? extends Object> val : this.selectedModule.getValues()) {
			int off;
			int n = off = val instanceof Option ? 6
					: Helper.mc.fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString()))
							+ 6;
			if (this.maxValue > Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off)
				continue;
			this.maxValue = Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
		}
		this.maxValue += this.maxModule;
	}

	double animtab = 0d;

	@EventHandler(priority = Priority.LOW)
	public void renderTabGUI(EventRender2D e) {
		translationFactor = (14.4f / (float) Minecraft.getDebugFPS());
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			return;
		}
		if (!ModuleManager.getModuleByClass(TabGui.class).isEnabled()) {
			return;
		}
		if (TabGui.modes.getValue().equals(TabGui.tabguimode.Novoline)) {
			return;
		}
		CFontRenderer font = FontLoaders.GoogleSans16;
		CFontRenderer icon = FontLoaders.NovICON28;
		int categoryY = 22 + TabGui.y.getValue().intValue();
		int moduleY = categoryY + 20;
		int valueY = categoryY;
		int color1 = !HUD.logomode.getValue().equals(HUD.logomodeE.Dark_Distance) ? new Color(234, 234, 234).getRGB() : new Color(15, 15, 15).getRGB();
		int color2 = !HUD.logomode.getValue().equals(HUD.logomodeE.Dark_Distance) ? new Color(108, 108, 108).getRGB() : new Color(230, 230, 230).getRGB();
		RenderUtil.drawRoundedRect(2, categoryY + 13, 65, categoryY - 2 + 18 * ModuleType.values().length,
				3f, color1);
		//FontLoaders.googlesans20.drawString("Mixed", 13, 8, color2);
		ModuleType[] moduleArray = ModuleType.values();
		int mA = moduleArray.length;
		int mA2 = 0;
		while (mA2 < mA) {
			ModuleType mt = moduleArray[mA2];
			RenderUtil.drawRect(4, AnimY + 2 + 17, 5, AnimY + 4 + 22, new Color(47, 154, 241).getRGB());

			if (this.selectedType == mt) {
				moduleY = categoryY;
				AnimY = AnimationUtils.animate(categoryY, AnimY, translationFactor);
			}
			if (this.selectedType == mt) {
				font.drawString(mt.name(), 18, categoryY + 20, new Color(47, 154, 241).getRGB());
				if (mt.name().equals("Combat")) {
					FontLoaders.NovICON24.drawString("H", 8, categoryY + 18, new Color(47, 154, 241).getRGB());
				}
				if (mt.name().equals("Render")) {
					FontLoaders.NovICON18.drawString("F", 7, categoryY + 20, new Color(47, 154, 241).getRGB());
				}
				if (mt.name().equals("Movement")) {
					FontLoaders.NovICON18.drawString("I", 7, categoryY + 20, new Color(47, 154, 241).getRGB());
				}
				if (mt.name().equals("World")) {
					FontLoaders.NovICON20.drawString("E", 8, categoryY + 20, new Color(47, 154, 241).getRGB());
				}
				if (mt.name().equals("Player")) {
					FontLoaders.NovICON20.drawString("C", 8, categoryY + 20, new Color(47, 154, 241).getRGB());
				}
			} else {
				font.drawString(mt.name(), 18, categoryY + 20, color2);
				if (mt.name().equals("Combat")) {
					FontLoaders.NovICON24.drawString("H", 8, categoryY + 18, color2);
				}
				if (mt.name().equals("Render")) {
					FontLoaders.NovICON18.drawString("F", 7, categoryY + 20, color2);
				}
				if (mt.name().equals("Movement")) {
					FontLoaders.NovICON18.drawString("I", 7, categoryY + 20, color2);
				}
				if (mt.name().equals("World")) {
					FontLoaders.NovICON20.drawString("E", 8, categoryY + 20, color2);
				}
				if (mt.name().equals("Player")) {
					FontLoaders.NovICON20.drawString("C", 8, categoryY + 20, color2);
				}
			}
			categoryY += 14;
			++mA2;
		}
		if (Double.isNaN(animtab))animtab = 0d;
		int maxtypes = maxType - 20;
		if (this.section == Section.MODULES || this.section == Section.VALUES) {
			animtab = AnimationUtil.moveUD(animtab, this.section == Section.MODULES? this.maxModule - 39 : this.maxValue, 10.0f / Minecraft.getDebugFPS(), 5.0f / Minecraft.getDebugFPS());
		}else {
			animtab = AnimationUtil.moveUD(animtab, maxtypes - 14, 10.0f / Minecraft.getDebugFPS(), 5.0f / Minecraft.getDebugFPS());
		}

		RenderUtil.prepareScissorBox(0, 0, (float) animtab, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		List<Module> modules = ModuleManager.getModulesInType(this.selectedType);

		modules.removeIf(Module::wasRemoved);
		RenderUtil.drawRoundedRect(maxtypes - 14, moduleY + 20, this.maxModule - 39, moduleY + (
				modules.size() * 12) - 12 + 20 + 15,
				3, color1);

		for (Module m : ModuleManager.getModulesInType(this.selectedType)) {
			if (m.wasRemoved()) {
				continue;
			}
			if (this.selectedModule == m) {
				AnimmodY = AnimationUtils.animate(moduleY, AnimmodY, translationFactor);
				valueY = moduleY;
			}
			font.drawString(m.getName(), maxtypes - 8, moduleY + 20 + 6,
					m.isEnabled() ? new Color(47, 154, 241).getRGB() : color2);
			if (!m.getValues().isEmpty()) {
				FontLoaders.NovICON20.drawString("G", maxtypes + 72, moduleY + 20 + 6,
						color2);
				if (this.selectedModule == m) {
					RenderUtil.drawRoundedRect(this.maxModule - 34, valueY + 20, this.maxValue - 4, valueY + (12 * this.selectedModule.getValues().size()) - 12 + 20 + 15,
							3, color1);
					for (Value val : this.selectedModule.getValues()) {

						if (this.selectedValue == val) {
							AnimValY = AnimationUtils.animate(valueY, AnimValY, translationFactor);
						}
						if (val instanceof Option) {
							font.drawString(val.getDisplayName(), this.maxModule - 27, valueY + 26,
									(Boolean) val.getValue() ? new Color(47, 154, 241).getRGB()
											: color2);
						} else {
							String toRender = String.format("%s: \u00a77%s", val.getDisplayName(),
									val.getValue().toString());
							font.drawString(toRender, this.maxModule - 27, valueY + 20 + 6,
									color2);
						}
						valueY += 12;
						RenderUtil.drawRect(this.maxModule - 23 - 8, AnimValY + 20 + 4, this.maxModule - 23 - 7, AnimValY + 20 + 12,
								new Color(47, 154, 241).getRGB());
					}
				}
			}
			moduleY += 12;
			RenderUtil.drawRect(maxtypes - 12, AnimmodY + 20 + 4, maxtypes - 11, AnimmodY + 20 + 12,
					new Color(47, 154, 241).getRGB());
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@EventHandler
	private void onKey(EventKey e) {
		if (!TabGui.modes.getValue().equals(TabGui.tabguimode.Distance)){
			return;
		}
		if (!Helper.mc.gameSettings.showDebugInfo) {
			block0: switch (e.getKey()) {
			case 208: {
				switch (TabUI.QwQ()[this.section.ordinal()]) {
				case 1: {
					++this.currentType;
					if (this.currentType > ModuleType.values().length - 1) {
						this.currentType = 0;
					}
					this.selectedType = ModuleType.values()[this.currentType];
					break block0;
				}
				case 2: {
					++this.currentModule;
					if (this.currentModule > ModuleManager.getModulesInType(this.selectedType)
							.size() - 1) {
						this.currentModule = 0;
					}
					this.selectedModule = ModuleManager.getModulesInType(this.selectedType)
							.get(this.currentModule);
					if (selectedModule.wasRemoved()) {
						selectedModule = ModuleManager.getModulesInType(this.selectedType)
								.get(this.currentModule + 1);
					}
					break block0;
				}
				case 3: {
					++this.currentValue;
					if (this.currentValue > this.selectedModule.getValues().size() - 1) {
						this.currentValue = 0;
					}
					this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
				}
				}
				break;
			}
			case 200: {
				switch (TabUI.QwQ()[this.section.ordinal()]) {
				case 1: {
					--this.currentType;
					if (this.currentType < 0) {
						this.currentType = ModuleType.values().length - 1;
					}
					this.selectedType = ModuleType.values()[this.currentType];
					break block0;
				}
				case 2: {
					--this.currentModule;
					if (this.currentModule < 0) {
						this.currentModule = ModuleManager.getModulesInType(this.selectedType)
								.size() - 1;
					}
					this.selectedModule = ModuleManager.getModulesInType(this.selectedType)
							.get(this.currentModule);
					if (selectedModule.wasRemoved()) {
						selectedModule = ModuleManager.getModulesInType(this.selectedType)
								.get(this.currentModule - 1);
					}
					break block0;
				}
				case 3: {
					--this.currentValue;
					if (this.currentValue < 0) {
						this.currentValue = this.selectedModule.getValues().size() - 1;
					}
					this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
				}
				}
				break;
			}
			case 205: {
				switch (TabUI.QwQ()[this.section.ordinal()]) {
				case 1: {
					this.currentModule = 0;
					this.selectedModule = ModuleManager.getModulesInType(this.selectedType)
							.get(this.currentModule);
					this.section = Section.MODULES;
					break block0;
				}
				case 2: {
					if (this.selectedModule.getValues().isEmpty())
						break block0;
					this.resetValuesLength();
					this.currentValue = 0;
					this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
					this.section = Section.VALUES;
					break block0;
				}
				case 3: {
					if (this.selectedValue instanceof Option) {
						this.selectedValue.setValue(!((Boolean) this.selectedValue.getValue()));
					} else if (this.selectedValue instanceof Numbers) {
						Numbers value = (Numbers) this.selectedValue;
						double inc = (Double) value.getValue();
						inc += ((Double) value.getIncrement()).doubleValue();
						if ((inc = MathUtil.toDecimalLength(inc, 1)) > (Double) value.getMaximum()) {
							inc = (Double) ((Numbers) this.selectedValue).getMinimum();
						}
						this.selectedValue.setValue(inc);
					} else if (this.selectedValue instanceof Mode) {
						Mode theme = (Mode) this.selectedValue;
						Enum current = (Enum) theme.getValue();
						int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
						this.selectedValue.setValue(theme.getModes()[next]);
					}
					this.resetValuesLength();
				}
				}
				break;
			}
			case 28: {
				switch (TabUI.QwQ()[this.section.ordinal()]) {
				case 1: {
					break block0;
				}
				case 2: {
					this.selectedModule.setEnabled(!this.selectedModule.isEnabled());
					break block0;
				}
				case 3: {
					this.section = Section.MODULES;
				}
				}
				break;
			}
			case 203: {
				switch (TabUI.QwQ()[this.section.ordinal()]) {
				case 1: {
					break block0;
				}
				case 2: {
					this.section = Section.TYPES;
					this.currentModule = 0;
					break block0;
				}
				case 3: {
					if (this.selectedValue instanceof Option) {
						this.selectedValue.setValue((Boolean) this.selectedValue.getValue() == false);
					} else if (this.selectedValue instanceof Numbers) {
						Numbers value = (Numbers) this.selectedValue;
						double inc = (Double) value.getValue();
						inc -= ((Double) value.getIncrement()).doubleValue();
						if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double) value.getMinimum()) {
							inc = (Double) ((Numbers) this.selectedValue).getMaximum();
						}
						this.selectedValue.setValue(inc);
					} else if (this.selectedValue instanceof Mode) {
						Mode theme = (Mode) this.selectedValue;
						Enum current = (Enum) theme.getValue();
						int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
						this.selectedValue.setValue(theme.getModes()[next]);
					}
					this.maxValue = 0;
					for (Value<? extends Object> val : this.selectedModule.getValues()) {
						int off;
						int n = off = val instanceof Option ? 6
								: Minecraft.getMinecraft().fontRendererObj
										.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
						if (this.maxValue > Minecraft.getMinecraft().fontRendererObj
								.getStringWidth(val.getDisplayName().toUpperCase()) + off)
							continue;
						this.maxValue = Minecraft.getMinecraft().fontRendererObj
								.getStringWidth(val.getDisplayName().toUpperCase()) + off;
					}
					this.maxValue += this.maxModule;
				}
				}
			}
			}
		}
	}

	static /* synthetic */ int[] QwQ() {
		int[] arrn;
		int[] arrn2 = QwQ;
		if (arrn2 != null) {
			return arrn2;
		}
		arrn = new int[Section.values().length];
		try {
			arrn[Section.MODULES.ordinal()] = 2;
			arrn[Section.TYPES.ordinal()] = 1;
			arrn[Section.VALUES.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError) {
		}
		QwQ = arrn;
		return QwQ;
	}

	public enum Section {
		TYPES, MODULES, VALUES;
	}
}
