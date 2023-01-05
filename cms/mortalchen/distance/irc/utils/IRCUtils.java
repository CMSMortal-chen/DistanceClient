package cms.mortalchen.distance.irc.utils;

import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.serverside.*;
import org.json.JSONObject;

import java.util.Locale;

@ObfuscationClass
public class IRCUtils {
    public static boolean isPacket(String packet) {
        if (packet == null) {
            return false;
        }
        return packet.startsWith("{");
    }

    public static String toJson(IRCPacket packet) {
        return packet.toJson();
    }

    public static IRCPacket coverToPacket(String json) {
        IRCPacket packet = null;
        JSONObject jsonPakcet = new JSONObject(json);
        String side = jsonPakcet.getString("side");
        long time = jsonPakcet.getLong("time");
        String content = jsonPakcet.getString("content");
        String type = jsonPakcet.getString("type");

        if (!side.equalsIgnoreCase("client")) {
            switch (type.toUpperCase(Locale.ROOT)) {
                case "CHAT": {
                    packet = new ServerChatPacket(time, content);
                    break;
                }
                case "HEART": {
                    packet = new ServerHeartNeededPacket(time, content);
                    break;
                }
                case "CONNECT": {
                    System.err.println("Illegal server packet!");
                    break;
                }
                case "HANDSHAKE":{
                    String publicKey = jsonPakcet.getString("key");
                    packet = new ServerHandShakePacket(time, publicKey);
                    break;
                }
                case "STOP": {
                    packet = new ServerStopPacket(time, content);
                    break;
                }
                case "COMMAND": {
                    packet = new ServerCommandPacket(time, content);
                    break;
                }
                case "TELL": {
                    System.err.println("Tell packet is current unable!");
                    break;
                }
                case "VERIFY": {
                    packet = new ServerVerifyResultPacket(time, content);
                    break;
                }
                case "REGISTER": {
                    packet = new ServerRegisterResultPacket(time, content);
                    break;
                }
                case "UPDATEHWID": {
                    packet = new ServerUpdateHwidResultPacket(time, content);
                    break;
                }
                default: {
                    System.err.println("Unknown packet type!");
                    break;
                }
            }
        }else {
            return null;
        }
        return packet;
    }
}
