package my.distance.api.events.World;

import my.distance.api.Event;

public class EventMotion extends Event {
    private Type motionType;

    public EventMotion(Type type){
        motionType = type;
    }

    public void setTypes(Type motionType) {
        this.motionType = motionType;
    }

    public Type getTypes() {
        return motionType;
    }

    public boolean isPre(){
        return motionType == Type.PRE;
    }

    public enum Type {
        PRE,
        POST
    }
}
