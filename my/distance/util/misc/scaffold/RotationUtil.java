package my.distance.util.misc.scaffold;

import my.distance.util.entity.PlayerUtil;
import my.distance.util.math.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.util.Vec3;

import java.util.Random;

public class RotationUtil {

	private static Minecraft mc = Minecraft.getMinecraft();
	public static Random random = new Random();
	public static double x = random.nextDouble();
	public static double y = random.nextDouble();
	public static double z = random.nextDouble();

	public static float[] prevRotations = new float[2];

	public static boolean isFaced(final Entity targetEntity, double blockReachDistance) {
		return rayCastUtil.raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
	}

	public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random,
										   final boolean predict, final boolean throughWalls, final float distance) {
		if (outborder) {
			final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0),
					bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
			return new VecRotation(vec3, toRotation(vec3, predict));
		}

		final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8,
				bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
		final Rotation randomRotation = toRotation(randomVec, predict);

		final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

		VecRotation vecRotation = null;

		for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
			for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
				for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
							bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
					final Rotation rotation = toRotation(vec3, predict);
					final double vecDist = eyes.distanceTo(vec3);

					if (vecDist > distance)
						continue;

					if (throughWalls || isVisible(vec3)) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);

						if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(),
								randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation)
								: getRotationDifference(currentVec.getRotation()) < getRotationDifference(
								vecRotation.getRotation())))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}

    public static VecRotation faceBlock(final BlockPos blockPos) {
        if (blockPos == null)
            return null;

        VecRotation vecRotation = null;

        for(double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            for(double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                    final Vec3 posVec = new Vec3(blockPos).addVector(xSearch, ySearch, zSearch);
                    final double dist = eyesPos.distanceTo(posVec);

                    final double diffX = posVec.xCoord - eyesPos.xCoord;
                    final double diffY = posVec.yCoord - eyesPos.yCoord;
                    final double diffZ = posVec.zCoord - eyesPos.zCoord;

                    final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                    final Rotation rotation = new Rotation(
                            MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                            MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                    );

                    final Vec3 rotationVector = getVectorForRotation(rotation);
                    final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * dist, rotationVector.yCoord * dist,
                            rotationVector.zCoord * dist);
                    final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false,
                            false, true);

                    if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        final VecRotation currentVec = new VecRotation(posVec, rotation);

                        if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }
    
    public static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
	}

	private static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static double getRotationDifference(final Rotation rotation) {
		return prevRotations == null ? 0D
				: getRotationDifference(rotation,
				new Rotation(prevRotations[0], prevRotations[1]));
	}

	public static boolean isVisible(final Vec3 vec3) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX,
				mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		return mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null;
	}

	public static Vec3 getCenter(final AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5,
				bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static double getRotationDifference(final Entity entity) {
		final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

		return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation,
											final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
				currentRotation.getYaw()
						+ (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
				currentRotation.getPitch()
						+ (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
	}

	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX,
				mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict)
			eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.xCoord - eyesPos.xCoord;
		final double diffY = vec.yCoord - eyesPos.yCoord;
		final double diffZ = vec.zCoord - eyesPos.zCoord;

		return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
				MathHelper.wrapAngleTo180_float(
						(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
	}

	public static boolean isFacingEntity(EntityLivingBase entityLivingBase, float[] lastRotate) {
		float yaw = getNeededRotations(entityLivingBase)[0];
		float pitch = getNeededRotations(entityLivingBase)[1];
		float playerYaw = lastRotate[0];
		float playerPitch = lastRotate[1];
		float boudingBoxSize = 20F + entityLivingBase.getCollisionBorderSize();
		while (playerYaw < 0)
			playerYaw += 360;
		while (yaw < 0)
			yaw += 360;
		while (playerYaw > 360)
			playerYaw -= 360;
		while (yaw > 360)
			yaw -= 360;
		if (playerYaw >= (yaw - boudingBoxSize) && playerYaw <= (yaw + boudingBoxSize))
			return playerPitch >= (pitch - boudingBoxSize) && playerPitch <= (pitch + boudingBoxSize);
		return false;
	}

	public static float getAngleChange(EntityLivingBase entityIn) {
		float yaw = getNeededRotations(entityIn)[0];
		float pitch = getNeededRotations(entityIn)[1];
		float playerYaw = mc.thePlayer.rotationYaw;
		float playerPitch = mc.thePlayer.rotationPitch;
		if (playerYaw < 0)
			playerYaw += 360;
		if (playerPitch < 0)
			playerPitch += 360;
		if (yaw < 0)
			yaw += 360;
		if (pitch < 0)
			pitch += 360;

		float yawChange = Math.max(playerYaw, yaw) - Math.min(playerYaw, yaw);
		float pitchChange = Math.max(playerPitch, pitch) - Math.min(playerPitch, pitch);
		return yawChange + pitchChange;
	}

	public static float[] getNeededRotations(EntityLivingBase entityIn) {
		double d0 = entityIn.posX - mc.thePlayer.posX;
		double d1 = entityIn.posZ - mc.thePlayer.posZ;
		double d2 = entityIn.posY + entityIn.getEyeHeight() * 0.925
				- (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());

		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f = (float) (MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(MathHelper.atan2(d2, d3) * 180.0D / Math.PI));
		return new float[] { f, f1 };
	}

	public static float[] getNeededRotations(BlockPos BlockPosIn, float test) {
		double d0 = BlockPosIn.getX() + 0.5 - mc.thePlayer.posX;
		double d1 = BlockPosIn.getZ() + 0.5 - mc.thePlayer.posZ;
		double d2 = BlockPosIn.getY() + 0.5 - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());

		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f = (float) (MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - test;
		float f1 = (float) (-(MathHelper.atan2(d2, d3) * 180.0D / Math.PI));
		return new float[] { f, f1 };
	}

	public static float[] getRotations(EntityLivingBase entityIn, float speed, float[] lastRotate) {
		float yaw = updateRotation(lastRotate[0],
				getNeededRotations(entityIn)[0] + (PlayerUtil.isMoving() ? randomNumber(5.0f, -5.0f) : 0), speed);
		float pitch = updateRotation(lastRotate[1],
				getNeededRotations(entityIn)[1] + (PlayerUtil.isMoving() ? randomNumber(5.0f, -5.0f) : 0), speed);
		return new float[] { yaw, pitch };
	}

	public static float[] getRotations(BlockPos BlockInput, float speed, float[] lastRotate, float test) {
		float yaw = updateRotation(lastRotate[0], getNeededRotations(BlockInput, test)[0], speed);
		float pitch = updateRotation(lastRotate[1], getNeededRotations(BlockInput, test)[1], speed);
		return new float[] { yaw, pitch };
	}

	public static float randomNumber(float max, float min) {
		return (float) Math.random() * (max - min) + min;
	}

	public static float[] getRotationFromPosition(double x, double z, double y) {
		double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = y - Minecraft.getMinecraft().thePlayer.posY;

		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
		return new float[] { yaw, pitch };
	}

	private static float updateRotation(float currentRotation, float intendedRotation, float increment) {
		float f = MathHelper.wrapAngleTo180_float(intendedRotation - currentRotation);

		if (f > increment)
			f = increment;

		if (f < -increment)
			f = -increment;

		return currentRotation + f;
	}
}
