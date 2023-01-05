package my.distance.module.modules.world.dis;

import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.*;
import net.minecraft.client.Minecraft;

public interface DisablerModule {
    Minecraft mc = Minecraft.getMinecraft();
    void onDisable();
    void onEnabled();

    void onPacket(EventPacketSend event);

    void onPacket(EventPacketReceive event);

    void onPacket(EventPacket event);

    void onUpdate(EventPreUpdate event);

    void onWorldChange(EventWorldChanged event);
    void onRender2d(EventRender2D event);
    void onMotionUpdate(EventMotionUpdate event);
}
