package my.distance.util.time;

public class StopWatchs {
    private long ms = this.getCurrentMS();

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public final long getElapsedTime() {
        return this.getCurrentMS() - this.ms;
    }

    public final boolean elapsed(long milliseconds) {
        return this.getCurrentMS() - this.ms > milliseconds;
    }

    public final void reset() {
        this.ms = this.getCurrentMS();
    }
}
