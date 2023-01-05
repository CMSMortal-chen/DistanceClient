package cms.mortalchen.distance.irc.utils.packets.clientside;


import LemonObfAnnotation.ObfuscationClass;
import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

@ObfuscationClass
public class ClientVerifyPacket extends IRCPacket {

    public String hwid;
    public String version;
    public boolean isBeta;

    public ClientVerifyPacket(long time, String content,String hwid,String version,boolean beta) {
        super(time, content, IRCType.VERIFY);
        this.hwid = hwid;
        this.isBeta = beta;
        this.version = version;
    }
    @Override
    public String toJson() {
        jsonObject.put("side","client");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        jsonObject.put("hwid", hwid);
        jsonObject.put("version", version);
        jsonObject.put("isBeta", isBeta);
        return jsonObject.toString();
    }
}
