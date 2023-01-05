package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.Client;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.misc.liquidbounce.LiquidRender;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.TimerUtil;
import my.distance.fastuni.FastUniFontRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Xray extends Module {
	float ULX2 = 2;
	static FastUniFontRenderer font = Client.FontLoaders.Chinese16;
	public static Numbers<Double> OPACITY = new Numbers<>("Opacity", 160.0, 0.0, 255.0, 5.0);
	public static Numbers<Double> Dis = new Numbers<>("RenderDistance", 5.0, 0.0, 256.0, 1.0);
	public static Option CAVE = new Option("Bypass", true);
	public static Option Coord = new Option("Coord", false);
	public static Option Tags = new Option("Tags", false);
	public static Option Tracers = new Option("Tracers", false);
	public static Option ESP = new Option("ESP", true);
	public static Option CoalOre = new Option("Coal Ore", true);
	public static Option RedStoneOre = new Option("RedStone Ore", true);
	public static Option IronOre = new Option("Iron Ore", true);
	public static Option GoldOre = new Option("Gold Ore", true);
	public static Option DiamondOre = new Option("Diamond Ore", true);
	public static Option EmeraldOre = new Option("Emerald Ore", true);
	public static Option LapisOre = new Option("Lapis Ore", true);
	public static CopyOnWriteArrayList<BlockPos> renderList = new CopyOnWriteArrayList<>();


	public Xray() {
		super("Xray", new String[]{"Xray"}, ModuleType.Render);
		this.addValues(OPACITY, CAVE, Tags, Tracers, Coord, ESP, Dis, CoalOre, RedStoneOre, IronOre, GoldOre, DiamondOre, EmeraldOre, LapisOre);
	}

	@Override
	public void onEnable() {
		mc.renderGlobal.loadRenderers();
	}

	@EventHandler
	private void on3DRender(EventRender3D e) {
		if (Tracers.getValue()) {
			for (BlockPos blockPos : renderList) {
				double[] arrd = new double[3];
				double posX = blockPos.getX() - mc.getRenderManager().renderPosX;
				double posY = blockPos.getY() - mc.getRenderManager().renderPosY;
				double posZ = blockPos.getZ() - mc.getRenderManager().renderPosZ;
				boolean old = mc.gameSettings.viewBobbing;
				RenderUtil.startDrawing();
				mc.gameSettings.viewBobbing = false;
				mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
				mc.gameSettings.viewBobbing = old;

				arrd[0] = 23.0;
				arrd[1] = 221.0;
				arrd[2] = 98.0;
				this.drawLine(arrd, posX, posY, posZ);
				RenderUtil.stopDrawing();
			}
		}
	}

	private Thread blockFinderThread = null;
	private final TimerUtil timerUtil = new TimerUtil();
	private final CopyOnWriteArrayList<BlockPos> blockList = new CopyOnWriteArrayList<>();

	@EventHandler
	public void onEventUpdate(EventPreUpdate e) {
		int range = Dis.getValue().intValue();
		if (timerUtil.hasReached(1000L) && !(blockFinderThread != null && blockFinderThread.isAlive())) {
			blockFinderThread = new Thread(() -> {
				blockList.clear();
				for (int x = -range; x < range; x++) {
					for (int y = range; y > -range; y--) {
						for (int z = -range; z < range; z++) {
							BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
							if (CAVE.getValue() && !oreTest(pos, 2d)) {
								continue;
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore) && DiamondOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore) && IronOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore) && GoldOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore) && CoalOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore) && RedStoneOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore) && LapisOre.getValue()) {
								blockList.add(pos);
							}
							if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore) && EmeraldOre.getValue()) {
								blockList.add(pos);
							}
						}
					}
				}
			}, "Xray-FinderThread");
			blockFinderThread.start();
			timerUtil.reset();
			renderList.clear();
			renderList.addAll(blockList);
		}
	}

	@EventHandler
	private void DrawTags(EventRender3D e) {
		if (Tags.getValue()) {
			for (BlockPos pos : renderList) {
				double posX = pos.getX() - mc.getRenderManager().renderPosX;
				double posY = pos.getY() - mc.getRenderManager().renderPosY;
				double posZ = pos.getZ() - mc.getRenderManager().renderPosZ;
				boolean old = mc.gameSettings.viewBobbing;
				mc.gameSettings.viewBobbing = false;
				mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
				mc.gameSettings.viewBobbing = old;

				rendertag(mc.theWorld.getBlockState(pos).getBlock().getLocalizedName(), posX, posY, posZ, pos.getX(), pos.getY(), pos.getZ(), getColor(pos).getRGB());
			}
		}
	}

	public static boolean needRender(Block block) {
		if (!(block instanceof BlockOre)) {
			return false;
		}
		if (block.equals(Blocks.diamond_ore) && DiamondOre.getValue()) {
			return true;
		}
		if (block.equals(Blocks.gold_ore) && GoldOre.getValue()) {
			return true;
		}
		if (block.equals(Blocks.lapis_ore) && LapisOre.getValue()) {
			return true;
		}
		if (block.equals(Blocks.iron_ore) && IronOre.getValue()) {
			return true;
		}
		if (block.equals(Blocks.emerald_ore) && EmeraldOre.getValue()) {
			return true;
		}
		if (block.equals(Blocks.redstone_ore) && RedStoneOre.getValue()) {
			return true;
		}
		return block.equals(Blocks.coal_ore) && CoalOre.getValue();
	}

	private void drawLine(double[] color, double x, double y, double z) {
		GL11.glEnable(2848);
		GL11.glColor4f((float) color[0], (float) color[1], (float) color[2], 0.3f);
		GL11.glLineWidth(1.0f);
		GL11.glBegin(1);
		GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
		GL11.glVertex3d(x + 0.5, y + 0.5, z + 0.5);
		GL11.glEnd();
		GL11.glDisable(2848);
	}

	public void onDisable() {
		renderList.clear();
		mc.renderGlobal.loadRenderers();
	}


	@EventHandler
	private void renderHud(EventRender2D event) {
		float ULY2 = RenderUtil.height() / 3f;

		float last = 2;
		if (Coord.getValue()) {
			List<String> aaa = new ArrayList<>();
			for (BlockPos pos : renderList) {
				if (!aaa.contains(pos.getX() + ", " + pos.getY() + ", " + pos.getZ())) {
					aaa.add(pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
					String Textx = pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "[" + (int) mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) + "]";

					if (font.getStringWidth(Textx) + 4 > ULX2) {
						ULX2 = font.getStringWidth(Textx) + 4;
					}
					ULY2 += 8;
					font.drawStringWithShadow(Textx, last, ULY2, getColor(pos).getRGB());
					if (RenderUtil.height() / 3f + 30 * 8 == ULY2) {
						ULY2 = RenderUtil.height() / 3f;
						last += ULX2;
						ULX2 = 0;
					}
				}
			}
		}
	}


	private static float getSize(double x, double y, double z) {
		return Math.max((float) (mc.thePlayer.getDistance(x, y, z)) / 4.0f, 2.0f);
	}

	private static void startDrawing(double x, double y, double z, double StringX, double StringY, double StringZ) {
		float var10001 = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
		double size = Config.zoomMode ? (double) (getSize(StringX, StringY, StringZ) / 10.0f) * 1.6
				: (double) (getSize(StringX, StringY, StringZ) / 10.0f) * 4.8;
		GL11.glPushMatrix();
		RenderUtil.startDrawing();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glNormal3f(0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(mc.getRenderManager().playerViewX, var10001, 0.0f, 0.0f);
		GL11.glScaled(-0.01666666753590107 * size, -0.01666666753590107 * size,
				0.01666666753590107 * size);
	}

	private static void stopDrawing() {
		RenderUtil.stopDrawing();
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		GlStateManager.popMatrix();
	}

	public static void rendertag(String Str, double x, double y, double z, double StringX, double StringY, double StringZ, int color) {
		y = (y + 1.55);
		startDrawing(x + 0.5, y, z + 0.5, StringX, StringY, StringZ);
		drawNames(Str, color);
		GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
		stopDrawing();
	}

	private static void drawNames(String Str, int Color) {
		float xP = 2.2f;
		float width = (float) getWidth(Str) / 2.0f + xP;
		float w = width = (float) ((double) width + 2.5);
		float nw = -width - xP;
		float offset = getWidth(Str) + 4;
		RenderUtil.drawFastRoundedRect(nw + 6.0f, -1.0f, width, 10.0f, 1.0f, new Color(20, 20, 20, 80).getRGB());
		drawString(Str, w - offset + 2, 2.0f, Color);
	}

	private static void drawString(String text, float x, float y, int color) {
		font.drawStringWithShadow(text, x, y, color);
	}

	private static int getWidth(String text) {
		return font.getStringWidth(text);
	}


	@EventHandler
	public void onEvent(EventRender3D event) {
		if (ESP.getValue()) {
			for (BlockPos blockPos : renderList) {
				LiquidRender.drawBlockBox(blockPos,
						getColor(blockPos), true);
			}
		}
	}

	public static Color getColor(BlockPos pos) {
		Color color = new Color(0, 0, 0);
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore)) {
			color = new Color(54, 194, 255, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore)) {
			color = new Color(255, 192, 115, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore)) {
			color = new Color(255, 221, 0, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore)) {
			color = new Color(50, 50, 50, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore)) {
			color = new Color(255, 73, 73, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore)) {
			color = new Color(0, 42, 255, 50);
		}
		if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore)) {
			color = new Color(103, 255, 48, 50);
		}
		return color;
	}

	private boolean oreTest(BlockPos origPos, Double depth) {
		Collection<BlockPos> posesNew = new ArrayList<>();
		Collection<BlockPos> posesLast = new ArrayList<>(Collections.singletonList(origPos));
		Collection<BlockPos> finalList = new ArrayList<>();
		for (int i = 0; i < depth; i++) {
			for (BlockPos blockPos : posesLast) {
				posesNew.add(blockPos.up());
				posesNew.add(blockPos.down());
				posesNew.add(blockPos.north());
				posesNew.add(blockPos.south());
				posesNew.add(blockPos.west());
				posesNew.add(blockPos.east());
			}
			for (BlockPos pos : posesNew) {
				if (posesLast.contains(pos)) {
					posesNew.remove(pos);
				}
			}
			posesLast = posesNew;
			finalList.addAll(posesNew);
			posesNew = new ArrayList<>();
		}

		List<Block> legitBlocks = Arrays.asList(Blocks.water, Blocks.lava, Blocks.flowing_lava, Blocks.air,
				Blocks.flowing_water, Blocks.fire);

		return finalList.stream()
				.anyMatch(blockPos -> legitBlocks.contains(mc.theWorld.getBlockState(blockPos).getBlock()));
	}

	public int getOpacity() {
		return OPACITY.getValue().intValue();
	}
}
