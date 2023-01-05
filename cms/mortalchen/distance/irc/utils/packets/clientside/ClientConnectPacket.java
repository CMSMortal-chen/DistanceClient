package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientConnectPacket extends IRCPacket {

    public String username = "";
    public String password = "";
    public String hwid = "";

    public ClientConnectPacket(long time, String content, String username, String password, String hwid) {
        super(time, content, IRCType.CONNECT);
        this.username = username;
        this.password = password;
        this.hwid = hwid;
    }

    @Override
    public String toJson() {
        jsonObject.put("side","client");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("hwid", hwid);
        return jsonObject.toString();
    }
}
