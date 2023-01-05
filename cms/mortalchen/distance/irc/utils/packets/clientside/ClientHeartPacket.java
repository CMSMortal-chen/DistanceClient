package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientHeartPacket extends IRCPacket {
    public ClientHeartPacket(long time, String content) {
        super(time, content, IRCType.HEART);
    }
    @Override
    public String toJson(){
        jsonObject.put("side","client");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        return jsonObject.toString();
    }
}
