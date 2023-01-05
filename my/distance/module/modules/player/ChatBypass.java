package my.distance.module.modules.player;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.EventPacketReceive;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.value.Mode;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class ChatBypass extends Module {
    private final Mode mode = new Mode("Mode",ChatBypassMode.values(),ChatBypassMode.Hypixel);
    public ChatBypass() {
        super("ChatBypass", new String[] { "ChatBypass", "ChatBypass" }, ModuleType.Player);
        addValues(mode);
    }

    @EventHandler
    public void onRender2d(EventRender2D e){
        this.setSuffix("Packet");
    }

    @EventHandler
    public void onPacketReceive(EventPacketReceive e){
        if (e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            if (packet.getChatComponent().getUnformattedText().contains("℡")) {
                packet.setChatComponent(new ChatComponentText(packet.getChatComponent().getUnformattedText().replace("℡", "")));
            }
        }
    }

    @EventHandler
    public final void onSendPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packetChatMessage = (C01PacketChatMessage) e.getPacket();
            boolean shout = packetChatMessage.getMessage().startsWith("/shout");
            if (packetChatMessage.getMessage().startsWith("/") && !shout) {
                return;
            }
            if (shout) {
                packetChatMessage.setMessage(packetChatMessage.getMessage().replaceFirst("/shout", ""));
            }
            switch ((ChatBypassMode)mode.getValue()){
                case Hypixel:
                    packetChatMessage.setMessage(insertPeriodically(packetChatMessage.getMessage(), "\u26CD\u26D7\u26CC\u26D7\u26D8\u26C9\u26E1\u26CD\u26D7\u26C9\u26CD\u26D8\u26DC\u26CD\u26E0\u26D8\u26DF\u26CF\u26E1\u26CF\u26D7\u26CF\u26CD\u26C9\u26CB\u05FC", 1));
                    break;
                case Test:
                    String text = packetChatMessage.getMessage();
                    StringBuilder builder = new StringBuilder(text.length() + ".".length() * (text.length()) + 1);
                    int index = 0;
                    String prefix = "";
                    while (index < text.length()) {
                        // Don't put the insert in the very first iteration.
                        // This is easier than appending it *after* each substring
                        builder.append(prefix);
                        Random random = new Random();
                        String bypass = ".,';`\"";
                        prefix = Character.toString(bypass.charAt(random.nextInt(bypass.length())));
                        builder.append(text, index, Math.min(index + 1, text.length()));
                        index += 1;
                    }
                    packetChatMessage.setMessage(builder.toString());
                    break;
                case Roblox:
                    packetChatMessage.setMessage(StringUtils.replaceChars(packetChatMessage.getMessage(), "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM", "\u024A\u5C71\u4E47\u5C3A\u3112\u311A\u3129\u4E28\u3116\u5369\u5342\u4E02\u15EA\u5343\u13B6\u5344\uFF8C\u049C\u3125\u4E59\u4E42\u531A\u142F\u4E43\u51E0\u722A1234567890\u024A\u5C71\u4E47\u5C3A\u3112\u311A\u3129\u4E28\u3116\u5369\u5342\u4E02\u15EA\u5343\u13B6\u5344\uFF8C\u049C\u3125\u4E59\u4E42\u531A\u142F\u4E43\u51E0\u722A"));
                    break;
                case Russian:
                    packetChatMessage.setMessage(StringUtils.replaceChars(packetChatMessage.getMessage(), "ABESZIKMHOPCTXWVYaekmotb3hpcyx", "\u0410\u0412\u0415\u0405Z\u0406\u041A\u041C\u041D\u041E\u0420\u0421\u0422\u0425\u0428\u0474\u0423\u0430\u0435\u043A\u043C\u043E\u0442\u0432\u0437\u043D\u0440\u0441\u0443\u0445"));
                    break;
            }
            if (shout) {
                packetChatMessage.setMessage("/shout " + packetChatMessage.getMessage());
            }
        }
    }
    public static String insertPeriodically(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);

            Random random = new Random();

            //String bypass = "??????????????????????????";
            prefix = Character.toString(insert.charAt(random.nextInt(insert.length())));

            builder.append(text, index, Math.min(index + period, text.length()));
            index += period;
        }
        return builder.toString();
    }
    enum ChatBypassMode{
        Russian, Roblox, Hypixel, Test
    }
}

