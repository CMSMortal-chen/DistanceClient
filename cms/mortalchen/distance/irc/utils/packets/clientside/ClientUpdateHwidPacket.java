package cms.mortalchen.distance.irc.utils.packets.clientside;


import cms.mortalchen.distance.irc.utils.packets.IRCPacket;
import cms.mortalchen.distance.irc.utils.packets.IRCType;

public class ClientUpdateHwidPacket extends IRCPacket {
    public String userName;
    public String passWord;
    public String newHwid;

    public ClientUpdateHwidPacket(long time, String content,String name, String pass,String hwid){
        super(time,content, IRCType.UPDATEHWID);
        userName = name;
        passWord = pass;
        newHwid = hwid;
    }

    @Override
    public String toJson(){
        jsonObject.put("side","client");
        jsonObject.put("time", time);
        jsonObject.put("content", content);
        jsonObject.put("type", type.name());
        jsonObject.put("userName", userName);
        jsonObject.put("passWord", passWord);
        jsonObject.put("newHwid", newHwid);
        return jsonObject.toString();
    }
}
