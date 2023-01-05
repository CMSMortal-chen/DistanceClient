
package my.distance.api.events.World;

import my.distance.api.Event;

public final class EventStrafe
extends Event {
    private final float strafe;
    private final float forward;
    private final float friction;

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public EventStrafe(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
}
