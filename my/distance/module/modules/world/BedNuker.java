package my.distance.module.modules.world;


import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventChat;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPostUpdate;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.Client;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.misc.liquidbounce.LiquidRender;
import my.distance.util.misc.liquidbounce.RotationUtils;
import my.distance.util.misc.liquidbounce.VecRotation;
import my.distance.util.math.Rotation;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class BedNuker extends Module {
    private static BlockPos blockBreaking;
    private static BlockPos selfBed;
    private final Numbers<Double> reach = new Numbers<>("Break Reach",3d,0d,10d,1d);
    private final Option markSelf = new Option("ExcludeSelf",true);

    public BedNuker() {
        super("BedNuker", new String[]{"BedFucker"}, ModuleType.World);
        addValues(reach,markSelf);
    }

    @Override
    public void onEnable(){
        selfBed = null;
        new Thread(BedNuker::updateCloestBlockPos,"BedNuker-Marker").start();
    }

    @EventHandler
    public void onEvent(EventPreUpdate em) {
        if (markSelf.getValue()) {
            if (selfBed != null) {
                double posX = mc.thePlayer.posX;
                double posZ = mc.thePlayer.posZ;

                double bedPosX = selfBed.getX();
                double bedPosZ = selfBed.getZ();
                if (posZ > bedPosZ - 10 && posZ < bedPosZ + 10 && posX > bedPosX - 10 && posX < bedPosX + 10) {
                    return;
                }
            }else {
                new Thread(BedNuker::updateCloestBlockPos,"BedNuker-Marker").start();
            }
        }
        int reachs = reach.get().intValue();
        for (int x = -reachs; x < reachs; ++x) {
            for (int y = reachs; y > -reachs; --y) {
                for (int z = -reachs; z < reachs; ++z) {
                    boolean uwot = x != 0 || z != 0;
                    if (uwot) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                        if (this.getFacingDirection(pos) != null && blockChecks(mc.theWorld.getBlockState(pos).getBlock()) && mc.thePlayer.getDistance(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z) < (double) mc.playerController.getBlockReachDistance() - 0.5D) {
                            VecRotation rotation = RotationUtils.faceBlock(pos);
                            Rotation rotations = rotation.getRotation();
                            em.setYaw(rotations.getYaw());
                            em.setPitch(rotations.getPitch());
                            Client.RenderRotate(em.getYaw(), em.getPitch());
                            blockBreaking = pos;
                            return;
                        }
                    }
                }
            }
        }
        blockBreaking = null;
    }
    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (blockBreaking != null) {
            LiquidRender.drawBlockBox(blockBreaking, Client.getBlueColor(80), false);
        }
        if (selfBed != null) {
            LiquidRender.drawBlockBox(selfBed, new Color(120, 255, 120, 80), false);
        }
    }
    @EventHandler
    public void onPost(EventPostUpdate e){
        if (blockBreaking != null) {
            if (mc.playerController.blockHitDelay > 1) {
                mc.playerController.blockHitDelay = 1;
            }

            mc.thePlayer.swingItem();
            EnumFacing direction = this.getFacingDirection(blockBreaking);
            if (direction != null) {
                mc.playerController.onPlayerDamageBlock(blockBreaking, direction);
            }
        }
    }
    @EventHandler
    public void onPacket(EventChat e) {
        if (markSelf.getValue()) {
            if (e.getMessage().startsWith("起床战争") && e.getMessage().endsWith("' 刚刚开始!")) {
                selfBed = null;
            }
        } else {
            if (selfBed != null) selfBed = null;
        }
    }

    public static void drawFilledBox(AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
            GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
            GL11.glEnd();
        }
    }

    public void drawFilledBBESP(AxisAlignedBB axisalignedbb, int color) {
        GL11.glPushMatrix();
        float red = (float)(color >> 24 & 255) / 255.0F;
        float green = (float)(color >> 16 & 255) / 255.0F;
        float blue = (float)(color >> 8 & 255) / 255.0F;
        float alpha = (float)(color & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawFilledBox(axisalignedbb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private static boolean blockChecks(Block block) {
        return block == Blocks.bed;
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isSolidFullCube()) {
            direction = EnumFacing.NORTH;
        }

        MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D));
        return rayResult != null && rayResult.getBlockPos() == pos ? rayResult.sideHit : direction;
    }

    public static void updateCloestBlockPos() {
        double posX = mc.thePlayer.posX;
        double posY = mc.thePlayer.posY;
        double posZ = mc.thePlayer.posZ;
        CopyOnWriteArrayList<BlockPos> targetBlockList = new CopyOnWriteArrayList<>();
        int searchDistance = 20;
        for (double SearchX = posX - searchDistance; SearchX < posX + searchDistance; SearchX++) {
            for (double SearchY = posY - searchDistance; SearchY < posY + searchDistance; SearchY++) {
                for (double SearchZ = posZ - searchDistance; SearchZ < posZ + searchDistance; SearchZ++) {
                    BlockPos blp = new BlockPos(SearchX, SearchY, SearchZ);
                    if (!mc.theWorld.isAirBlock(blp)) {
                        if (blockChecks(mc.theWorld.getBlockState(blp).getBlock())) {
                            targetBlockList.add(blp);
                        }
                    }
                }
            }
        }
        if (targetBlockList.size() == 0) {
            selfBed = null;
        }else{
            selfBed = getClosestBlock(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, targetBlockList);
            Notifications.getManager().post("BedNuker","已标记您自己的床,它将会高亮显示为绿色");
        }
    }
    public static BlockPos getClosestBlock(double posX,double posY, double posZ,List<BlockPos> blpList) {
        blpList.sort((o1, o2) ->{
            double distanceA = o1.distanceSqToCenter(posX, posY, posZ);
            double distanceB = o2.distanceSqToCenter(posX, posY, posZ);
            return (int) (distanceA - distanceB);
        });
        return blpList.get(0);
    }

}
