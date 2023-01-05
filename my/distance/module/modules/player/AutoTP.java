package my.distance.module.modules.player;

import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.ClientNotification;
import my.distance.util.entity.MovementUtils;
import my.distance.util.misc.AStarCustomPathFinder;
import my.distance.util.misc.CustomVec3;
import my.distance.util.misc.Helper;
import my.distance.util.time.StopWatchs;
import my.distance.util.Vec3;
import my.distance.api.EventHandler;
import my.distance.api.events.World.*;
import my.distance.api.value.Mode;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public final class AutoTP
        extends Module {
    private static double xpos = 0;
    private static double ypos = 120;
    private static double zpos = 0;
    private final Mode mode = new Mode("Mode",Modes.values(), Modes.Hypixel);
    private final StopWatchs timer = new StopWatchs();
    private CustomVec3 target;
    private int stage;
    boolean tp;
    public static Entity targets;
    public static ArrayList<Vec3> path = new ArrayList<>();

    public AutoTP() {
        super("AutoTP",new String[]{"Auto00"}, ModuleType.Player);
        this.addValues(this.mode);
    }

    @Override
    public void onEnable() {
        Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3 to = new Vec3(xpos, ypos, zpos);
        path = computePath(topFrom, to);
        if (mc.thePlayer == null) {
            this.setEnabled(false);
        }
        this.stage = 0;
        this.tp = false;
        if (this.mode.getValue().equals(Modes.Vanilla)){
            this.tp = true;
        } else if (this.mode.getValue() == Modes.Hypixel) {
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, true));
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212015, mc.thePlayer.posZ, true));
            mc.thePlayer.stepHeight = 0.0f;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onDisable() {
        path.clear();
        targets = null;
        EntityPlayerSP player = mc.thePlayer;
        mc.timer.timerSpeed = 1.0f;
        player.stepHeight = 0.625f;
        player.motionX = 0.0;
        player.motionZ = 0.0;
    }
    @EventHandler
    public void onUpdates(EventPreUpdate e){
        if (!tp) {
            Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Vec3 to = new Vec3(xpos, ypos, zpos);
            path = computePath(topFrom, to);
        }
    }

    @EventHandler
    public void onSendPacket(EventPacketSend event) {
        if (this.stage == 1 && !this.timer.elapsed(6000L) || this.mode.getValue() == Modes.Hypixel && !this.tp && event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onReceivePacket(EventPacketReceive event) {
        if (this.mode.getValue() == Modes.Hypixel && event.getPacket() instanceof S08PacketPlayerPosLook && !this.tp) {
            this.tp = true;
        }
    }
    @EventHandler
    public void onMotionUpdate(EventMotionUpdate event) {
        if (targets != null) {
            xpos = targets.posX;
            ypos = targets.posY;
            zpos = targets.posZ;
        }
        this.setSuffix(xpos+" "+ypos+" "+zpos);
        if (this.tp) {
            this.setEnabled(false);
            new Thread(() -> {
                if (mode.getValue().equals(Modes.Hypixel)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                if (targets != null) {
                    xpos = targets.posX;
                    ypos = targets.posY;
                    zpos = targets.posZ;
                }
                PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                playerCapabilities.setFlySpeed(1.0E8F);
                playerCapabilities.setPlayerWalkSpeed(1.0E8F - 1F);
                mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(playerCapabilities));
                Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                Vec3 to = new Vec3(xpos,ypos,zpos);
                path = computePath(topFrom, to);
                for (Vec3 pathElm : path) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                }

                mc.thePlayer.setPosition(xpos, ypos, zpos);
                Helper.sendClientMessage("传送完成!", ClientNotification.Type.info);
                path.clear();
                xpos = 0;
                ypos = 120;
                zpos = 0;
            }).start();
        }
    }

    @EventHandler
    public void onMove(EventMove event) {
        if (this.stage == 1 & !this.timer.elapsed(6000L) || this.mode.getValue() == Modes.Hypixel) {
            MovementUtils.setSpeed(event, 0.0);
            mc.thePlayer.motionY = 0.0;
            event.y = 0.0;
        }
    }
    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        double dashDistance = 5;
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    public void tp(double x ,double y,double z){
        xpos = x;
        ypos = y;
        zpos = z;
        this.setEnabled(true);
    }
    public void tp(Entity e){
        targets = e;
        this.setEnabled(true);
    }
    private enum Modes {
        Hypixel,
        Vanilla
    }
}
