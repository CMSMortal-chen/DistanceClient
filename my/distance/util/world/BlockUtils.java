package my.distance.util.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.distance.util.math.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class BlockUtils {
    static double x;
    static double y;
    static double z;
    static double xPreEn;
    static double yPreEn;
    static double zPreEn;
    static double xPre;
    static double yPre;
    static double zPre;
    static Minecraft mc = Minecraft.getMinecraft();
    static List<Block> blacklistedBlocks = Arrays.asList(
    Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
    Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
    Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
    Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
    Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
    Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
    Blocks.stone_button, Blocks.wooden_button, Blocks.lever);

    public static List<Block> getBlacklistedBlocks(){
        return blacklistedBlocks;
    }
    public static float[] getFacingRotations(int x2, int y2, int z2, EnumFacing facing) {
        EntitySnowball entitySnowball4;
        EntitySnowball entitySnowball5;
        EntitySnowball entitySnowball6;
        EntitySnowball temp = new EntitySnowball(mc.theWorld);
        temp.posX = (double)x2 + 0.5;
        temp.posY = (double)y2 + 0.5;
        temp.posZ = (double)z2 + 0.5;
        EntitySnowball entitySnowball = entitySnowball4 = temp;
        entitySnowball4.posX += (double)facing.getDirectionVec().getX() * 0.25;
        EntitySnowball entitySnowball2 = entitySnowball5 = temp;
        entitySnowball5.posY += (double)facing.getDirectionVec().getY() * 0.25;
        EntitySnowball entitySnowball3 = entitySnowball6 = temp;
        entitySnowball6.posZ += (double)facing.getDirectionVec().getZ() * 0.25;
        return null;
    }

    public static boolean isOnLiquid() {
        boolean onLiquid = false;
        if (BlockUtils.getBlockAtPosC(mc.thePlayer, 0.30000001192092896, 0.10000000149011612, 0.30000001192092896).getMaterial().isLiquid() && BlockUtils.getBlockAtPosC(mc.thePlayer, -0.30000001192092896, 0.10000000149011612, -0.30000001192092896).getMaterial().isLiquid()) {
            onLiquid = true;
        }
        return onLiquid;
    }

    public static boolean isOnLadder() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean onLadder = false;
        int y2 = (int)mc.thePlayer.getEntityBoundingBox().offset((double)0.0, (double)1.0, (double)0.0).minY;
        int x2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX);
        while (x2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1) {
            int z2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ);
            while (z2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1) {
                Block block = BlockUtils.getBlock(x2, y2, z2);
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLadder) && !(block instanceof BlockVine)) {
                        return false;
                    }
                    onLadder = true;
                }
                ++z2;
            }
            ++x2;
        }
        if (!onLadder && !mc.thePlayer.isOnLadder()) {
            return false;
        }
        return true;
    }

    public static boolean isOnIce() {
        if (mc.thePlayer == null) {
            return false;
        }
        boolean onIce = false;
        int y2 = (int)mc.thePlayer.getEntityBoundingBox().offset((double)0.0, (double)-0.01, (double)0.0).minY;
        int x2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX);
        while (x2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1) {
            int z2 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ);
            while (z2 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1) {
                Block block = BlockUtils.getBlock(x2, y2, z2);
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockIce) && !(block instanceof BlockPackedIce)) {
                        return false;
                    }
                    onIce = true;
                }
                ++z2;
            }
            ++x2;
        }
        return onIce;
    }

    public static boolean isReplaceable(BlockPos blockPosition) {
        return getMaterial(blockPosition).isReplaceable();
    }

    public static Material getMaterial(BlockPos blockPosition){
        return getBlock(blockPosition).getMaterial();
    }

    public boolean isInsideBlock() {
        int x2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minX);
        while (x2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1) {
            int y2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minY);
            while (y2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxY) + 1) {
                int z2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ);
                while (z2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1) {
                    AxisAlignedBB boundingBox;
                    Block block = mc.theWorld.getBlockState(new BlockPos(x2, y2, z2)).getBlock();
                    if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x2, y2, z2), mc.theWorld.getBlockState(new BlockPos(x2, y2, z2)))) != null && mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                        return true;
                    }
                    ++z2;
                }
                ++y2;
            }
            ++x2;
        }
        return false;
    }



    public static boolean isBlockUnderPlayer(Material material, float height) {
        if (BlockUtils.getBlockAtPosC(mc.thePlayer, 0.3100000023841858, height, 0.3100000023841858).getMaterial() == material && BlockUtils.getBlockAtPosC(mc.thePlayer, -0.3100000023841858, height, -0.3100000023841858).getMaterial() == material && BlockUtils.getBlockAtPosC(mc.thePlayer, -0.3100000023841858, height, 0.3100000023841858).getMaterial() == material && BlockUtils.getBlockAtPosC(mc.thePlayer, 0.3100000023841858, height, -0.3100000023841858).getMaterial() == material) {
            return true;
        }
        return false;
    }

    public static Block getBlockAtPosC(EntityPlayer inPlayer, double x2, double y2, double z2) {
        return BlockUtils.getBlock(new BlockPos(inPlayer.posX - x2, inPlayer.posY - y2, inPlayer.posZ - z2));
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return BlockUtils.getBlock(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ));
    }

    public static Block getBlockAbovePlayer(EntityPlayer inPlayer, double height) {
        return BlockUtils.getBlock(new BlockPos(inPlayer.posX, inPlayer.posY + (double)inPlayer.height + height, inPlayer.posZ));
    }

    public static Block getBlock(int x2, int y2, int z2) {
        return mc.theWorld.getBlockState(new BlockPos(x2, y2, z2)).getBlock();
    }

    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    private static void preInfiniteReach(double range, double maxXZTP, double maxYTP, ArrayList<Vec3> positionsBack, ArrayList<Vec3> positions, Vec3 targetPos, boolean tpStraight, boolean up2, boolean attack, boolean tpUpOneBlock, boolean sneaking) {
    }

    private static void postInfiniteReach() {
    }

    public static Block getBlock(double x2, double y2, double z2) {
        return mc.theWorld.getBlockState(new BlockPos((int)x2, (int)y2, (int)z2)).getBlock();
    }

    public static boolean infiniteReach(double range, double maxXZTP, double maxYTP, ArrayList<Vec3> positionsBack, ArrayList<Vec3> positions, EntityLivingBase en2) {
        int ind = 0;
        xPreEn = en2.posX;
        yPreEn = en2.posY;
        zPreEn = en2.posZ;
        xPre = mc.thePlayer.posX;
        yPre = mc.thePlayer.posY;
        zPre = mc.thePlayer.posZ;
        boolean attack = true;
        boolean up2 = false;
        boolean tpUpOneBlock = false;
        boolean hit = false;
        boolean tpStraight = false;
        positions.clear();
        positionsBack.clear();
        double step = maxXZTP / range;
        int steps = 0;
        int i2 = 0;
        while ((double)i2 < range) {
            if (maxXZTP * (double)(++steps) > range) break;
            ++i2;
        }
        MovingObjectPosition rayTrace = null;
        MovingObjectPosition rayTrace2 = null;
        Object rayTraceCarpet = null;
        if (BlockUtils.rayTraceWide(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(en2.posX, en2.posY, en2.posZ), false, false, true) || (rayTrace2 = BlockUtils.rayTracePos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(en2.posX, en2.posY + (double)mc.thePlayer.getEyeHeight(), en2.posZ), false, false, true)) != null) {
            rayTrace = BlockUtils.rayTracePos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(en2.posX, mc.thePlayer.posY, en2.posZ), false, false, true);
            if (rayTrace != null || (rayTrace2 = BlockUtils.rayTracePos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(en2.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), en2.posZ), false, false, true)) != null) {
                MovingObjectPosition trace = null;
                if (rayTrace == null) {
                    trace = rayTrace2;
                }
                if (rayTrace2 == null) {
                    trace = rayTrace;
                }
                if (trace != null) {
                    if (trace.getBlockPos() == null) {
                        attack = false;
                        return false;
                    }
                    boolean fence = false;
                    BlockPos target = trace.getBlockPos();
                    up2 = true;
                    y = target.up().getY();
                    yPreEn = target.up().getY();
                    Block lastBlock = null;
                    Boolean found = false;
                    int j2 = 0;
                    while ((double)j2 < maxYTP) {
                        MovingObjectPosition tr2 = BlockUtils.rayTracePos(new Vec3(mc.thePlayer.posX, target.getY() + j2, mc.thePlayer.posZ), new Vec3(en2.posX, target.getY() + j2, en2.posZ), false, false, true);
                        if (tr2 != null && tr2.getBlockPos() != null) {
                            BlockPos blockPos = tr2.getBlockPos();
                            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                            if (block.getMaterial() == Material.air) {
                                fence = lastBlock instanceof BlockFence;
                                y = target.getY() + j2;
                                yPreEn = target.getY() + j2;
                                if (fence) {
                                    y += 1.0;
                                    yPreEn += 1.0;
                                    if ((double)(j2 + 1) > maxYTP) {
                                        found = false;
                                        break;
                                    }
                                }
                                found = true;
                                break;
                            }
                            lastBlock = block;
                        }
                        ++j2;
                    }
                    double difX = mc.thePlayer.posX - xPreEn;
                    double difZ = mc.thePlayer.posZ - zPreEn;
                    double divider = step * 0.0;
                    if (!found.booleanValue()) {
                        attack = false;
                        return false;
                    }
                }
            } else {
                MovingObjectPosition ent = BlockUtils.rayTracePos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(en2.posX, en2.posY, en2.posZ), false, false, false);
                if (ent != null && ent.entityHit == null) {
                    y = mc.thePlayer.posY;
                    yPreEn = mc.thePlayer.posY;
                } else {
                    y = mc.thePlayer.posY;
                    yPreEn = en2.posY;
                }
            }
        }
        if (!attack) {
            return false;
        }
        int k2 = 0;
        while (k2 < steps) {
            double difZ2;
            double difX2;
            double difY;
            double divider2;
            ++ind;
            if (k2 == 1 && up2) {
                x = mc.thePlayer.posX;
                y = yPreEn;
                z = mc.thePlayer.posZ;
                BlockUtils.sendPacket(false, positionsBack, positions);
            }
            if (k2 != steps - 1) {
                difX2 = mc.thePlayer.posX - xPreEn;
                difY = mc.thePlayer.posY - yPreEn;
                difZ2 = mc.thePlayer.posZ - zPreEn;
                divider2 = step * (double)k2;
                x = mc.thePlayer.posX - difX2 * divider2;
                y = mc.thePlayer.posY - difY * (up2 ? 1.0 : divider2);
                z = mc.thePlayer.posZ - difZ2 * divider2;
                BlockUtils.sendPacket(false, positionsBack, positions);
            } else {
                difX2 = mc.thePlayer.posX - xPreEn;
                difY = mc.thePlayer.posY - yPreEn;
                difZ2 = mc.thePlayer.posZ - zPreEn;
                divider2 = step * (double)k2;
                x = mc.thePlayer.posX - difX2 * divider2;
                y = mc.thePlayer.posY - difY * (up2 ? 1.0 : divider2);
                z = mc.thePlayer.posZ - difZ2 * divider2;
                BlockUtils.sendPacket(false, positionsBack, positions);
                double xDist = x - xPreEn;
                double zDist = z - zPreEn;
                double yDist = y - en2.posY;
                double dist = Math.sqrt(xDist * xDist + zDist * zDist);
                if (dist > 4.0) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    BlockUtils.sendPacket(false, positionsBack, positions);
                } else if (dist > 0.05 && up2) {
                    x = xPreEn;
                    y = yPreEn;
                    z = zPreEn;
                    BlockUtils.sendPacket(false, positionsBack, positions);
                }
                if (Math.abs(yDist) < maxYTP && mc.thePlayer.getDistanceToEntity(en2) >= 4.0f) {
                    x = xPreEn;
                    y = en2.posY;
                    z = zPreEn;
                    BlockUtils.sendPacket(false, positionsBack, positions);
                } else {
                    attack = false;
                }
            }
            ++k2;
        }
        k2 = positions.size() - 2;
        while (k2 > -1) {
            x = positions.get((int)k2).xCoord;
            y = positions.get((int)k2).yCoord;
            z = positions.get((int)k2).zCoord;
            BlockUtils.sendPacket(false, positionsBack, positions);
            --k2;
        }
        x = mc.thePlayer.posX;
        y = mc.thePlayer.posY;
        z = mc.thePlayer.posZ;
        BlockUtils.sendPacket(false, positionsBack, positions);
        if (!attack) {
            positions.clear();
            positionsBack.clear();
            return false;
        }
        return true;
    }

    public static double normalizeAngle(double angle) {
        return (angle + 360.0) % 360.0;
    }

    public static float normalizeAngle(float angle) {
        return (angle + 360.0f) % 360.0f;
    }

    public static void sendPacket(boolean goingBack, ArrayList<Vec3> positionsBack, ArrayList<Vec3> positions) {
        C03PacketPlayer.C04PacketPlayerPosition playerPacket = new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true);
        mc.getNetHandler().getNetworkManager().sendPacket(playerPacket);
        if (goingBack) {
            positionsBack.add(new Vec3(x, y, z));
            return;
        }
        positions.add(new Vec3(x, y, z));
    }

    public static void attackInf(EntityLivingBase entity) {
        mc.thePlayer.swingItem();
        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        boolean vanillaCrit = mc.thePlayer.fallDistance > 0.0f && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null;
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
        if (sharpLevel > 0.0f) {
            mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    public static void attackinfGuardian(EntityLivingBase entity) {
        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        boolean vanillaCrit = mc.thePlayer.fallDistance > 0.0f && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null;
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
        if (sharpLevel > 0.0f) {
            mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    public static float[] getFacePos(Vec3 vec) {
        double n2 = vec.xCoord + 0.5;
        double diffX = n2 - mc.thePlayer.posX;
        double n22 = vec.yCoord + 0.5;
        double posY = mc.thePlayer.posY;
        double diffY = n22 - (posY + (double)mc.thePlayer.getEyeHeight());
        double n3 = vec.zCoord + 0.5;
        double diffZ = n3 - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        float[] array = new float[2];
        boolean n4 = false;
        float rotationYaw = mc.thePlayer.rotationYaw;
        float n5 = yaw;
        array[0] = rotationYaw + MathHelper.wrapAngleTo180_float(n5 - mc.thePlayer.rotationYaw);
        boolean n6 = true;
        float rotationPitch = mc.thePlayer.rotationPitch;
        float n7 = pitch;
        array[1] = rotationPitch + MathHelper.wrapAngleTo180_float(n7 - mc.thePlayer.rotationPitch);
        return array;
    }

    public static float[] getFacePosRemote(Vec3 src, Vec3 dest) {
        double diffX = dest.xCoord - src.xCoord;
        double diffY = dest.yCoord - src.yCoord;
        double diffZ = dest.zCoord - src.zCoord;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static MovingObjectPosition rayTracePos(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float[] rots = BlockUtils.getFacePosRemote(vec32, vec31);
        float yaw = rots[0];
        double angleA = Math.toRadians(BlockUtils.normalizeAngle(yaw));
        double angleB = Math.toRadians(BlockUtils.normalizeAngle(yaw) + 180.0f);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
        MovingObjectPosition trace1 = mc.theWorld.rayTraceBlocks(left, left2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = mc.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = mc.theWorld.rayTraceBlocks(right, right2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (trace2 != null || trace1 != null || trace3 != null || trace4 != null || trace5 != null) {
            if (returnLastUncollidableBlock) {
                if (trace5 != null && (BlockUtils.getBlock(trace5.getBlockPos()).getMaterial() != Material.air || trace5.entityHit != null)) {
                    return trace5;
                }
                if (trace4 != null && (BlockUtils.getBlock(trace4.getBlockPos()).getMaterial() != Material.air || trace4.entityHit != null)) {
                    return trace4;
                }
                if (trace3 != null && (BlockUtils.getBlock(trace3.getBlockPos()).getMaterial() != Material.air || trace3.entityHit != null)) {
                    return trace3;
                }
                if (trace1 != null && (BlockUtils.getBlock(trace1.getBlockPos()).getMaterial() != Material.air || trace1.entityHit != null)) {
                    return trace1;
                }
                if (trace2 != null && (BlockUtils.getBlock(trace2.getBlockPos()).getMaterial() != Material.air || trace2.entityHit != null)) {
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
                    return trace3;
                }
                if (trace1 != null) {
                    return trace1;
                }
                if (trace2 != null) {
                    return trace2;
                }
            }
        }
        if (trace2 != null) {
            return trace2;
        }
        if (trace3 != null) {
            return trace3;
        }
        if (trace1 != null) {
            return trace1;
        }
        if (trace5 != null) {
            return trace5;
        }
        if (trace4 == null) {
            return null;
        }
        return trace4;
    }

    public static boolean rayTraceWide(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        float yaw = BlockUtils.getFacePosRemote(vec32, vec31)[0];
        yaw = BlockUtils.normalizeAngle(yaw);
        yaw += 180.0f;
        yaw = MathHelper.wrapAngleTo180_float(yaw);
        double angleA = Math.toRadians(yaw);
        double angleB = Math.toRadians(yaw + 180.0f);
        double size = 2.1;
        double size2 = 2.1;
        Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
        Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
        Vec3 leftA = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
        Vec3 rightA = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
        Vec3 left2A = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
        Vec3 right2A = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
        MovingObjectPosition trace1 = mc.theWorld.rayTraceBlocks(left, left2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace2 = mc.theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace3 = mc.theWorld.rayTraceBlocks(right, right2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
        MovingObjectPosition trace4 = null;
        MovingObjectPosition trace5 = null;
        if (returnLastUncollidableBlock) {
            if (!(trace1 != null && BlockUtils.getBlock(trace1.getBlockPos()).getMaterial() != Material.air || trace2 != null && BlockUtils.getBlock(trace2.getBlockPos()).getMaterial() != Material.air || trace3 != null && BlockUtils.getBlock(trace3.getBlockPos()).getMaterial() != Material.air || trace4 != null && BlockUtils.getBlock(trace4.getBlockPos()).getMaterial() != Material.air || trace5 != null && BlockUtils.getBlock(trace5.getBlockPos()).getMaterial() != Material.air)) {
                return false;
            }
            return true;
        }
        if (trace1 == null && trace2 == null && trace3 == null && trace5 == null && trace4 == null) {
            return false;
        }
        return true;
    }

    public static boolean placeBlockScaffold(BlockPos pos) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        EnumFacing[] arrenumFacing = EnumFacing.values();
        int n2 = arrenumFacing.length;
        int n3 = 0;
        while (n3 < n2) {
            Vec3 hitVec;
            EnumFacing side = arrenumFacing[n3];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (eyesPos.squareDistanceTo(new Vec3(pos).addVector(0.5, 0.5, 0.5)) < eyesPos.squareDistanceTo(new Vec3(neighbor).addVector(0.5, 0.5, 0.5)) && BlockUtils.canBeClicked(neighbor) && eyesPos.squareDistanceTo(hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()).scale(0.5))) <= 18.0625) {
                RotationUtil.faceVectorPacketInstant(hitVec);
                PlayerControllerMP playerController = mc.playerController;
                mc.thePlayer.swingItem();
                mc.rightClickDelayTimer = 4;
                return true;
            }
            ++n3;
        }
        return false;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtils.getBlock(pos).canCollideCheck(BlockUtils.getState(pos), false);
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    private static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3 hitVec) {
        BlockUtils.getPlayerController();
    }

    public static boolean isInLiquid() {
        if (mc.thePlayer.isInWater()) {
            return true;
        } else {
            boolean var1 = false;
            int var2 = (int)mc.thePlayer.getEntityBoundingBox().minY;

            for(int var3 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); var3 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++var3) {
                for(int var4 = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); var4 < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++var4) {
                    Block var5 = mc.theWorld.getBlockState(new BlockPos(var3, var2, var4)).getBlock();
                    if (var5 != null && var5.getMaterial() != Material.air) {
                        if (!(var5 instanceof BlockLiquid)) {
                            return false;
                        }

                        var1 = true;
                    }
                }
            }

            return var1;
        }
    }

    public static void updateTool(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        float strength = 1.0F;
        int bestItemIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            if ((itemStack.getStrVsBlock(block) > strength)) {
                strength = itemStack.getStrVsBlock(block);
                bestItemIndex = i;
            }
        }
        if (bestItemIndex != -1) {
            mc.thePlayer.inventory.currentItem = bestItemIndex;
        }
    }
}

