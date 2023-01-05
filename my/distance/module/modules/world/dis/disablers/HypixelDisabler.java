package my.distance.module.modules.world.dis.disablers;

import LemonObfAnnotation.ObfuscationClass;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.World.*;
import my.distance.module.modules.world.dis.DisablerModule;
import my.distance.util.math.RandomUtil;
import my.distance.util.time.TimeHelper;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@ObfuscationClass
public class HypixelDisabler implements DisablerModule {
    byte[] uuid = UUID.randomUUID().toString().getBytes();

    private final Queue<TimestampedPacket> queue = new ConcurrentLinkedDeque<>();
    private final LinkedList<Packet<?>> list = new LinkedList<>();

    private int bypassValue = 0;
    private long lastTransaction = 0L;

    private int lastUid;
    private boolean checkReset;
    private boolean active;

    private final TimeHelper tick = new TimeHelper();
    private final TimeHelper collecttimer = new TimeHelper();

    @Override
    public void onEnabled() {
        uuid = UUID.randomUUID().toString().getBytes();
        list.clear();
    }

    @Override
    public void onPacket(EventPacketSend event) {

    }

    @Override
    public void onPacket(EventPacketReceive event) {

    }

    @Override
    public void onPacket(EventPacket event) {
        base(event);
    }

    @Override
    public void onUpdate(EventPreUpdate event) {
        base(null);
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

    private void checkUidVaild(EventPacket event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            final C0FPacketConfirmTransaction C0F = (C0FPacketConfirmTransaction) event.getPacket();
            final int windowId = C0F.getWindowId();
            final int uid = C0F.getUid();
            if (windowId == 0 && uid < 0) {
                final int predictedUid = lastUid - 1;
                if (!checkReset) {
                    if (uid == predictedUid) {
                        if (!active) {
                            active = true;
                        }
                    } else {
                        active = false;
                    }
                } else {
                    if (uid != predictedUid) {
                        active = false;
                    }
                    checkReset = false;
                }
                lastUid = uid;
            }
        }
    }

    public void base(EventPacket event) {
        if (mc.isSingleplayer())
            return;

        if (event != null) {
            if (event.getPacket() instanceof S07PacketRespawn) {
                queue.clear();
                checkReset = true;
            }

            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook serverSidePosition = (S08PacketPlayerPosLook) event.getPacket();

                if (mc.currentScreen instanceof GuiDownloadTerrain)
                    mc.currentScreen = null;

                final float serverPitch = serverSidePosition.getPitch();
                final float serverYaw = serverSidePosition.getYaw();

                if (serverPitch == 0 && serverYaw == 0)
                    event.setCancelled(true);
            }

            //ping test bypass
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                lastTransaction = System.currentTimeMillis();
                checkUidVaild(event);
                if (active){
                    event.setCancelled(true);
                    queue.add(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()));
                }
            }

        } else {
            if (Math.abs(lastTransaction - System.currentTimeMillis()) <= 200L
                    && tick.isDelayComplete(10000)) {
                bypassValue = 300 +  RandomUtil.nextInt(40, 60);
                tick.reset();
            }

            if (collecttimer.isDelayComplete(bypassValue)) {
                collecttimer.reset();
                // ping spoof

                while (queue.size() > 1)
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(queue.poll().packet);
            }

        }
    }

    private static class TimestampedPacket {
        private final Packet<?> packet;
        private final long timestamp;

        public TimestampedPacket(final Packet<?> packet, final long timestamp) {
            this.packet = packet;
            this.timestamp = timestamp;
        }
    }
}
