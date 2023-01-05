package cms.mortalchen.distance.irc.utils.packets.serverside;

import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ServerStopPacket extends IRCPacket {
    public ServerStopPacket(long time, String content) {
        super(time, content, IRCType.STOP);
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
