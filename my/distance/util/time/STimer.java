package my.distance.util.time;

public class STimer {
    private long previousTime;

    public STimer() {
        previousTime = -1L;
    }

    public boolean check(float milliseconds) {
        return getTime() >= milliseconds;
    }

    public long getTime() {
        return getCurrentTime() - previousTime;
    }

    public void reset() {
        previousTime = getCurrentTime();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}

