package my.distance.util.anim;

import static my.distance.util.render.RenderUtil.delta;

public class AnimationUtil {
   private static final float defaultSpeed = 0.125f;
   public static float mvoeUD(float current, float end, float minSpeed) {
      return moveUD(current, end, defaultSpeed, minSpeed);
   }
   	public static float getAnimationState(float animation, float finalState, float speed) {
      final float add = (float) (delta * (speed / 1000f));
      if (animation < finalState) {
         if (animation + add < finalState) {
            animation += add;
         } else {
            animation = finalState;
         }
      } else if (animation - add > finalState) {
         animation -= add;
      } else {
         animation = finalState;
      }
      return animation;
   }
   public static double mvoeUD(double current, double end, float minSpeed) {
      return moveUD(current, end, defaultSpeed, minSpeed);
   }
   public static float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
      boolean larger = end > current;
      if (smoothSpeed < 0.0f) {
         smoothSpeed = 0.0f;
      } else if (smoothSpeed > 1.0f) {
         smoothSpeed = 1.0f;
      }
      if (minSpeed < 0.0f) {
         minSpeed = 0.0f;
      } else if (minSpeed > 1.0f) {
         minSpeed = 1.0f;
      }
      float movement = (end - current) * smoothSpeed;
      if (movement > 0) {
         movement = Math.max(minSpeed, movement);
         movement = Math.min(end - current, movement);
      } else if (movement < 0) {
         movement = Math.min(-minSpeed, movement);
         movement = Math.max(end - current, movement);
      }
      if (larger){
         if (end <= current + movement){
            return end;
         }
      }else {
         if (end >= current + movement){
            return end;
         }
      }
      return current + movement;
   }
   public static double moveUD(double current, double end, float smoothSpeed, float minSpeed) {
      boolean larger = end > current;
      if (smoothSpeed < 0.0f) {
         smoothSpeed = 0.0f;
      } else if (smoothSpeed > 1.0f) {
         smoothSpeed = 1.0f;
      }
      if (minSpeed < 0.0f) {
         minSpeed = 0.0f;
      } else if (minSpeed > 1.0f) {
         minSpeed = 1.0f;
      }
      double movement = (end - current) * smoothSpeed;
      if (movement > 0) {
         movement = Math.max(minSpeed, movement);
         movement = Math.min(end - current, movement);
      } else if (movement < 0) {
         movement = Math.min(-minSpeed, movement);
         movement = Math.max(end - current, movement);
      }
      if (larger){
         if (end <= current + movement){
            return end;
         }
      }else {
         if (end >= current + movement){
            return end;
         }
      }
      return current + movement;
   }
   public static float calculateCompensation(float target, float current, long delta, int speed) {
      float diff = current - target;
      if (delta < 1L) {
         delta = 1L;
      }

      double xD;
      if (diff > (float)speed) {
         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
         current = (float)((double)current - xD);
         if (current < target) {
            current = target;
         }
      } else if (diff < (float)(-speed)) {
         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
         current = (float)((double)current + xD);
         if (current > target) {
            current = target;
         }
      } else {
         current = target;
      }

      return current;
   }
}
