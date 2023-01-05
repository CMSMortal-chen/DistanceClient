package my.distance.util.anim;

public final class AnimationUtils {
    public static double animate(double target, double current, double speed) {
        boolean larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        if (dif < 0.1){
            return target;
        }
        double factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1;
        }
        if (larger){
            if (current + factor>target){
                current = target;
            }else {
                current += factor;
            }
        }else {
            if (current - factor<target) {
                current = target;
            }else {
                current -= factor;
            }
        }
        return current;
    }
}

