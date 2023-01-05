package my.distance.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class XrayBlock {
    public double x;
    public double y;
    public double z;
    public String type;

    public XrayBlock(BlockPos blockPos, String name) {
        z = blockPos.getZ();
        y = blockPos.getY();
        x = blockPos.getX();
        type = name;
    }
    public XrayBlock(BlockPos blockPos) {
        z = blockPos.getZ();
        y = blockPos.getY();
        x = blockPos.getX();
        type = Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock().getUnlocalizedName();
    }
}
