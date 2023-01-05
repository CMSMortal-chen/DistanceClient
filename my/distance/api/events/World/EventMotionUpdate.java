package my.distance.api.events.World;


import my.distance.api.Event;

public final class EventMotionUpdate extends Event {
    private double posX;
    private double lastPosX;
    private double posY;
    private double lastPosY;
    private double posZ;
    private double lastPosZ;
    private float yaw;
    private float lastYaw;
    private float pitch;
    private float lastPitch;
    private boolean onGround;
    private Type type;
    private EventPreUpdate event;

    public EventMotionUpdate(EventPreUpdate eventPreUpdate,double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, Type type) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.type = type;
        event = eventPreUpdate;
    }

    public boolean isPre() {
        return this.type == Type.PRE;
    }

    public double getPosX() {
        return this.posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getLastPosX() {
        return this.lastPosX;
    }

    public void setLastPosX(double lastPosX) {
        this.lastPosX = lastPosX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getLastPosY() {
        return this.lastPosY;
    }

    public void setLastPosY(double lastPosY) {
        this.lastPosY = lastPosY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public double getLastPosZ() {
        return this.lastPosZ;
    }

    public void setLastPosZ(double lastPosZ) {
        this.lastPosZ = lastPosZ;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        event.setYaw(this.yaw = yaw);
    }

    public float getLastYaw() {
        return this.lastYaw;
    }

    public void setLastYaw(float lastYaw) {
        this.lastYaw = lastYaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        event.setPitch(this.pitch = pitch);
    }

    public float getLastPitch() {
        return this.lastPitch;
    }

    public void setLastPitch(float lastPitch) {
        this.lastPitch = lastPitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Type getTypes() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        PRE,
        POST
    }
}

