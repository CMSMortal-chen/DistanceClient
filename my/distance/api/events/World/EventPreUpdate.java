package my.distance.api.events.World;

import my.distance.api.Event;

public class EventPreUpdate extends Event {
	private boolean isPre;
	public float yaw;
	public float pitch;
	public double y;
	private boolean ground;

	public EventPreUpdate(float yaw, float pitch, double y, boolean ground) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.y = y;
		this.ground = ground;
		this.isPre = true;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isOnground() {
		return this.ground;
	}

	public void setOnground(boolean ground) {
		this.ground = ground;
	}
	public boolean isPre() {
		return this.isPre;
	}
}
