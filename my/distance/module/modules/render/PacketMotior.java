package my.distance.module.modules.render;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventPostUpdate;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.misc.Helper;
import my.distance.util.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketMotior extends Module {
    private int packetcount;
    private TimerUtil time = new TimerUtil();

    public PacketMotior() {
        super("PacketMotior", new String[]{"rotate"}, ModuleType.Render);
        this.setColor((new Color(17, 250, 154)).getRGB());
    }

    @EventHandler
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            if (packetcount > 22) {
                e.setCancelled(true);
            }
            ++this.packetcount;
        }
    }

    @EventHandler
    public void OnUpdate(EventPostUpdate event) {
        if (this.time.hasReached(1000.0D)) {
            super.setSuffix("PPS:" + this.packetcount);
            if (this.packetcount > 22) {
                Helper.sendMessage("C03PacketPlayer发送数量过多！ (" + this.packetcount + "/22)");
            }
            this.packetcount = 0;
            this.time.reset();
        }
    }
}
