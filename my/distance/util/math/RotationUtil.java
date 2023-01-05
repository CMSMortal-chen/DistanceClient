package my.distance.util.math;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventTick;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Random;

import my.distance.util.misc.Helper;
import my.distance.util.misc.Location;

public class RotationUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    public static Rotation serverRotation = new Rotation(0F, 0F);
    public static Rotation targetRotation;
    private static int keepLength;


    public static float a(double a1, double a2) {
        double v1 = a1 - Minecraft.getMinecraft().thePlayer.posX;
        double v3 = a2 - Minecraft.getMinecraft().thePlayer.posZ;
        double v5 = MathHelper.ceiling_double_int((v1 * v1 + v3 * v3));
        return (float)(Math.atan2(v3, v1) * 180.0 / 3.141592653589793) - 90.0f;
    }
    public static float[] getPredictedRotations(EntityLivingBase ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        double y = ent.posY + (double)(ent.getEyeHeight() / 2.0F);
        return getRotationFromPosition(x, z, y);
    }
    public static void setTargetRotation(final Rotation rotation) {
        setTargetRotation(rotation, 0);
    }
    public static float getYawDifference(final float current, final float target) {
        final float rot = (target + 180.0f - current) % 360.0f;
        return rot + ((rot > 0.0f) ? -180.0f : 180.0f);
    }
    public static float EXgetYawChangeGiven(double posX, double posZ, float yaw) {
        double deltaX = posX - mc.thePlayer.posX;
        double deltaZ = posZ - mc.thePlayer.posZ;
        double yawToEntity;
        if(deltaZ < 0.0D && deltaX < 0.0D) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if(deltaZ < 0.0D && deltaX > 0.0D) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw - (float)yawToEntity));
    }
    public static float getPitchChange(float pitch, Entity entity, double posY) {
        double var10000 = entity.posX;
        double deltaX = var10000 - mc.thePlayer.posX;
        var10000 = entity.posZ;
        double deltaZ = var10000 - mc.thePlayer.posZ;
        var10000 = posY - 2.2D + (double)entity.getEyeHeight();
        double deltaY = var10000 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(pitch - (float)pitchToEntity) - 2.5F;
    }
    public static void setTargetRotation(final Rotation rotation, final int keepLength) {
        if(Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        targetRotation = rotation;
        RotationUtil.keepLength = keepLength;
    }
    public static boolean canEntityBeSeen(Entity e){
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight()/1.32F),e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY) ;
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;
        vec2 = new Vec3(maxx,miny,minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;
        vec2 = new Vec3(minx,miny,minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if(see)
            return true;
        vec2 = new Vec3(minx,miny,maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;
        vec2 = new Vec3(maxx,miny,maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;

        vec2 = new Vec3(maxx, maxy,minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if(see)
            return true;
        vec2 = new Vec3(minx, maxy,minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;
        vec2 = new Vec3(minx, maxy,maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if(see)
            return true;
        vec2 = new Vec3(maxx, maxy,maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        return see;
    }
    public static float[] getRotationsEx(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + (double)(ent.getEyeHeight() / 2.0F);
        return getRotationFromPosition(x, z, y);
    }
    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.thePlayer.posX;
        double zDiff = z - mc.thePlayer.posZ;
        double yDiff = y - mc.thePlayer.posY - 1.2D;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D));
        return new float[]{yaw, pitch};
    }
    public static float getYawChangeGiven(double posX, double posZ, float yaw) {
        double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity;
        if (deltaZ < 0.0D && deltaX < 0.0D) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaZ < 0.0D && deltaX > 0.0D) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw - (float)yawToEntity));
    }

    public static float[] getRotationsInsane(Entity entity, double maxRange) {
        if (entity == null) {
            return null;
        } else {
            double diffX = entity.posX - mc.thePlayer.posX;
            double diffZ = entity.posZ - mc.thePlayer.posZ;
            Location BestPos = new Location(entity.posX, entity.posY, entity.posZ);
            Location myEyePos = new Location(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

            double diffY;
            for(diffY = entity.boundingBox.minY + 0.7D; diffY < entity.boundingBox.maxY - 0.1D; diffY += 0.1D) {
                if (myEyePos.distanceTo(new Location(entity.posX, diffY, entity.posZ)) < myEyePos.distanceTo(BestPos)) {
                    BestPos = new Location(entity.posX, diffY, entity.posZ);
                }
            }

            if (myEyePos.distanceTo(BestPos) >= maxRange) {
                return null;
            } else {
                diffY = BestPos.getY() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
                double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
                float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
                float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D));
                return new float[]{yaw, pitch};
            }
        }
    }
    public static float[] rotate(EntityLivingBase ent) {

        final double x = ent.posX - mc.thePlayer.posX;
        double y = ent.posY - mc.thePlayer.posY;
        final double z = ent.posZ - mc.thePlayer.posZ;
        y /= mc.thePlayer.getDistanceToEntity(ent);
        final float yaw = (float) (-(Math.atan2(x, z) * 57.29577951308232));
        final float pitch = (float) (-(Math.asin(y) * 57.29577951308232));
        return new float[]{yaw, pitch};
    }

    public static Vec3 getEyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }
    public static float[] getNeededRotations(Vec3 vec) {
        Vec3 eyesPos = RotationUtil.getEyesPos();
        double diffX = vec.xCoord - eyesPos.xCoord + 0.5;
        double diffY = vec.yCoord - eyesPos.yCoord + 0.5;
        double diffZ = vec.zCoord - eyesPos.zCoord + 0.5;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)((- Math.atan2(diffY, diffXZ)) * 180.0 / 3.141592653589793);
        float[] arrf = new float[]{MathHelper.wrapAngleTo180_float(yaw), Minecraft.getMinecraft().gameSettings.keyBindJump.pressed ? 90.0f : MathHelper.wrapAngleTo180_float(pitch)};
        return arrf;
    }
    public static void faceVectorPacketInstant(Vec3 vec) {
        float[] rotations = RotationUtil.getNeededRotations(vec);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], mc.thePlayer.onGround));
    }
    public static float pitch() {
        return Helper.mc.thePlayer.rotationPitch;
    }

    public static void pitch(float pitch) {
        Helper.mc.thePlayer.rotationPitch = pitch;
    }

    public static float yaw() {
        return Helper.mc.thePlayer.rotationYaw;
    }

    public static void yaw(float yaw) {
        Helper.mc.thePlayer.rotationYaw = yaw;
    }

    public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss) {
        double var6;
        double var4 = target.posX - Helper.mc.thePlayer.posX;
        double var8 = target.posZ - Helper.mc.thePlayer.posZ;
        if (target instanceof EntityLivingBase) {
            EntityLivingBase var10 = (EntityLivingBase) target;
            var6 = var10.posY + (double) var10.getEyeHeight()
                    - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        } else {
            var6 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0
                    - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        }
        Random rnd = new Random();
        double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float) (Math.atan2(var8, var4) * 180.0 / 3.141592653589793) - 90.0f;
        float var13 = (float) (-Math.atan2(var6 - (target instanceof EntityPlayer ? 0.25 : 0.0), var14) * 180.0
                / 3.141592653589793);
        float pitch = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationPitch, var13, p_706253);
        float yaw = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationYaw, var12, p_706252);
        return new float[] { yaw, pitch };
    }

    public static float changeRotation(float p_706631, float p_706632, float p_706633) {
        float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
        if (var4 > p_706633) {
            var4 = p_706633;
        }
        if (var4 < -p_706633) {
            var4 = -p_706633;
        }
        return p_706631 + var4;
    }

    public static double[] getRotationToEntity(Entity entity) {
        double pX = Helper.mc.thePlayer.posX;
        double pY = Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight();
        double pZ = Helper.mc.thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double) (entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new double[] { yaw, 90.0 - pitch };
    }

    public static float[] getRotations(Entity entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - Helper.mc.thePlayer.posX;
        double diffZ = entity.posZ - Helper.mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + ((double) elb.getEyeHeight() - 0.4)
                    - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0
                    - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[] { yaw, pitch };
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] grabBlockRotations(BlockPos pos) {
        return RotationUtil.getVecRotation(
                Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0),
                new Vec3((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5));
    }

    public static float[] getVecRotation(Vec3 position) {
        return RotationUtil.getVecRotation(
                Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0),
                position);
    }

    public static float[] getVecRotation(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[] { yaw, pitch };
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int) ((double) (yaw + (float) (360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }

    public static float getYawChange(float yaw, double posX, double posZ) {
        double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if (deltaZ != 0)
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
    }
    public static Vec3 getVectorForRotation(Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - 3.1415927F);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - 3.1415927F);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
    public static double getRotationDifference(Rotation a, Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }
    public static double getRotationDifference(final Rotation rotation) {
        return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
    }
    @EventHandler
    public void onTick(EventTick event) {
        if(targetRotation != null) {
            keepLength--;
            if (keepLength <= 0)
                reset();
        }
    }
    public static void reset() {
        keepLength = 0;
        targetRotation = null;
    }

    private static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }
}
