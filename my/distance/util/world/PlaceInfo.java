package my.distance.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public final class PlaceInfo {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BlockPos blockPos;
    private final EnumFacing enumFacing;
    private Vec3 vec3;
//    public static final Companion Companion = new Companion();

    public PlaceInfo(BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
        this.vec3 = vec3;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public EnumFacing getEnumFacing() {
        return this.enumFacing;
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    public void setVec3(Vec3 vec3) {
        this.vec3 = vec3;
    }

    public PlaceInfo(BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3, int n) {
        this(blockPos, enumFacing, vec3);
        if ((n & 4) != 0) {
            vec3 = new Vec3((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
        }
    }
    public static PlaceInfo get(BlockPos blockPos) {
        if (BlockUtils.canBeClicked(blockPos.add(0, -1, 0))) {
            return new PlaceInfo(blockPos.add(0, -1, 0), EnumFacing.UP, null, 4);
        } else if (BlockUtils.canBeClicked(blockPos.add(0, 0, 1))) {
            return new PlaceInfo(blockPos.add(0, 0, 1), EnumFacing.NORTH, null, 4);
        } else if (BlockUtils.canBeClicked(blockPos.add(-1, 0, 0))) {
            return new PlaceInfo(blockPos.add(-1, 0, 0), EnumFacing.EAST, null, 4);
        } else if (BlockUtils.canBeClicked(blockPos.add(0, 0, -1))) {
            return new PlaceInfo(blockPos.add(0, 0, -1), EnumFacing.SOUTH, null, 4);
        } else {
            return BlockUtils.canBeClicked(blockPos.add(1, 0, 0)) ? new PlaceInfo(blockPos.add(1, 0, 0),EnumFacing.WEST, null, 4) : null;
        }
    }


    public static Block getBlock(BlockPos blockPos) {
        IBlockState var1;
        WorldClient var10000 = mc.theWorld;
        if (var10000 != null && (var1 = var10000.getBlockState(blockPos)) != null) {
            return var1.getBlock();
        }
        return null;
    }

    public static IBlockState getState(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos);
    }

    public static boolean canBeClicked(BlockPos blockPos) {
        Block var10000 = PlaceInfo.getBlock(blockPos);
        if (var10000 != null && var10000.canCollideCheck(PlaceInfo.getState(blockPos), false)) {
            WorldClient var1 = mc.theWorld;
            return var1.getWorldBorder().contains(blockPos);
        }
        return false;
    }
}

