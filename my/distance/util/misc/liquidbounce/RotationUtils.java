/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package my.distance.util.misc.liquidbounce;


import my.distance.api.EventBus;
import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventTick;
import my.distance.util.entity.RaycastUtils;
import my.distance.util.math.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import java.util.Objects;
import java.util.Random;

public final class RotationUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final Random random = new Random();

	private static int keepLength;

	public static Rotation targetRotation;
	public static Rotation serverRotation = new Rotation(0F, 0F);

	public static boolean keepCurrentRotation = false;

	private static double x = random.nextDouble();
	private static double y = random.nextDouble();
	private static double z = random.nextDouble();

	public RotationUtils(){
		EventBus.getInstance().register(this);
	}

	/**
	 * Allows you to check if your crosshair is over your target entity
	 *
	 * @param targetEntity your target entity
	 * @param blockReachDistance your reach
	 * @return if crosshair is over target
	 */
	public static boolean isFaced(final Entity targetEntity, double blockReachDistance) {
		return RaycastUtils.raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
	}

	/**
	 * Face block
	 *
	 * @param blockPos target block
	 */
	public static VecRotation faceBlock(final BlockPos blockPos) {
		if (blockPos == null)
			return null;

		VecRotation vecRotation = null;

		for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
			for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
				for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
					final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
					final Vec3 posVec = new Vec3(blockPos).addVector(xSearch, ySearch, zSearch);
					final double dist = eyesPos.distanceTo(posVec);

					final double diffX = posVec.getX() - eyesPos.getX();
					final double diffY = posVec.getY() - eyesPos.getY();
					final double diffZ = posVec.getZ() - eyesPos.getZ();

					final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

					final Rotation rotation = new Rotation(
							MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
							MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
					);

					final Vec3 rotationVector = getVectorForRotation(rotation);
					final Vec3 vector = eyesPos.addVector(rotationVector.getX() * dist, rotationVector.getY() * dist,
							rotationVector.getZ() * dist);
					final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(new net.minecraft.util.Vec3(eyesPos.getX(),eyesPos.getY(),eyesPos.getZ()), new net.minecraft.util.Vec3(vector.getX(),vector.getY(),vector.getZ()), false,
							false, true);

					if (obj != null && obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
						final VecRotation currentVec = new VecRotation(posVec, rotation);

						if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}

	/**
	 * Face target with bow
	 *
	 * @param target      your enemy
	 * @param silent      client side rotations
	 * @param predict     predict new enemy position
	 * @param predictSize predict size of predict
	 */
	public static void faceBow(final Entity target, final boolean silent, final boolean predict, final float predictSize) {
		final EntityPlayerSP player = mc.thePlayer;

		final double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0) - (player.posX + (predict ? (player.posX - player.prevPosX) : 0));
		final double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? (player.posY - player.posX) : 0)) - player.getEyeHeight();
		final double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0) - (player.posZ + (predict ? (player.posZ - player.prevPosZ) : 0));
		final double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

		float velocity = player.getItemInUseDuration() / 20F;
		velocity = (velocity * velocity + velocity * 2) / 3;

		if (velocity > 1) velocity = 1;

		final Rotation rotation = new Rotation(
				(float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90,
				(float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006F * (0.006F * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006F * posSqrt)))
		);

		if (silent)
			setTargetRotation(rotation);
		else
			limitAngleChange(new Rotation(player.rotationYaw, player.rotationPitch), rotation, 10 +
					new Random().nextInt(6)).toPlayer(mc.thePlayer);
	}

	/**
	 * Translate vec to rotation
	 *
	 * @param vec     target vec
	 * @param predict predict new location of your body
	 * @return rotation
	 */
	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
				mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		if (predict)
			eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

		final double diffX = vec.getX() - eyesPos.getX();
		final double diffY = vec.getY() - eyesPos.getY();
		final double diffZ = vec.getZ() - eyesPos.getZ();

		return new Rotation(MathHelper.wrapAngleTo180_float(
				(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
		), MathHelper.wrapAngleTo180_float(
				(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
		));
	}

	/**
	 * Get the center of a box
	 *
	 * @param bb your box
	 * @return center of box
	 */
	public static Vec3 getCenter(final AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX) - bb.minX * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}
	/**
	 * Search good center
	 *
	 * @param bb           enemy box
	 * @param outborder    outborder option
	 * @param random       random option
	 * @param predict      predict option
	 * @param throughWalls throughWalls option
	 * @return center
	 */
	public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random,
										   final boolean predict, final boolean throughWalls, final float distance) {
		if (outborder) {
			final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
			return new VecRotation(vec3, toRotation(vec3, predict));
		}

		final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8, bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
		final Rotation randomRotation = toRotation(randomVec, predict);

		final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

		VecRotation vecRotation = null;

		for(double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
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

						if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
							vecRotation = currentVec;
					}
				}
			}
		}

		return vecRotation;
	}

	public static VecRotation calculateCenter(final String calMode, final String randMode, final double randomRange, final AxisAlignedBB bb, final boolean predict, final boolean throughWalls) {

        /*if(outborder) {
            final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }*/

		//final Rotation randomRotation = toRotation(randomVec, predict);

		VecRotation vecRotation = null;

		double xMin = 0.0D;
		double yMin = 0.0D;
		double zMin = 0.0D;
		double xMax = 0.0D;
		double yMax = 0.0D;
		double zMax = 0.0D;
		double xDist = 0.0D;
		double yDist = 0.0D;
		double zDist = 0.0D;

		xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
		yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
		zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;

		Vec3 curVec3 = null;

		switch(calMode) {
			case "LiquidBounce":
				xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
				yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
				zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;
				break;
			case "Full":
				xMin = 0.00D; xMax = 1.00D; xDist = 0.1D;
				yMin = 0.00D; yMax = 1.00D; yDist = 0.1D;
				zMin = 0.00D; zMax = 1.00D; zDist = 0.1D;
				break;
			case "HalfUp":
				xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
				yMin = 0.50D; yMax = 0.90D; yDist = 0.1D;
				zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
				break;
			case "HalfDown":
				xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
				yMin = 0.10D; yMax = 0.50D; yDist = 0.1D;
				zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
				break;
			case "CenterSimple":
				xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
				yMin = 0.65D; yMax = 0.75D; yDist = 0.0125D;
				zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
				break;
			case "CenterLine":
				xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
				yMin = 0.10D; yMax = 0.90D; yDist = 0.1D;
				zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
				break;
		}

		for(double xSearch = xMin; xSearch < xMax; xSearch += xDist) {
			for (double ySearch = yMin; ySearch < yMax; ySearch += yDist) {
				for (double zSearch = zMin; zSearch < zMax; zSearch += zDist) {
					final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
					final Rotation rotation = toRotation(vec3, predict);

					if(throughWalls || isVisible(vec3)) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);

						if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
							vecRotation = currentVec;
							curVec3 = vec3;
						}
					}
				}
			}
		}

		if(vecRotation == null || Objects.equals(randMode, "Off"))
			return vecRotation;

		double rand1 = random.nextDouble();
		double rand2 = random.nextDouble();
		double rand3 = random.nextDouble();

		final double xRange = bb.maxX - bb.minX;
		final double yRange = bb.maxY - bb.minY;
		final double zRange = bb.maxZ - bb.minZ;
		double minRange = 999999.0D;

		if(xRange<=minRange) minRange = xRange;
		if(yRange<=minRange) minRange = yRange;
		if(zRange<=minRange) minRange = zRange;

		rand1 = rand1 * minRange * randomRange;
		rand2 = rand2 * minRange * randomRange;
		rand3 = rand3 * minRange * randomRange;

		final double xPrecent = minRange * randomRange / xRange;
		final double yPrecent = minRange * randomRange / yRange;
		final double zPrecent = minRange * randomRange / zRange;

		Vec3 randomVec3 = new Vec3(
				curVec3.getX() - xPrecent * (curVec3.getX() - bb.minX) + rand1,
				curVec3.getY() - yPrecent * (curVec3.getY() - bb.minY) + rand2,
				curVec3.getZ() - zPrecent * (curVec3.getZ() - bb.minZ) + rand3
		);
		switch(randMode) {
			case "Horizonal":
				randomVec3 = new Vec3(
						curVec3.getX() - xPrecent * (curVec3.getX() - bb.minX) + rand1,
						curVec3.getY(),
						curVec3.getZ() - zPrecent * (curVec3.getZ() - bb.minZ) + rand3
				);
				break;
			case "Vertical":
				randomVec3 = new Vec3(
						curVec3.getX(),
						curVec3.getY() - yPrecent * (curVec3.getY() - bb.minY) + rand2,
						curVec3.getZ()
				);
				break;
		}

		final Rotation randomRotation = toRotation(randomVec3, predict);
		vecRotation =  new VecRotation(randomVec3, randomRotation);

		return vecRotation;
	}

	// LnkuidBance
	public static VecRotation searchCenterLnk(final AxisAlignedBB bb,final boolean throughWalls, final float distance) {
		double ySearch;
		boolean entityonl = false;
		boolean entityonr = false;
		VecRotation vecRotation = null;
		if ((bb.maxX - bb.minX) < (bb.maxZ - bb.minZ)) {
			entityonr = true;
		}if ((bb.maxX - bb.minX) > (bb.maxZ - bb.minZ)) {
			entityonl = true;
		}if((bb.maxX - bb.minX) == (bb.maxZ - bb.minZ)) {
			entityonr = false;
			entityonl = false;
		}
		double  x = bb.minX + (bb.maxX - bb.minX) * (entityonl ? 0.25: 0.5);
		double  z = bb.minZ + (bb.maxZ - bb.minZ) * (entityonr ? 0.25: 0.5);
		for(double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.15D) {
			for (ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.15D) {
				for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.15D) {
					double pitch = bb.minY + (bb.maxY - bb.minY) * ySearch;
					Vec3 vec3;
					vec3 = new Vec3(x, pitch, z);
					final Rotation rotation = toRotation(vec3, false);
					final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

					final double vecDist = eyes.distanceTo(vec3);
					if (vecDist > distance)
						continue;

					if (vecDist <= (double)distance && (throughWalls || isVisible(vec3))) {
						final VecRotation currentVec = new VecRotation(vec3, rotation);
						if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
							vecRotation = currentVec;
						}
					}
				}
			}
		}
		return vecRotation;
	}



	/**
	 * Calculate difference between the client rotation and your entity
	 *
	 * @param entity your entity
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Entity entity) {
		final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

		return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
	}

	/**
	 * Calculate difference between the server rotation and your rotation
	 *
	 * @param rotation your rotation
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Rotation rotation) {
		return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
	}

	/**
	 * Calculate difference between two rotations
	 *
	 * @param a rotation
	 * @param b rotation
	 * @return difference between rotation
	 */
	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
	}

	/**
	 * Limit your rotation using a turn speed
	 *
	 * @param currentRotation your current rotation
	 * @param targetRotation your goal rotation
	 * @param turnSpeed your turn speed
	 * @return limited rotation
	 */
	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
				currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
				currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
				));
	}

	/**
	 * Calculate difference between two angle points
	 *
	 * @param a angle point
	 * @param b angle point
	 * @return difference between angle points
	 */
	private static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	/**
	 * Calculate rotation to vector
	 *
	 * @param rotation your rotation
	 * @return target vector
	 */
	public static Vec3 getVectorForRotation(final Rotation rotation) {
		float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
		float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
		float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
		return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
	}

	/**
	 * Allows you to check if your enemy is behind a wall
	 */
	public static boolean isVisible(final Vec3 vec3) {
		final net.minecraft.util.Vec3 eyesPos = new net.minecraft.util.Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		return mc.theWorld.rayTraceBlocks(eyesPos, new net.minecraft.util.Vec3(vec3.getX(),vec3.getY(),vec3.getZ())) == null;
	}

	/**
	 * Handle minecraft tick
	 *
	 * @param event Tick event
	 */
	@EventHandler
	public void onTick(final EventTick event) {
		if(targetRotation != null) {
			keepLength--;

			if (keepLength <= 0)
				reset();
		}

		if(random.nextGaussian() > 0.8D) x = Math.random();
		if(random.nextGaussian() > 0.8D) y = Math.random();
		if(random.nextGaussian() > 0.8D) z = Math.random();
	}

	/**
	 * Set your target rotation
	 *
	 * @param rotation your target rotation
	 */
	public static void setTargetRotation(final Rotation rotation, final int keepLength) {
		if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
				|| rotation.getPitch() > 90 || rotation.getPitch() < -90)
			return;
		rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
		targetRotation = rotation;
		RotationUtils.keepLength = keepLength;
	}

	/**
	 * Set your target rotation
	 *
	 * @param rotation your target rotation
	 */
	public static void setTargetRotation(final Rotation rotation) {
		setTargetRotation(rotation, 0);
	}

	/**
	 * Handle packet
	 *
	 * @param event Packet Event
	 */
	@EventHandler
	public void onPacket(final EventPacketSend event) {
		final Packet<?> packet = event.getPacket();
		if (packet instanceof C03PacketPlayer) {
			final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
			if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
				packetPlayer.yaw = targetRotation.getYaw();
				packetPlayer.pitch = targetRotation.getPitch();
				packetPlayer.rotating = true;
			}

			if (packetPlayer.rotating) {
				serverRotation = new Rotation(packetPlayer.getYaw(), packetPlayer.getPitch());
			}
		}
	}

	/**
	 * NCP Rotation
	 * @param a1 target
	 * @return rotation
	 */
	public static float[] rotateNCP(Entity a1) {
		//SkidSense Rotation
		if (a1 == null) {
			return null;
		} else {
			double v1 = a1.posX - Minecraft.getMinecraft().thePlayer.posX;
			double v3 = a1.posY + (double) a1.getEyeHeight() * 0.9D - (Minecraft.getMinecraft().thePlayer.posY + (double) Minecraft.getMinecraft().thePlayer.getEyeHeight());
			double v5 = a1.posZ - Minecraft.getMinecraft().thePlayer.posZ;
			double v7 = MathHelper.ceiling_float_int((float) (v1 * v1 + v5 * v5));
			float v9 = (float) (Math.atan2(v5, v1) * 180.0D / 3.141592653589793D) - 90.0F;
			float v10 = (float) (-(Math.atan2(v3, v7) * 180.0D / 3.141592653589793D));
			return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(v9 - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(v10 - Minecraft.getMinecraft().thePlayer.rotationPitch)};
		}
	}

	/**
	 * Reset your target rotation
	 */
	public static void reset() {
		keepLength = 0;
		targetRotation = null;
	}
}
