package my.distance.command.commands;
import my.distance.command.Command;
import my.distance.ui.ClientNotification;
import my.distance.util.misc.Helper;
import net.minecraft.network.play.client.C01PacketChatMessage;


public class Say
        extends Command {
    boolean sending = false;
    public Say() {
        super("say", new String[]{"say"}, "", "发送聊天信息(不经过事件管理器)");
    }


    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            sending = false;
            Helper.sendClientMessage(".say <Text>", ClientNotification.Type.warning);
        } else {
            String msg = "";
            sending = true;
            int i = 0;
            while (i < args.length) {
                msg = String.valueOf(String.valueOf(String.valueOf(msg))) + args[i] + " ";
                i++;
            }
            msg = msg.substring(0, msg.length() - 1);

            mc.getNetHandler().addToSendQueueSilent(new C01PacketChatMessage(msg));
        }

        if (sending)Helper.sendClientMessage("信息已发送到游戏聊天", ClientNotification.Type.info);
        return null;
    }
}

