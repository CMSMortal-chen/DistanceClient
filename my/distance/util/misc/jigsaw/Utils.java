package my.distance.util.misc.jigsaw;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Utils {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static Random rand = new Random();

    public static boolean spectator;

    public static ArrayList<Entity> blackList = new ArrayList<>();

    static double x;
    static double y;
    static double z;
    static double xPreEn;
    static double yPreEn;
    static double zPreEn;
    static double xPre;
    static double yPre;
    static double zPre;

    private static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    @SuppressWarnings("unused")
    public static MovingObjectPosition rayTracePos(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid,
                                                   boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float[] rots = getFacePosRemote(vec32, vec31);
        float yaw = rots[0];
        double angleA = Math.toRadians(Utils.normalizeAngle(yaw));
        double angleB = Math.toRadians(Utils.normalizeAngle(yaw) + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size2);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size2);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size2);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size2);
        if (false) {
            MovingObjectPosition trace2 = mc.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                    ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
            return trace2;
        }
        // MovingObjectPosition trace4 = mc.theWorld.rayTraceBlocks(leftA,
        // left2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace1 = mc.theWorld.rayTraceBlocks(left, left2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = mc.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = mc.theWorld.rayTraceBlocks(right, right2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        // MovingObjectPosition trace5 = mc.theWorld.rayTraceBlocks(rightA,
        // right2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        // positionsBack.add(rightA);
        // positionsBack.add(right2A);
        // positionsBack.add(leftA);
        // positionsBack.add(left2A);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (trace2 != null || trace1 != null || trace3 != null || trace4 != null || trace5 != null) {
            if (returnLastUncollidableBlock) {
                if (trace5 != null && (Utils.getBlock(trace5.getBlockPos()).getMaterial() != Material.air
                        || trace5.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace5;
                }
                if (trace4 != null && (Utils.getBlock(trace4.getBlockPos()).getMaterial() != Material.air
                        || trace4.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace4;
                }
                if (trace3 != null && (Utils.getBlock(trace3.getBlockPos()).getMaterial() != Material.air
                        || trace3.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null && (Utils.getBlock(trace1.getBlockPos()).getMaterial() != Material.air
                        || trace1.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null && (Utils.getBlock(trace2.getBlockPos()).getMaterial() != Material.air
                        || trace2.entityHit != null)) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            } else {
                if (trace5 != null) {
                    return trace5;
                }
                if (trace4 != null) {
                    return trace4;
                }
                if (trace3 != null) {
                    // positions.add(BlockTools.getVec3(trace3.getBlockPos()));
                    return trace3;
                }
                if (trace1 != null) {
                    // positions.add(BlockTools.getVec3(trace1.getBlockPos()));
                    return trace1;
                }
                if (trace2 != null) {
                    // positions.add(BlockTools.getVec3(trace2.getBlockPos()));
                    return trace2;
                }
            }
        }
        if (trace2 == null) {
            if (trace3 == null) {
                if (trace1 == null) {
                    if (trace5 == null) {
                        if (trace4 == null) {
                            return null;
                        }
                        return trace4;
                    }
                    return trace5;
                }
                return trace1;
            }
            return trace3;
        }
        return trace2;
    }

    public static boolean rayTraceWide(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
                                       boolean returnLastUncollidableBlock) {
        float yaw = getFacePosRemote(vec32, vec31)[0];
        yaw = Utils.normalizeAngle(yaw);
        yaw += 180;
        yaw = MathHelper.wrapAngleTo180_float(yaw);
        double angleA = Math.toRadians(yaw);
        double angleB = Math.toRadians(yaw + 180);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * size, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * size, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleA) * size2);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * size2, vec31.yCoord,
                vec31.zCoord + Math.sin(angleB) * size2);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleA) * size2);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * size2, vec32.yCoord,
                vec32.zCoord + Math.sin(angleB) * size2);
        // MovingObjectPosition trace4 = mc.theWorld.rayTraceBlocks(leftA,
        // left2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace1 = mc.theWorld.rayTraceBlocks(left, left2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = mc.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = mc.theWorld.rayTraceBlocks(right, right2, stopOnLiquid,
                ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        // MovingObjectPosition trace5 = mc.theWorld.rayTraceBlocks(rightA,
        // right2A, stopOnLiquid, ignoreBlockWithoutBoundingBox,
        // returnLastUncollidableBlock);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (returnLastUncollidableBlock) {
            return (trace1 != null && Utils.getBlock(trace1.getBlockPos()).getMaterial() != Material.air)
                    || (trace2 != null && Utils.getBlock(trace2.getBlockPos()).getMaterial() != Material.air)
                    || (trace3 != null && Utils.getBlock(trace3.getBlockPos()).getMaterial() != Material.air)
                    || (trace4 != null && Utils.getBlock(trace4.getBlockPos()).getMaterial() != Material.air)
                    || (trace5 != null && Utils.getBlock(trace5.getBlockPos()).getMaterial() != Material.air);
        } else {
            return trace1 != null || trace2 != null || trace3 != null || trace5 != null || trace4 != null;
        }

    }

    public static void blinkToPosFromPos(Vec3 src, Vec3 dest, double maxTP) {
        double range = 0;
        double xDist = src.xCoord - dest.xCoord;
        double yDist = src.yCoord - dest.yCoord;
        double zDist = src.zCoord - dest.zCoord;
        double x1 = src.xCoord;
        double y1 = src.yCoord;
        double z1 = src.zCoord;
        double x2 = dest.xCoord;
        double y2 = dest.yCoord;
        double z2 = dest.zCoord;
        range = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        double step = maxTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            if (maxTP * steps > range) {
                break;
            }
        }
        for (int i = 0; i < steps; i++) {
            double difX = x1 - x2;
            double difY = y1 - y2;
            double difZ = z1 - z2;
            double divider = step * i;
            double x = x1 - difX * divider;
            double y = y1 - difY * divider;
            double z = z1 - difZ * divider;
            //Jigsaw.chatMessage(y);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
    }

    public static boolean isBlacklisted(Entity en) {
        for(Entity i : blackList) {
            if(i.isEntityEqual(en)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<EntityLivingBase> getClosestEntitiesToEntity(float range, Entity ent) {
        ArrayList<EntityLivingBase> entities = new ArrayList<>();
        for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (Utils.isNotItem(o) && !ent.isEntityEqual((EntityLivingBase) o)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (ent.getDistanceToEntity(en) < range) {
                    entities.add(en);
                }
            }
        }
        return entities;
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntityFromEntity(Entity entityIn, Entity entityIn2) {
        float f = (float) (entityIn.posX - entityIn2.posX);
        float f1 = (float) (entityIn.posY - entityIn2.posY);
        float f2 = (float) (entityIn.posZ - entityIn2.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

//	public static boolean checkEntity(boolean friends, boolean invisible, boolean players) {
//		if (en.isEntityEqual(Minecraft.getMinecraft().thePlayer)) {
//			return false;
//		}
//		if (en instanceof EntityPlayer && Jigsaw.getModuleByName("Freecam").isToggled()
//				&& en.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
//			return false;
//		}
//		if (en instanceof EntityPlayer && Jigsaw.getModuleByName("Blink").isToggled()
//				&& en.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
//			return false;
//		}
//		if (en.isDead) {
//			return false;
//		}
//		if (en.getHealth() <= 0) {
//			return false;
//		}
//		if (!(en instanceof EntityLivingBase)) {
//			return false;
//		}
//		if (en instanceof EntityPlayer && Jigsaw.getFriendsMananger().isFriend((EntityPlayer) en)) {
//			if (!Jigsaw.getModuleByName("Friends").isToggled()) {
//				return false;
//			}
//		}
//		if (en.isInvisible()) {
//			if (!Jigsaw.getModuleByName("Invisible").isToggled()) {
//				return false;
//			}
//		}
//		if (en instanceof EntityPlayer) {
//			if (!Jigsaw.getModuleByName("Players").isToggled() || en.height < 0.21f) {
//				return false;
//			}
//		}
//		if (Team.isOnTeam(en)) {
//			if (!Jigsaw.getModuleByName("Team").isToggled()) {
//				return false;
//			}
//		}
//		if (!(en instanceof EntityPlayer)) {
//			if (!Jigsaw.getModuleByName("NonPlayers").isToggled()) {
//				return false;
//			}
//		}
//		if ((en instanceof EntityPlayer)) {
//			if (Jigsaw.getBypassManager().getEnabledBypass() != null && Jigsaw.getBypassManager().getEnabledBypass().getName().equals("AntiGwen")) {
//				if (!((EntityPlayer) en).didSwingItem) {
//					if (en.onGround) {
//						if (en.isSprinting()) {
//							return true;
//						}
//					} else {
//						if (en.hurtResistantTime == 0) {
//							return false;
//						}
//					}
//				}
//			}
//			if(Jigsaw.getBypassManager().getEnabledBypass() != null && Jigsaw.getBypassManager().getEnabledBypass().getName().equals("AntiWatchdog")) {
//				if(en.ticksExisted < 139) {
//					return false;
//				}
//			}
//		}
//		if(isBlacklisted(en)) {
//			return false;
//		}
//		// if(en.hurtTime > 12 &&
//		// !Jigsaw.getModuleByName("HurtResistant").isToggled()) {
//		// return false;
//		// }
//		return true;
//	}

    public static EntityLivingBase getClosestEntitySkipValidCheck(float range) {
        EntityLivingBase closestEntity = null;
        float mindistance = range;
        for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (isNotItem(o) && !(o instanceof EntityPlayerSP)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) < mindistance) {
                    mindistance = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en);
                    closestEntity = en;
                }
            }
        }
        return closestEntity;
    }

    public static EntityLivingBase getClosestEntityToEntity(float range, Entity ent) {
        EntityLivingBase closestEntity = null;
        float mindistance = range;
        for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (isNotItem(o) && !ent.isEntityEqual((EntityLivingBase) o)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (ent.getDistanceToEntity(en) < mindistance) {
                    mindistance = ent.getDistanceToEntity(en);
                    closestEntity = en;
                }
            }
        }
        return closestEntity;
    }

    public static boolean isNotItem(Object o) {
        if (!(o instanceof EntityLivingBase)) {
            return false;
        }
        return true;
    }

    public static void faceEntity(Entity en) {
        facePos(new Vec3(en.posX - 0.5, en.posY + (en.getEyeHeight() - en.height / 1.5), en.posZ - 0.5));

    }

    public static void faceBlock(BlockPos blockPos) {
        facePos(getVec3(blockPos));
    }

    public static Vec3 getVec3(BlockPos blockPos) {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos getBlockPos(Vec3 vec) {
        return new BlockPos(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static void facePos(Vec3 vec) {
        double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        double diffY = vec.yCoord + 0.5
                - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        Minecraft.getMinecraft().thePlayer.rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw
                + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
        Minecraft.getMinecraft().thePlayer.rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch
                + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
    }

    /**
     *
     * @param vec
     * @return index 0 = yaw | index 1 = pitch
     */
    public static float[] getFacePos(Vec3 vec) {
        double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        double diffY = vec.yCoord + 0.5
                - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] {
                Minecraft.getMinecraft().thePlayer.rotationYaw
                        + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
                Minecraft.getMinecraft().thePlayer.rotationPitch
                        + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch) };
    }

    /**
     *
     *
     * @return index 0 = yaw | index 1 = pitch
     */
    public static float[] getFacePosRemote(Vec3 src, Vec3 dest) {
        double diffX = dest.xCoord - src.xCoord;
        double diffY = dest.yCoord - (src.yCoord);
        double diffZ = dest.zCoord - src.zCoord;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] {MathHelper.wrapAngleTo180_float(yaw),
                MathHelper.wrapAngleTo180_float(pitch) };
    }

    /**
     *
     * @param
     * @return index 0 = yaw | index 1 = pitch
     */
    public static float[] getFacePosEntity(Entity en) {
        if (en == null) {
            return new float[] { Minecraft.getMinecraft().thePlayer.rotationYawHead,
                    Minecraft.getMinecraft().thePlayer.rotationPitch };
        }
        return getFacePos(new Vec3(en.posX - 0.5, en.posY + (en.getEyeHeight() - en.height / 1.5), en.posZ - 0.5));
    }

    /**
     *
     * @param
     * @return index 0 = yaw | index 1 = pitch
     */
    public static float[] getFacePosEntityRemote(EntityLivingBase facing, Entity en) {
        if (en == null) {
            return new float[] { facing.rotationYawHead, facing.rotationPitch };
        }
        return getFacePosRemote(new Vec3(facing.posX, facing.posY + en.getEyeHeight(), facing.posZ),
                new Vec3(en.posX, en.posY + en.getEyeHeight(), en.posZ));
    }

    // public static int getDistanceFromMouse(Entity entity)
    // {
    // float[] neededRotations = getRotationsNeeded(entity);
    // if(neededRotations != null)
    // {
    // float neededYaw =
    // Minecraft.getMinecraft().thePlayer.rotationYaw
    // - neededRotations[0], neededPitch =
    // Minecraft.getMinecraft().thePlayer.rotationPitch
    // - neededRotations[1];
    // float distanceFromMouse =
    // MathHelper.sqrt_float(neededYaw * neededYaw + neededPitch
    // * neededPitch);
    // return (int)distanceFromMouse;
    // }
    // return -1;
    // }
    // public static float[] getRotationsNeeded(Entity entity)
    // {
    // if(entity == null)
    // return null;
    // double diffX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
    // double diffY;
    // if(entity instanceof EntityLivingBase)
    // {
    // EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
    // diffY =
    // entityLivingBase.posY
    // + entityLivingBase.getEyeHeight()
    // * 0.9
    // - (Minecraft.getMinecraft().thePlayer.posY + Minecraft
    // .getMinecraft().thePlayer.getEyeHeight());
    // }else
    // diffY =
    // (entity.boundingBox.minY + entity.boundingBox.maxY)
    // / 2.0D
    // - (Minecraft.getMinecraft().thePlayer.posY + Minecraft
    // .getMinecraft().thePlayer.getEyeHeight());
    // double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
    // double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    // float yaw =
    // (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
    // float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
    // if(AuraUtils.getSmoothAim()) {
    // return new float[]{
    // (float) (MathHelper.wrapAngleTo180_float(yaw
    // - Minecraft.getMinecraft().thePlayer.rotationYaw) /
    // AuraUtils.getSmoothAimSpeed()),
    // (float) (MathHelper.wrapAngleTo180_float(pitch
    // - Minecraft.getMinecraft().thePlayer.rotationPitch) /
    // AuraUtils.getSmoothAimSpeed())};
    // }
    // return new float[]{
    // Minecraft.getMinecraft().thePlayer.rotationYaw
    // + MathHelper.wrapAngleTo180_float(yaw
    // - Minecraft.getMinecraft().thePlayer.rotationYaw),
    // Minecraft.getMinecraft().thePlayer.rotationPitch
    // + MathHelper.wrapAngleTo180_float(pitch
    // - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    //
    // }
    // public static float[] getRotationsNeededRemote(EntityLivingBase remote,
    // Entity entity)
    // {
    // if(entity == null)
    // return null;
    // double diffX = entity.posX - remote.posX;
    // double diffY;
    // if(entity instanceof EntityLivingBase)
    // {
    // EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
    // diffY =
    // entityLivingBase.posY
    // + entityLivingBase.getEyeHeight()
    // * 0.9
    // - (remote.posY + Minecraft
    // .getMinecraft().thePlayer.getEyeHeight());
    // }else
    // diffY =
    // (entity.boundingBox.minY + entity.boundingBox.maxY)
    // / 2.0D
    // - (remote.posY + remote.getEyeHeight());
    // double diffZ = entity.posZ - remote.posZ;
    // double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    // float yaw =
    // (float)(Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
    // float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
    // if(AuraUtils.getSmoothAim()) {
    // return new float[]{
    // (float) (MathHelper.wrapAngleTo180_float(yaw
    // - remote.rotationYaw) / AuraUtils.getSmoothAimSpeed()),
    // (float) (MathHelper.wrapAngleTo180_float(pitch
    // - remote.rotationPitch) / AuraUtils.getSmoothAimSpeed())};
    // }
    // return new float[]{
    // remote.rotationYaw
    // + MathHelper.wrapAngleTo180_float(yaw
    // - remote.rotationYaw),
    // remote.rotationPitch
    // + MathHelper.wrapAngleTo180_float(pitch
    // - remote.rotationPitch)};
    //
    // }
    public static float getPlayerBlockDistance(BlockPos blockPos) {
        return getPlayerBlockDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static float getPlayerBlockDistance(double posX, double posY, double posZ) {
        float xDiff = (float) (Minecraft.getMinecraft().thePlayer.posX - posX);
        float yDiff = (float) (Minecraft.getMinecraft().thePlayer.posY - posY);
        float zDiff = (float) (Minecraft.getMinecraft().thePlayer.posZ - posZ);
        return getBlockDistance(xDiff, yDiff, zDiff);
    }

    public static float getBlockDistance(float xDiff, float yDiff, float zDiff) {
        return MathHelper.sqrt_float(
                (xDiff - 0.5F) * (xDiff - 0.5F) + (yDiff - 0.5F) * (yDiff - 0.5F) + (zDiff - 0.5F) * (zDiff - 0.5F));
    }

    public static ArrayList<EntityItem> getNearbyItems(int range) {
        ArrayList<EntityItem> eList = new ArrayList<>();
        for (Object o : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
            if (!(o instanceof EntityItem)) {
                continue;
            }
            EntityItem e = (EntityItem) o;
            if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) >= range) {
                continue;
            }

            eList.add(e);
        }
        return eList;
    }

    public static EntityItem getClosestItem(float range) {
        float mindistance = range;
        EntityItem ee = null;
        for (Object o : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
            if (!(o instanceof EntityItem)) {
                continue;
            }
            EntityItem e = (EntityItem) o;
            if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) >= mindistance) {
                continue;
            }
            ee = e;
        }
        return ee;
    }

    public static Entity getClosestItemOrXPOrb(float range) {
        float mindistance = range;
        Entity ee = null;
        for (Object o : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
            if (!(o instanceof EntityItem) && !(o instanceof EntityXPOrb)) {
                continue;
            }
            Entity e = (Entity) o;
            if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) >= mindistance) {
                continue;
            }
            ee = e;
        }
        return ee;
    }

    private static float limitAngleChange(final float current, final float intended, final float maxChange) {
        float change = intended - current;
        if (change > maxChange)
            change = maxChange;
        else if (change < -maxChange)
            change = -maxChange;
        return current + change;
    }

    public static double normalizeAngle(double angle) {
        return (angle + 360) % 360;
    }

    public static float normalizeAngle(float angle) {
        return (angle + 360) % 360;
    }

    public static int getItemIndexHotbar(int itemID) {
        for (int i = 0; i < 9; i++) {
            ItemStack stackInSlot = mc.thePlayer.inventory.getStackInSlot(i);
            if (stackInSlot == null) {
                continue;
            }
            if (itemID == Item.getIdFromItem(stackInSlot.getItem())) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isBlockPosAir(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock().getMaterial() == Material.air;
    }

    public static Block getBlockRelativeToEntity(Entity en, double d) {
        return getBlock(new BlockPos(en.posX, en.posY + d, en.posZ));
    }

    public static BlockPos getBlockPosRelativeToEntity(Entity en, double d) {
        return new BlockPos(en.posX, en.posY + d, en.posZ);
    }

    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    private static Vec3 lastLoc = null;

    public static Vec3 getLastGroundLocation() {
        return lastLoc;

    }

    public static void updateLastGroundLocation() {
        if(mc.thePlayer.onGround) {
            lastLoc = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }
    }

    public static double getXZDist(Vec3 loc1, Vec3 loc2) {
        double xDist = loc1.getX() - loc2.getX();
        double zDist = loc1.getZ() - loc2.getZ();
        return Math.abs(Math.sqrt(xDist * xDist + zDist * zDist));
    }

    public static IBlockState getBlockState(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos);
    }

    public static ArrayList<BlockPos> getBlockPosesPlayerIsStandingOn() {
        BlockPos pos1 = new BlockPos(mc.thePlayer.boundingBox.minX, mc.thePlayer.boundingBox.minY - 0.01, mc.thePlayer.boundingBox.minZ);

        BlockPos pos2 = new BlockPos(mc.thePlayer.boundingBox.maxX, mc.thePlayer.boundingBox.minY - 0.01, mc.thePlayer.boundingBox.maxZ);

        Iterable<BlockPos> collisionBlocks = BlockPos.getAllInBox(pos1, pos2);
        ArrayList<BlockPos> returnList = new ArrayList<>();
        for(BlockPos pos : collisionBlocks) {
            returnList.add(pos);
        }
        return returnList;
    }

    public static ArrayList<BlockPos> getBlockPosesEntityIsStandingOn(Entity en) {
        BlockPos pos1 = new BlockPos(en.boundingBox.minX, en.boundingBox.minY - 0.01, en.boundingBox.minZ);

        BlockPos pos2 = new BlockPos(en.boundingBox.maxX, en.boundingBox.minY - 0.01, en.boundingBox.maxZ);

        Iterable<BlockPos> collisionBlocks = BlockPos.getAllInBox(pos1, pos2);
        ArrayList<BlockPos> returnList = new ArrayList<>();
        for(BlockPos pos : collisionBlocks) {
            returnList.add(pos);
        }
        return returnList;
    }

    public static boolean isEntityOnGround(Entity en) {
        ArrayList<BlockPos> poses = getBlockPosesEntityIsStandingOn(en);


        for(BlockPos pos : poses) {
            Block block = Utils.getBlock(pos);
            if(!(block.getMaterial() instanceof MaterialTransparent) && block.getMaterial() != Material.air
                    && !(block instanceof BlockLiquid)) {
                return true;
            }
        }

        return false;
    }

}







