package cms.mortalchen.distance.irc.utils.packets.serverside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ServerHandShakePacket extends IRCPacket {
    public ServerHandShakePacket(long time, String publicKey){
        super(time,publicKey, IRCType.HANDSHAKE);
    }
    @Override
    public String toJson(){
        jsonObject.put("side","server");
        jsonObject.put("time", time);
        jsonObject.put("content", "");
        jsonObject.put("key", content);
        jsonObject.put("type", type.name());
        return jsonObject.toString();
    }
}
