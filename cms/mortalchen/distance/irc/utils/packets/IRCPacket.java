package cms.mortalchen.distance.irc.utils.packets;

import org.json.JSONObject;

public class IRCPacket {
    public long time = 0;
    public String content = "";
    public IRCType type;
    public int key = 0;//以后实现
    protected final JSONObject jsonObject = new JSONObject();

    public IRCPacket(long time, String content, IRCType type) {
        this.time = time;
        this.content = content;
        this.type = type;
    }

    public String toJson() {
        return "";
    }
}
