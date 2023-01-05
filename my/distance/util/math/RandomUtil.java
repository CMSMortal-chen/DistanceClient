package my.distance.util.math;

import java.util.Random;

public class RandomUtil {
    public static String randomString(final int length) {
        return ">"+(random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"))+"<";
    }
    public static String random(final int length, final String chars) {
        return random(length, chars.toCharArray());
    }

    public static String random(final int length, final char[] chars) {
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++)
            stringBuilder.append(chars[new Random().nextInt(chars.length)]);
        return stringBuilder.toString();
    }

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }

    public static float randFloat(float min, float max) {
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        if (scaled > max) {
            scaled = max;
        }
        float shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static boolean nextBoolean() {
        return new Random().nextInt(100) >= 50;
    }
}
