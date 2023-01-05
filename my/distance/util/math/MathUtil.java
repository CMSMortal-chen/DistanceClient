package my.distance.util.math;

import my.distance.util.misc.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {
	public static Random random = new Random();

	public static double toDecimalLength(double in, int places) {
		return Double.parseDouble(String.format("%." + places + "f", in));
	}

	public static float toDegree(double x, double z) {
		return (float)(Math.atan2(z - (Minecraft.getMinecraft()).thePlayer.posZ, x - (Minecraft.getMinecraft()).thePlayer.posX) * 180.0D / Math.PI) - 90.0F;
	}

	public static double distTo(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}


	public static double getRandomInRange(double min, double max) {
		Random random = new Random();
		double range = max - min;
		double scaled = random.nextDouble() * range;
		if (scaled > max) {
			scaled = max;
		}
		double shifted = scaled + min;

		if (shifted > max) {
			shifted = max;
		}
		return shifted;
	}
	public static double round(final double value, final int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double round(double num, double increment) {
		if (increment < 0.0D) {
			throw new IllegalArgumentException();
		} else {
			BigDecimal bd = new BigDecimal(num);
			bd = bd.setScale((int)increment, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}


	public static boolean parsable(String s, byte type) {
		try {
			switch (type) {
				case 0: {
					Short.parseShort(s);
					break;
				}
				case 1: {
					Byte.parseByte(s);
					break;
				}
				case 2: {
					Integer.parseInt(s);
					break;
				}
				case 3: {
					Float.parseFloat(s);
					break;
				}
				case 4: {
					Double.parseDouble(s);
					break;
				}
				case 5: {
					Long.parseLong(s);
				}
				default: {
					break;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static double square(double in) {
		return in * in;
	}

	public static double randomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}
	public static double randomDouble2(double min, double max) {
		return MathHelper.clamp_double(min + random.nextDouble() * max, min, max);
	}
	public static float randomFloat(final float min, final float max) {
		return min + MathUtil.random.nextFloat() * (max - min);
	}
	public static double getBaseMovementSpeed() {
		double baseSpeed = 0.2873;
		if (Helper.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Helper.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
		}
		return baseSpeed;
	}

	public static double getHighestOffset(double max) {
		double i = 0.0;
		while (i < max) {
			int[] arrn = new int[5];
			arrn[0] = -2;
			arrn[1] = -1;
			arrn[3] = 1;
			arrn[4] = 2;
			int n = arrn.length;
			int n2 = 0;
			while (n2 < n) {
				int offset = arrn[n2];
				if (Helper.mc.theWorld.getCollidingBoundingBoxes(Helper.mc.thePlayer,
						Helper.mc.thePlayer.getEntityBoundingBox().offset(Helper.mc.thePlayer.motionX * (double) offset,
								i, Helper.mc.thePlayer.motionZ * (double) offset))
						.size() > 0) {
					return i - 0.01;
				}
				++n2;
			}
			i += 0.01;
		}
		return max;
	}
    public static double clamp(double value, double minimum, double maximum) {
        return value > maximum ? maximum : Math.max(value, minimum);
    }
	public static class NumberType {
		public static final byte SHORT = 0;
		public static final byte BYTE = 1;
		public static final byte INT = 2;
		public static final byte FLOAT = 3;
		public static final byte DOUBLE = 4;
		public static final byte LONG = 5;

		public static byte getByType(Class cls) {
			if (cls == Short.class) {
				return 0;
			}
			if (cls == Byte.class) {
				return 1;
			}
			if (cls == Integer.class) {
				return 2;
			}
			if (cls == Float.class) {
				return 3;
			}
			if (cls == Double.class) {
				return 4;
			}
			if (cls == Long.class) {
				return 5;
			}
			return -1;
		}
	}
	public static float[] constrainAngle(float[] vector) {
		vector[0] %= 360.0F;

		for(vector[1] %= 360.0F; vector[0] <= -180.0F; vector[0] += 360.0F) {
		}

		while(vector[1] <= -180.0F) {
			vector[1] += 360.0F;
		}

		while(vector[0] > 180.0F) {
			vector[0] -= 360.0F;
		}

		while(vector[1] > 180.0F) {
			vector[1] -= 360.0F;
		}

		return vector;
	}
	public static int randomNumber(int max, int min) { return -min + (int)(Math.random() * (max - -min + 1)); }
}
