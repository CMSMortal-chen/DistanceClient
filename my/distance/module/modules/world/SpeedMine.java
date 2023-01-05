package my.distance.module.modules.world;

import java.awt.*;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventDamageBlock;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;

public class SpeedMine extends Module {

    public static Numbers<Double> speed = new Numbers<>("Speed", "Speed", 0.7, 0.0, 1.0, 0.1);
    public static Numbers<Double> Pot = new Numbers<>("Potion", "Potion", 1.0, 0.0, 4.0, 1.0);
    public static Mode mode = new Mode("Mode", "mode", SpeedMineMode.values(), SpeedMineMode.Packet);
    private boolean bzs = false;
    private float bzx = 0.0f;
    public BlockPos blockPos;
    public EnumFacing facing;
    public static Option SendPacket = new Option("SendPacket", "SendPacket", false);

    public SpeedMine() {
        super("SpeedMine", new String[]{"SpeedMine", "antifall"}, ModuleType.World);
        this.setColor(new Color(223, 233, 233).getRGB());
        super.addValues(speed, mode, Pot, SendPacket);
    }

    public Block getBlock(double x, double y, double z) {
        BlockPos bp = new BlockPos(x, y, z);
        return mc.theWorld.getBlockState(bp).getBlock();
    }

    @EventHandler
    private void onUpdate(EventDamageBlock e) {

        if (SendPacket.getValue())
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getpos(), e.getfac()));
        if (Pot.getValue().intValue() != 0) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 100, Pot.getValue().intValue() - 1));
        } else {
            mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
        }
        if (mode.getValue() == SpeedMineMode.Packet) {
            mc.playerController.blockHitDelay = 0;
            if (mc.playerController.curBlockDamageMP >= SpeedMine.speed.getValue()) {
                mc.playerController.curBlockDamageMP = 1.0f;
            }

        }
        if (mode.getValue() == SpeedMineMode.NewPacket2) {
            if (mc.playerController.curBlockDamageMP == 0.2f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
            if (mc.playerController.curBlockDamageMP == 0.4f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
            if (mc.playerController.curBlockDamageMP == 0.6f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
            if (mc.playerController.curBlockDamageMP == 0.8f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }

        }


        if (mode.getValue() == SpeedMineMode.NewPacket) {
            if (mc.playerController.curBlockDamageMP == 0.1f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
            if (mc.playerController.curBlockDamageMP == 0.4f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
            if (mc.playerController.curBlockDamageMP == 0.7f) {
                mc.playerController.curBlockDamageMP += 0.1f;
            }
        }
    }


    @EventHandler
    public void onDamageBlock(EventPacketSend event) {

        if (mode.getValue() == SpeedMineMode.MiniPacket) {
            if (event.packet instanceof C07PacketPlayerDigging && !mc.playerController.extendedReach() && mc.playerController != null) {
                C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging) event.packet;
                if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    this.bzs = true;
                    this.blockPos = c07PacketPlayerDigging.getPosition();
                    this.facing = c07PacketPlayerDigging.getFacing();
                    this.bzx = 0.0f;
                } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    this.bzs = false;
                    this.blockPos = null;
                    this.facing = null;
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue());
        if (mode.getValue() == SpeedMineMode.MiniPacket) {
            if (mc.playerController.extendedReach()) {
                mc.playerController.blockHitDelay = 0;
            } else if (this.bzs) {
                Block block = mc.theWorld.getBlockState(this.blockPos).getBlock();
                this.bzx += (float) ((double) block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.blockPos) * 1.4);
                if (this.bzx >= 1.0f) {
                    mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                    this.bzx = 0.0f;
                    this.bzs = false;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
    }

    enum SpeedMineMode {
        Packet, NewPacket, NewPacket2, MiniPacket
    }
}
