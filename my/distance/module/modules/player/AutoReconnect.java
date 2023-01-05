package my.distance.module.modules.player;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventChat;
import my.distance.module.Module;
import my.distance.module.ModuleType;

public class AutoReconnect
extends Module {
    private float old;

    public AutoReconnect() {
        super("AutoReconnect", new String[]{"AutoReconnect", "AutoReconnect", "AutoReconnect"}, ModuleType.Player);
        this.setColor(new Color(244, 255, 149).getRGB());
    }

    @EventHandler
    private void onChat(EventChat e) {
        if(e.getMessage().contains("Flying or related."))mc.thePlayer.sendChatMessage("/back");
    }

}

