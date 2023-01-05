package my.distance.api.events.World;

import my.distance.api.Event;

public class EventNoSlow extends Event {
    float moveStrafe;
    float moveForward;
    public EventNoSlow(float moveStrafe, float moveForward){
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }

    public float getMoveForward() {
        return moveForward;
    }

    public float getMoveStrafe() {
        return moveStrafe;
    }
}
