package my.distance.module.modules.world.dis.disablers;

import LemonObfAnnotation.ObfuscationClass;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.*;
import my.distance.module.modules.world.dis.DisablerModule;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.optifine.util.LinkedList;

@ObfuscationClass
public class NewSpoofDisabler implements DisablerModule {
    private boolean shouldActive;
    private final LinkedList<C0FPacketConfirmTransaction> packetQueue = new LinkedList<>();
    private final LinkedList<C00PacketKeepAlive> anotherQueue = new LinkedList<>();
    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {
    }

    @Override
    public void onPacket(EventPacketReceive event) {
    }

    @Override
    public void onPacket(EventPacket event) {

    }

    @Override
    public void onUpdate(EventPreUpdate event) {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onWorldChange(EventWorldChanged event) {
    }

    @Override
    public void onRender2d(EventRender2D event) {
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate event) {
    }
}
