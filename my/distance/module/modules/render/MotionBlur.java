package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class MotionBlur extends Module {
	public enum Modes {
		Little, Normal, Large
	}

	private final Mode mode = new Mode("Mode", "mode", Modes.values(), Modes.Normal);

	public MotionBlur() {
		super("MotionBlur", new String[] { "MotionBlur" }, ModuleType.Render);
		super.addValues(mode);
	}

	@Override
	public void onDisable() {
		Module.mc.entityRenderer.theShaderGroup = null;
	}

	@Override
	public void onEnable() {
		if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (Module.mc.entityRenderer.theShaderGroup != null) {
				Module.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
			}
			switch ((Modes)mode.getValue()) {
				case Little:
					Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Little.json"));
					break;
				case Normal:
					Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Normal.json"));
					break;
				case Large:
					Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Large.json"));
					break;
			}

		}

	}

	@EventHandler
	public void onUpdate(EventPreUpdate e) {
		setSuffix(mode.getValue());
		if (Module.mc.entityRenderer.theShaderGroup == null
				|| !Module.mc.entityRenderer.theShaderGroup.getShaderGroupName().contains("MotionBlur")) {
			if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
				if (Module.mc.entityRenderer.theShaderGroup != null) {
					Module.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
				}
				switch (mode.getValue().name()) {
					case "Little":
						Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Little.json"));
						break;
					case "Normal":
						Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Normal.json"));
						break;
					case "Large":
						Module.mc.entityRenderer.loadShader(new ResourceLocation("MotionBlur/Large.json"));
						break;
				}

			}
		}
	}
}
