package cms.mortalchen.distance.irc.utils.packets.serverside;

import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ServerRegisterResultPacket extends IRCPacket {
    public ServerRegisterResultPacket(long time, String content) {
        super(time, content, IRCType.REGISTER);
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
