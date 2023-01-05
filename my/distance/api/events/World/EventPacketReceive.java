package my.distance.api.events.World;

import my.distance.api.Event;
import net.minecraft.network.Packet;

public class EventPacketReceive extends Event {
	public Packet<?> packet;

	public EventPacketReceive(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return this.packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}
}
