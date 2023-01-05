package cms.mortalchen.distance.irc.utils.packets.serverside;


import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

@ObfuscationClass
public class ServerCommandPacket extends IRCPacket {
    public ServerCommandPacket(long time, String content){
        super(time,content, IRCType.COMMAND);
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
