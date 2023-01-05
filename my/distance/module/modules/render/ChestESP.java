package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.value.Mode;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.misc.liquidbounce.LiquidRender;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.*;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ChestESP extends Module {

	private final Mode modeValue = new Mode("Mode", Modes.values(), Modes.OtherBox);

	private final Option chestValue = new Option("Chest", true);
	private final Option enderChestValue = new Option("EnderChest", true);
	private final Option furnaceValue = new Option("Furnace", true);
	private final Option dispenserValue = new Option("Dispenser", true);
	private final Option hopperValue = new Option("Hopper", true);

	public ChestESP(){
		super("ChestESP",new String[]{"StorageESP"}, ModuleType.Render);
		addValues(modeValue,chestValue,enderChestValue,furnaceValue,dispenserValue,hopperValue);
	}
	@EventHandler
	public void onRender3D(EventRender3D event) {
		try {
			float gamma = mc.gameSettings.gammaSetting;
			mc.gameSettings.gammaSetting = 100000.0F;

			for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
				Color color = null;

				if (chestValue.getValue() && tileEntity instanceof TileEntityChest)
					color = new Color(255, 136, 81);

				if (enderChestValue.getValue() && tileEntity instanceof TileEntityEnderChest)
					color = Color.MAGENTA;

				if (furnaceValue.getValue() && tileEntity instanceof TileEntityFurnace)
					color = Color.BLACK;

				if (dispenserValue.getValue() && tileEntity instanceof TileEntityDispenser)
					color = Color.BLACK;

				if (hopperValue.getValue() && tileEntity instanceof TileEntityHopper)
					color = Color.GRAY;

				if (color == null)
					continue;

				if (!(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)) {
					LiquidRender.drawBlockBox(tileEntity.getPos(), color, !modeValue.getValue().equals(Modes.OtherBox));
					continue;
				}

				switch ((Modes)modeValue.getValue()) {
					case OtherBox:
					case Box:
						LiquidRender.drawBlockBox(tileEntity.getPos(), color, !modeValue.getValue().equals(Modes.OtherBox));
						break;
					case TowD:
						LiquidRender.draw2D(tileEntity.getPos(), color.getRGB(), Color.BLACK.getRGB());
						break;
					case WireFrame:
						glPushMatrix();
						glPushAttrib(GL_ALL_ATTRIB_BITS);
						glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
						glDisable(GL_TEXTURE_2D);
						glDisable(GL_LIGHTING);
						glDisable(GL_DEPTH_TEST);
						glEnable(GL_LINE_SMOOTH);
						glEnable(GL_BLEND);
						glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
						LiquidRender.glColor(color);
						glLineWidth(1.5F);
						TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.getPartialTicks(), -1);
						glPopAttrib();
						glPopMatrix();
						break;
				}
			}

			for (final Entity entity : mc.theWorld.loadedEntityList)
				if (entity instanceof EntityMinecartChest) {
					switch ((Modes)modeValue.getValue()) {
						case OtherBox:
						case Box:
							LiquidRender.drawEntityBox(entity, new Color(0, 66, 255,80), !modeValue.getValue().equals(Modes.OtherBox));
							break;
						case TowD:
							LiquidRender.draw2D(entity.getPosition(), new Color(0, 66, 255).getRGB(), Color.BLACK.getRGB());
							break;
						case WireFrame: {
							final boolean entityShadow = mc.gameSettings.entityShadows;
							mc.gameSettings.entityShadows = false;

							glPushMatrix();
							glPushAttrib(GL_ALL_ATTRIB_BITS);
							glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
							glDisable(GL_TEXTURE_2D);
							glDisable(GL_LIGHTING);
							glDisable(GL_DEPTH_TEST);
							glEnable(GL_LINE_SMOOTH);
							glEnable(GL_BLEND);
							glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
							LiquidRender.glColor(new Color(0, 66, 255));
							mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
							LiquidRender.glColor(new Color(0, 66, 255));
							glLineWidth(1.5F);
							mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
							glPopAttrib();
							glPopMatrix();

							mc.gameSettings.entityShadows = entityShadow;
							break;
						}
					}
				}

			LiquidRender.glColor(new Color(255, 255, 255, 255));
			mc.gameSettings.gammaSetting = gamma;
		} catch (Exception ignored) {
		}
	}
	enum Modes{
		Box, OtherBox,TowD, WireFrame
	}
}
