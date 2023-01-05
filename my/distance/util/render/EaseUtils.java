package my.distance.util.render;


public final class EaseUtils {

    public static double easeInSine(double x) {
        double d = x * Math.PI / (double) 2;
        double d2 = 1.0;
        double d3 = Math.cos(d);
        return d2 - d3;
    }

    public static double easeOutSine(double x) {
        double d = x * Math.PI / (double) 2;
        return Math.sin(d);
    }

    public static double easeInOutSine(double x) {
        double d = Math.PI * x;
        return -(Math.cos(d) - 1.0) / (double) 2;
    }

    public static double easeInQuad(double x) {
        return x * x;
    }

    public static double easeOutQuad(double x) {
        return 1.0 - (1.0 - x) * (1.0 - x);
    }

    public static double easeInOutQuad(double x) {
        double d;
        if (x < 0.5) {
            d = (double) 2 * x * x;
        } else {
            double d2 = (double) -2 * x + (double) 2;
            int n = 2;
            double d3 = 1.0;
            double d4 = Math.pow(d2, n);
            d = d3 - d4 / (double) 2;
        }
        return d;
    }

    public static double easeInCubic(double x) {
        return x * x * x;
    }

    public static double easeOutCubic(double x) {
        double d = 1.0 - x;
        int n = 3;
        double d2 = 1.0;
        double d3 = Math.pow(d, n);
        return d2 - d3;
    }

    public static double easeInOutCubic(double x) {
        double d;
        if (x < 0.5) {
            d = (double) 4 * x * x * x;
        } else {
            double d2 = (double) -2 * x + (double) 2;
            int n = 3;
            double d3 = 1.0;
            double d4 = Math.pow(d2, n);
            d = d3 - d4 / (double) 2;
        }
        return d;
    }

    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    public static double easeOutQuart(double x) {
        double d = 1.0 - x;
        int n = 4;
        double d2 = 1.0;
        double d3 = Math.pow(d, n);
        return d2 - d3;
    }

    public static double easeInOutQuart(double x) {
        double d;
        if (x < 0.5) {
            d = (double) 8 * x * x * x * x;
        } else {
            double d2 = (double) -2 * x + (double) 2;
            int n = 4;
            double d3 = 1.0;
            double d4 = Math.pow(d2, n);
            d = d3 - d4 / (double) 2;
        }
        return d;
    }

    public static double easeInQuint(double x) {
        return x * x * x * x * x;
    }

    public static double easeOutQuint(double x) {
        double d = 1.0 - x;
        int n = 5;
        double d2 = 1.0;
        double d3 = Math.pow(d, n);
        return d2 - d3;
    }

    public static double easeInOutQuint(double x) {
        double d;
        if (x < 0.5) {
            d = (double) 16 * x * x * x * x * x;
        } else {
            double d2 = (double) -2 * x + (double) 2;
            int n = 5;
            double d3 = 1.0;
            double d4 = Math.pow(d2, n);
            d = d3 - d4 / (double) 2;
        }
        return d;
    }

    public static double easeInExpo(double x) {
        double d;
        if (x == 0.0) {
            d = 0.0;
        } else {
            double d2 = 2.0;
            double d3 = (double) 10 * x - (double) 10;
            d = Math.pow(d2, d3);
        }
        return d;
    }

    public static double easeOutExpo(double x) {
        double d;
        if (x == 1.0) {
            d = 1.0;
        } else {
            double d2 = 2.0;
            double d3 = (double) -10 * x;
            double d4 = 1.0;
            double d5 = Math.pow(d2, d3);
            d = d4 - d5;
        }
        return d;
    }

    public static double easeInOutExpo(double x) {
        double d;
        if (x == 0.0) {
            d = 0.0;
        } else if (x == 1.0) {
            d = 1.0;
        } else if (x < 0.5) {
            double d2 = 2.0;
            double d3 = (double) 20 * x - (double) 10;
            d = Math.pow(d2, d3) / (double) 2;
        } else {
            double d4 = 2.0;
            double d5 = (double) -20 * x + (double) 10;
            double d6 = 2;
            double d7 = Math.pow(d4, d5);
            d = (d6 - d7) / (double) 2;
        }
        return d;
    }

    public static double easeInCirc(double x) {
        double d = x;
        int n = 2;
        double d2 = 1.0;
        double d3 = 1.0;
        double d4 = Math.pow(d, n);
        d = d2 - d4;
        d2 = Math.sqrt(d);
        return d3 - d2;
    }

    public static double easeOutCirc(double x) {
        double d = x - 1.0;
        int n = 2;
        double d2 = 1.0;
        double d3 = Math.pow(d, n);
        d = d2 - d3;
        return Math.sqrt(d);
    }

    public static double easeInOutCirc(double x) {
        double d;
        if (x < 0.5) {
            double d2 = (double) 2 * x;
            int n = 2;
            double d3 = 1.0;
            double d4 = 1.0;
            double d5 = Math.pow(d2, n);
            d2 = d3 - d5;
            d3 = Math.sqrt(d2);
            d = (d4 - d3) / (double) 2;
        } else {
            double d6 = (double) -2 * x + (double) 2;
            int n = 2;
            double d7 = 1.0;
            double d8 = Math.pow(d6, n);
            d6 = d7 - d8;
            d = (Math.sqrt(d6) + 1.0) / (double) 2;
        }
        return d;
    }

    public static double easeInBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return c3 * x * x * x - c1 * x * x;
    }

    public static double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        double d = x - 1.0;
        int n = 3;
        double d2 = c3;
        double d3 = 1.0;
        double d4 = Math.pow(d, n);
        double d5 = d3 + d2 * d4;
        d = x - 1.0;
        n = 2;
        d2 = c1;
        d3 = d5;
        d4 = Math.pow(d, n);
        return d3 + d2 * d4;
    }

    public static double easeInOutBack(double x) {
        double d;
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        if (x < 0.5) {
            double d2 = (double) 2 * x;
            int n = 2;
            d = Math.pow(d2, n) * ((c2 + 1.0) * (double) 2 * x - c2) / (double) 2;
        } else {
            double d3 = (double) 2 * x - (double) 2;
            int n = 2;
            d = (Math.pow(d3, n) * ((c2 + 1.0) * (x * (double) 2 - (double) 2) + c2) + (double) 2) / (double) 2;
        }
        return d;
    }

    public static double easeInElastic(double x) {
        double d;
        double c4 = 2.0943951023931953;
        if (x == 0.0) {
            d = 0.0;
        } else if (x == 1.0) {
            d = 1.0;
        } else {
            double d2 = -2.0;
            double d3 = (double) 10 * x - (double) 10;
            double d4 = Math.pow(d2, d3);
            d2 = (x * (double) 10 - 10.75) * c4;
            double d6 = Math.sin(d2);
            d = d4 * d6;
        }
        return d;
    }

    public static double easeOutElastic(double x) {
        double d;
        double c4 = 2.0943951023931953;
        if (x == 0.0) {
            d = 0.0;
        } else if (x == 1.0) {
            d = 1.0;
        } else {
            double d2 = 2.0;
            double d3 = (double) -10 * x;
            double d4 = Math.pow(d2, d3);
            d2 = (x * (double) 10 - 0.75) * c4;
            double d6 = Math.sin(d2);
            d = d4 * d6 + 1.0;
        }
        return d;
    }

    public static double easeInOutElastic(double x) {
        double d;
        double c5 = 1.3962634015954636;
        if (x == 0.0) {
            d = 0.0;
        } else if (x == 1.0) {
            d = 1.0;
        } else if (x < 0.5) {
            double d2 = 2.0;
            double d3 = (double) 20 * x - (double) 10;
            double d4 = Math.pow(d2, d3);
            d2 = ((double) 20 * x - 11.125) * c5;
            double d6 = Math.sin(d2);
            d = -(d4 * d6) / (double) 2;
        } else {
            double d7 = 2.0;
            double d8 = (double) -20 * x + (double) 10;
            double d9 = Math.pow(d7, d8);
            d7 = ((double) 20 * x - 11.125) * c5;
            double d11 = Math.sin(d7);
            d = d9 * d11 / (double) 2 + 1.0;
        }
        return d;
    }

    public static double easeInBounce(double x) {
        return 1.0 - EaseUtils.easeOutBounce(1.0 - x);
    }

    public static double easeOutBounce(double animeX) {
        double x = animeX;
        double n1 = 7.5625;
        double d1 = 2.75;
        if (x < 1.0 / d1) {
            return n1 * x * x;
        }
        if (x < (double) 2 / d1) {
            return n1 * ((x -= 1.5) / d1) * x + 0.75;
        }
        if (x < 2.5 / d1) {
            return n1 * ((x -= 2.25) / d1) * x + 0.9375;
        }
        return n1 * ((x -= 2.625) / d1) * x + 0.984375;
    }

    public static double easeInOutBounce(double x) {
        return x < 0.5 ? (1.0 - EaseUtils.easeOutBounce(1.0 - (double) 2 * x)) / (double) 2 : (1.0 + EaseUtils.easeOutBounce((double) 2 * x - 1.0)) / (double) 2;
    }
}
