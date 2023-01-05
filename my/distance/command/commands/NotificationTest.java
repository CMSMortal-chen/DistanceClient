package my.distance.command.commands;


import my.distance.command.Command;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.misc.Helper;

public class NotificationTest extends Command {
    public NotificationTest() {
        super("NotificationTest",new String[]{"noti"}, "", "测试通知系统");
    }
    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendMessage(".noti [notify/warning/info/f]");
        } else {
            Notifications not = Notifications.getManager();
            if (args[0].equalsIgnoreCase("notify")) {
                not.post("Speed", "检测到拉回！", 2500L, Notifications.Type.NOTIFY);
            } else if (args[0].equalsIgnoreCase("warning")) {
                not.post("Bypass", "检测到Hypixel!但是Disabler没有启动", 2500L, Notifications.Type.WARNING);
            } else if (args[0].equalsIgnoreCase("info")) {
                not.post("AutoFish", "貌似有东西上钩了( >ω< )", 2500L, Notifications.Type.INFO);
            } else if (args[0].equalsIgnoreCase("f")) {
                not.post("FFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 2500L, Notifications.Type.INFO);
            }else {
                Helper.sendMessage(" ???什么鬼");
            }

        }
        return null;
    }

    public String getUsage() {
        return null;
    }
}
