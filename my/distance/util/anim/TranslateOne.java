package my.distance.util.anim;

public class TranslateOne {
    private float anim;
    private long lastMS;

    public TranslateOne(float current) {
        this.anim = current;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(float target, float smoothing) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        this.lastMS = currentMS;
        int deltaX = (int)(Math.abs(target - this.anim) * smoothing);
        this.anim = AnimationUtil.calculateCompensation(target, this.anim, delta, deltaX);
    }

    public float getAnim() {
        return this.anim;
    }

    public void setAnim(float x) {
        this.anim = x;
    }
}
