package my.distance.util.time;

public class Timer {

    private long prevMS;

    public Timer() {
        this.prevMS = getTime();
    }

    public boolean delay(float milliSec) {
        return (float) (getTime() - this.prevMS) >= milliSec;
    }

    public void reset() {
        this.prevMS = getTime();
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public long getDifference() {
        return getTime() - this.prevMS;
    }

    public void setDifference(long difference) {
        this.prevMS = (getTime() - difference);
    }

}
