package my.distance.util.math.rotations;

public class Angle extends Vector2<Float> {
	 public static float b;
	    public static float c;
	    public static boolean a;
	    public static int e;
	    public static int d;
	public Angle(Float x, Float y) {
		super(x, y);
	}

	public Angle setYaw(Float yaw) {
		this.setX(yaw);
		return this;
	}

	public Angle setPitch(Float pitch) {
		this.setY(pitch);
		return this;
	}

	public Float getYaw() {
		return this.getX().floatValue();
	}

	public Float getPitch() {
		return this.getY().floatValue();
	}

	
	  public static double a2(double a, double b) {
	      return ((a - b) % 360.0D + 540.0D) % 360.0D - 180.0D;
	}
	
	  public static float getNewAngle(float angle) {
	        angle %= 360.0F;
	        if (angle >= 180.0F) {
	            angle -= 360.0F;
	        }
	        if (angle < -180.0F) {
	            angle += 360.0F;
	        }
	        return angle;
	    }
	  
	  public Angle constrantAngle() {
		this.setYaw(this.getYaw() % 360F);
		this.setPitch(this.getPitch() % 360F);

		while (this.getYaw() <= -380F) {
			this.setYaw(this.getYaw() + 360F);
		}

		while (this.getPitch() <= -380F) {
			this.setPitch(this.getPitch() + 360F);
		}

		while (this.getYaw() > 380F) {
			this.setYaw(this.getYaw() - 360F);
		}

		while (this.getPitch() > 380F) {
			this.setPitch(this.getPitch() - 360F);
		}

		return this;
	}
}
