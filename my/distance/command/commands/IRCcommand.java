package my.distance.command.commands;
import my.distance.command.Command;
import my.distance.manager.ModuleManager;
import my.distance.module.modules.world.IRC;
import my.distance.util.misc.Helper;


import net.minecraft.util.EnumChatFormatting;

public class IRCcommand
        extends Command {
    public IRCcommand() {
        super("IRC", new String[]{"c"}, "", "发送一个IRC消息");
    }

    @Override
    public String execute(String[] args) {
        String msg;
        if (args.length == 0) {
            Helper.sendMessageWithoutPrefix(EnumChatFormatting.GREEN+"[DistanceIRC]"+EnumChatFormatting.WHITE+".irc <text>");
            return null;
        } else {
            StringBuilder stringBuilder = null;
            boolean a = false;
            for (String s : args) {
                if (!a) {
                    stringBuilder = new StringBuilder(s);
                    a = true;
                    continue;
                }
                stringBuilder.append(" ").append(s);
            }
            msg = stringBuilder.toString();
            if (ModuleManager.getModuleByClass(IRC.class).isEnabled()) {
                IRC.sendIRCMessage(msg);
            }
        }

        return null;
    }
}





