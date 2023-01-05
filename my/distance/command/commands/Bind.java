package my.distance.command.commands;

import my.distance.Client;
import my.distance.module.Module;
import my.distance.util.misc.Helper;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import my.distance.command.Command;

public class Bind extends Command {
	public Bind() {
		super("Bind", new String[] { "b" }, "", "绑定模块到指定按键");
	}

	@Override
	public String execute(String[] args) {
		if (args.length >= 2) {
			Module m = Client.instance.getModuleManager().getAlias(args[0]);
			if (m != null) {
				int k = Keyboard.getKeyIndex((String) args[1].toUpperCase());
				m.setKey(k);
				Object[] arrobject = new Object[2];
				arrobject[0] = m.getName();
				arrobject[1] = k == 0 ? "none" : args[1].toUpperCase();
				Helper.sendMessage(String.format("绑定模块 %s 到 %s", arrobject));
			} else {
				Helper.sendMessage("模块: " + (Object) ((Object) EnumChatFormatting.RED) + args[0]
						+ (Object) ((Object) EnumChatFormatting.GRAY) + " 不存在");
			}
		} else {
			Helper.sendMessage("语法错误 .bind <module> <key>");
		}
		return null;
	}
}
