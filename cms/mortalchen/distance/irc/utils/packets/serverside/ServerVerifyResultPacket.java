package cms.mortalchen.distance.irc.utils.packets.serverside;

import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

@ObfuscationClass
public class ServerVerifyResultPacket extends IRCPacket {
    public ServerVerifyResultPacket(long time, String content) {
        super(time, content, IRCType.VERIFY);
    }
    @Override
    public String toJson(){
        jsonObject.put("side","server");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        return jsonObject.toString();
    }
}
