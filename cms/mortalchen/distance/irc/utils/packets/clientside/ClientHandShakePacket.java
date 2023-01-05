package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientHandShakePacket extends IRCPacket {
    public ClientHandShakePacket(long time, String publicKey){
        super(time,publicKey, IRCType.HANDSHAKE);
    }

    @Override
    public String toJson(){
        jsonObject.put("side","client");
        jsonObject.put("time", time);
        jsonObject.put("key", content);
        jsonObject.put("content", "");
        jsonObject.put("type", type.name());
        return jsonObject.toString();
    }
}
