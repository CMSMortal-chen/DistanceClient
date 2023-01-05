package my.distance.module.modules.player.Nofalls.impl;

import my.distance.api.events.World.EventMotionUpdate;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.module.modules.player.Nofalls.NofallModule;
import my.distance.util.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class AAC5NoFall implements NofallModule {
    double fall;
    private boolean aac5doFlag = false;
    private boolean aac5Check= false;
    private int aac5Timer = 0;


    @Override
    public void onEnable() {
        fall = 0;
        aac5doFlag = false;
        aac5Check= false;
        aac5Timer = 0;
    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        double offsetYs = 0.0;
        aac5Check = false;
        while (mc.thePlayer.motionY - 1.5 < offsetYs) {
            BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + offsetYs, mc.thePlayer.posZ);
            Block block = BlockUtils.getBlock(blockPos);
            AxisAlignedBB axisAlignedBB = block.getCollisionBoundingBox(mc.theWorld, blockPos, BlockUtils.getState(blockPos));
            if (axisAlignedBB != null) {
                offsetYs = -999.9;
                aac5Check = true;
            }
            offsetYs -= 0.5;
        }
        if (mc.thePlayer.onGround) {
            mc.thePlayer.fallDistance = -2f;
            aac5Check = false;
        }
        if (aac5Timer > 0) {
            aac5Timer -= 1;
        }
        if (aac5Check && mc.thePlayer.fallDistance > 2.5) {
            aac5doFlag = true;
            aac5Timer = 18;
        } else {
            if (aac5Timer < 2) aac5doFlag = false;
        }
        if (aac5doFlag) {
            if (mc.thePlayer.onGround) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
            } else {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, true));
            }
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onUpdateMotion(EventMotionUpdate e) {

    }
}
