package my.distance.util.misc;

import my.distance.api.verify.SHWID;
import my.distance.ui.ClientNotification;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.world.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public enum Helper {
	INSTANCE;
	private static final ArrayList<ClientNotification> notifications = new ArrayList<>();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean onetime = true;

	public static void showURL(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void drawNotifications() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		double startY = res.getScaledHeight() - 40;
		final double lastY = startY;
		for (int i = 0; i < notifications.size(); i++) {
			ClientNotification not = notifications.get(i);
			if (not.shouldDelete())
				notifications.remove(i);
			not.draw(startY, lastY);
			startY -= not.getHeight() + 3;
		}
		if(notifications.size() > 10) {
			for (ClientNotification not : notifications) {
				not.setFinished();
			}
			if (onetime) {
				Notifications.getManager().post("通知已满", "已清空所有通知");
				onetime = false;
			}
		}else if (notifications.size() < 9){
			onetime = true;
		}
	}
	public static void disableFastRender() {
		mc.gameSettings.ofFastRender = false;
	}
	public static boolean isBlockBetween(BlockPos start, BlockPos end) {
		int startX = start.getX();
		int startY = start.getY();
		int startZ = start.getZ();
		int endX = end.getX();
		int endY = end.getY();
		int endZ = end.getZ();
		double diffX = endX - startX;
		double diffY = endY - startY;
		double diffZ = endZ - startZ;
		double x = startX;
		double y = startY;
		double z = startZ;
		int STEPS = (int)Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

		for(int i = 0; i < STEPS - 1; ++i) {
			x += diffX / (double)STEPS;
			y += diffY / (double)STEPS;
			z += diffZ / (double)STEPS;
			if (x != (double)endX || y != (double)endY || z != (double)endZ) {
				BlockPos pos = new BlockPos(x, y, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
					return true;
				}
			}
		}

		return false;
	}
	public static void Verify(){
		SHWID.verify();
	}
	public static void sendMessage(String message) {
		new ChatUtils.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build()
				.displayClientSided();
	}
	public static void sendMessageIRC(String message) {
		new ChatUtils.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build()
				.displayClientSided();
	}

	public static boolean onServer(String server) {
		return !mc.isSingleplayer() && Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server.toLowerCase());
	}

	public static void sendMessageWithoutPrefix(String string) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(string));
	}
	public static void sendClientMessage(String message, ClientNotification.Type type) {
		 notifications.add(new ClientNotification(message, type));
	}
}
