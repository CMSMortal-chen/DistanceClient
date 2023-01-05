package my.distance.util.math.rotations;

public class AngleUtilitys {
    private float minYawSmoothing, maxYawSmoothing, minPitchSmoothing, maxPitchSmoothing;
    private Vector3<Float> delta;
    private Angle smoothedAngle;
    private float height = Mafs.getRandomInRange(1.1F, 1.8F);

    public AngleUtilitys(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
        delta = new Vector3<>(0F, 0F, 0F);
        smoothedAngle = new Angle(0F, 0F);
    }


    public Angle calculateAngle(Vector3<Double> destination, Vector3<Double> source) {
        Angle angles = new Angle(0F, 0F);
        delta.setX(destination.getX().floatValue() - source.getX().floatValue())
        .setY((destination.getY().floatValue() + height) - (source.getY().floatValue() + height))
        .setZ(destination.getZ().floatValue() - source.getZ().floatValue());
        double hypotenuse = Math.hypot(delta.getX().doubleValue(), delta.getZ().doubleValue());
        float yawAtan = ((float) Math.atan2(delta.getZ().floatValue(), delta.getX().floatValue()));
        float pitchAtan = ((float) Math.atan2(delta.getY().floatValue(), hypotenuse));
        float deg = ((float) (180 / Math.PI));
        float yaw = ((yawAtan * deg) - 90F);
        float pitch = -(pitchAtan * deg);
        return angles.setYaw(yaw).setPitch(pitch);
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Angle smoothAngle(Angle destination, Angle source) {
    	return smoothedAngle
                   .setYaw(source.getYaw() - destination.getYaw())
                   .setPitch((source.getPitch() - destination.getPitch()))
                   .constrantAngle()
                   .setYaw(source.getYaw() - smoothedAngle.getYaw() )
                   .constrantAngle()
                   .setPitch(source.getPitch() - smoothedAngle.getPitch())
                   .constrantAngle();
       }
}
