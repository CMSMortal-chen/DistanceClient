/*
 * Decompiled with CFR 0_132.
 */
package my.distance.command;

import my.distance.util.misc.Helper;
import net.minecraft.client.Minecraft;

public abstract class Command {
	public static Minecraft mc = Minecraft.getMinecraft();
	private String name;
	private String[] alias;
	private String syntax;
	private String help;

	public Command(String name, String[] alias, String syntax, String help) {
		this.name = name.toLowerCase();
		this.syntax = syntax.toLowerCase();
		this.help = help;
		this.alias = alias;
	}

	public abstract String execute(String[] var1);

	public String getName() {
		return this.name;
	}

	public String[] getAlias() {
		return this.alias;
	}

	public String getSyntax() {
		return this.syntax;
	}

	public String getHelp() {
		return this.help;
	}

	public void syntaxError(String msg) {
		Helper.sendMessage(String.format("\u00a77非法命令语法: %s", msg));
	}
}
