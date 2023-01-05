package my.distance.command.commands;

import my.distance.command.Command;
import my.distance.manager.CommandManager;
import my.distance.util.misc.Helper;

public class Help extends Command {
	public Help() {
		super("Help", new String[] { "list" }, "", "列出所有可用指令");
	}

	@Override
	public String execute(String[] args) {
		if (args.length == 0) {
			Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
			Helper.sendMessageWithoutPrefix("\u00a7b\u00a7lDistance");
            for (Command c : CommandManager.commands){
            	if (c.getHelp().contains("设置此模块"))continue;
				Helper.sendMessageWithoutPrefix("\u00a7b."+c.getName().toLowerCase()+" >\u00a77 "+c.getHelp());
			}
			Helper.sendMessageWithoutPrefix("\u00a7b.(模块名) >\u00a77 设置模块参数");

			Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
		} else {
			Helper.sendMessage("Correct usage .help");
		}
		return null;
	}
}
