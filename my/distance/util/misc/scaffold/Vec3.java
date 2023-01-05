package my.distance.util.misc.scaffold;

import net.minecraft.util.BlockPos;

public class Vec3 {
	private double x, y, z;

	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vec3(BlockPos pos){
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Vec3 addVector(double x, double y, double z) {
		return new Vec3(this.x + x, this.y + y, this.z + z);
	}

	public Vec3 floor() {
		return new Vec3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
	}

	public double squareDistanceTo(Vec3 v) {
		return Math.pow(v.x - this.x, 2) + Math.pow(v.y - this.y, 2) + Math.pow(v.z - this.z, 2);
	}
	public double distanceTo(Vec3 vec){
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		double var8 = d0 * d0 + d1 * d1 + d2 * d2;
		return Math.sqrt(var8);
	}

	public Vec3 add(Vec3 v) {
		return addVector(v.getX(), v.getY(), v.getZ());
	}

	public net.minecraft.util.Vec3 mc() {
		return new net.minecraft.util.Vec3(x, y, z);
	}

	@Override
	public String toString() {
		return "[" + x + ";" + y + ";" + z + "]";
	}
}
