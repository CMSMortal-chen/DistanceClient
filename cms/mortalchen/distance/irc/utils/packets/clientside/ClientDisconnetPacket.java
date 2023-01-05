package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientDisconnetPacket extends IRCPacket {
    public ClientDisconnetPacket(long time, String content){
        super(time,content, IRCType.STOP);
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
