package my.distance.command.commands;

import my.distance.command.Command;
import my.distance.module.modules.player.AutoSay;
import my.distance.ui.ClientNotification;
import my.distance.util.misc.Helper;

public class Spammer extends Command {
    public Spammer() {
        super("Spammer", new String[]{"spam"}, "", "更改CustomSpammer的内容");
    }


    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendClientMessage(".spam <Text>", ClientNotification.Type.warning);
        } else {
            int i = 0;
            String msg = "";
            while (i < args.length) {
                msg = msg + args[i] + " ";
                i++;
            }
            msg = msg.substring(0, msg.length() - 1);
            AutoSay.CustomString = msg;
        }
        return null;
    }
}
