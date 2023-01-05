package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientRegisterPacket extends IRCPacket {

    public String username;
    public String password;
    public String hwid;
    public String code;
    public String qq;

    public ClientRegisterPacket(long time, String content, String username, String password, String hwid, String qq, String code) {
        super(time, content, IRCType.REGISTER);
        this.username = username;
        this.password = password;
        this.hwid = hwid;
        this.code = code;
        this.qq = qq;
    }
    @Override
    public String toJson() {
        jsonObject.put("side", "client");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("hwid", hwid);
        jsonObject.put("code", code);
        jsonObject.put("qq", qq);
        return jsonObject.toString();
    }
}
