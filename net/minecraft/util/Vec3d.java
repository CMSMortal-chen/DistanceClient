package net.minecraft.util;

public class Vec3d
{
    public final double xCoord;
    public final double yCoord;
    public final double zCoord;
    private static final String __OBFID = "CL_00000612";
    public static float sqrt(final float p_76129_0_) {
        return (float)Math.sqrt(p_76129_0_);
    }

    public static float sqrt(final double p_76133_0_) {
        return (float)Math.sqrt(p_76133_0_);
    }
    public Vec3d(double x, double y, double z) {
        super();
        if (x == -0.0) {
            x = 0.0;
        }
        if (y == -0.0) {
            y = 0.0;
        }
        if (z == -0.0) {
            z = 0.0;
        }
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3d(final Vec3i vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vec3d scale(final double p_186678_1_) {
        return new Vec3d(this.xCoord * p_186678_1_, this.yCoord * p_186678_1_, this.zCoord * p_186678_1_);
    }

    public Vec3d subtractReverse(final Vec3d vec) {
        return new Vec3d(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }

    public Vec3d normalize() {
        final double var1 = sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return (var1 < 1.0E-4) ? new Vec3d(0.0, 0.0, 0.0) : new Vec3d(this.xCoord / var1, this.yCoord / var1, this.zCoord / var1);
    }

    public double dotProduct(final Vec3d vec) {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }

    public Vec3d crossProduct(final Vec3d vec) {
        return new Vec3d(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
    }

    public Vec3d subtract(final Vec3 vec3) {
        return this.subtract(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public Vec3d subtract(final double p_178786_1_, final double p_178786_3_, final double p_178786_5_) {
        return this.addVector(-p_178786_1_, -p_178786_3_, -p_178786_5_);
    }

    public Vec3d add(final Vec3d p_178787_1_) {
        return this.addVector(p_178787_1_.xCoord, p_178787_1_.yCoord, p_178787_1_.zCoord);
    }

    public Vec3d addVector(final double x, final double y, final double z) {
        return new Vec3d(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public double distanceTo(final Vec3 vec3) {
        final double var2 = vec3.xCoord - this.xCoord;
        final double var3 = vec3.yCoord - this.yCoord;
        final double var4 = vec3.zCoord - this.zCoord;
        return sqrt(var2 * var2 + var3 * var3 + var4 * var4);
    }

    public double squareDistanceTo(final Vec3d vec) {
        final double var2 = vec.xCoord - this.xCoord;
        final double var3 = vec.yCoord - this.yCoord;
        final double var4 = vec.zCoord - this.zCoord;
        return var2 * var2 + var3 * var3 + var4 * var4;
    }

    public double lengthVector() {
        return sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    public Vec3d getIntermediateWithXValue(final Vec3d vec, final double x) {
        final double var4 = vec.xCoord - this.xCoord;
        final double var5 = vec.yCoord - this.yCoord;
        final double var6 = vec.zCoord - this.zCoord;
        if (var4 * var4 < 1.0000000116860974E-7) {
            return null;
        }
        final double var7 = (x - this.xCoord) / var4;
        return (var7 >= 0.0 && var7 <= 1.0) ? new Vec3d(this.xCoord + var4 * var7, this.yCoord + var5 * var7, this.zCoord + var6 * var7) : null;
    }

    public Vec3d getIntermediateWithYValue(final Vec3d vec, final double y) {
        final double var4 = vec.xCoord - this.xCoord;
        final double var5 = vec.yCoord - this.yCoord;
        final double var6 = vec.zCoord - this.zCoord;
        if (var5 * var5 < 1.0000000116860974E-7) {
            return null;
        }
        final double var7 = (y - this.yCoord) / var5;
        return (var7 >= 0.0 && var7 <= 1.0) ? new Vec3d(this.xCoord + var4 * var7, this.yCoord + var5 * var7, this.zCoord + var6 * var7) : null;
    }

    public Vec3d getIntermediateWithZValue(final Vec3d vec, final double z) {
        final double var4 = vec.xCoord - this.xCoord;
        final double var5 = vec.yCoord - this.yCoord;
        final double var6 = vec.zCoord - this.zCoord;
        if (var6 * var6 < 1.0000000116860974E-7) {
            return null;
        }
        final double var7 = (z - this.zCoord) / var6;
        return (var7 >= 0.0 && var7 <= 1.0) ? new Vec3d(this.xCoord + var4 * var7, this.yCoord + var5 * var7, this.zCoord + var6 * var7) : null;
    }

    @Override
    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public Vec3d rotatePitch(final float p_178789_1_) {
        final float var2 = MathHelper.cos(p_178789_1_);
        final float var3 = MathHelper.sin(p_178789_1_);
        final double var4 = this.xCoord;
        final double var5 = this.yCoord * var2 + this.zCoord * var3;
        final double var6 = this.zCoord * var2 - this.yCoord * var3;
        return new Vec3d(var4, var5, var6);
    }

    public Vec3d rotateYaw(final float p_178785_1_) {
        final float var2 = MathHelper.cos(p_178785_1_);
        final float var3 = MathHelper.sin(p_178785_1_);
        final double var4 = this.xCoord * var2 + this.zCoord * var3;
        final double var5 = this.yCoord;
        final double var6 = this.zCoord * var2 - this.xCoord * var3;
        return new Vec3d(var4, var5, var6);
    }
}
