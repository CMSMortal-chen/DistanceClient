package my.distance.module.modules.combat;

import my.distance.manager.FriendManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.world.Teams;
import my.distance.util.misc.AStarCustomPathFinder;
import my.distance.util.render.Colors;
import my.distance.util.render.RenderingUtil;
import my.distance.util.SuperLib;
import my.distance.util.time.STimer;
import my.distance.util.time.TimerUtil;
import my.distance.util.Vec3;
import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.events.World.EventTick;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TPAura extends Module {
    public static final Mode TPMode = new Mode("Mode",Modes.values(),Modes.Sigma);
    private double dashDistance = 5;
    public static final Numbers<Double> RANGE = new Numbers<>("Range", 30d, 1d, 100d, 5d);
    //public static final Option AUTOBLOCK = new Option("AutoBlock",true);

    public static final Option PLAYERS = new Option("Player",true);
    public static final Option ANIMALS = new Option("Animals",true);
    public static final Option TEAMS = new Option("Team",false);
    public static final Option INVISIBLES = new Option("Invisibles",true);
    public static final Option ESP = new Option("ESP",true);
    public static final Option PATHESP = new Option("DrawPath",true);
    public static final Numbers<Double> CPS = new Numbers<>("CPS", 8d, 1d, 20d, 1d);
    public static final Numbers<Double> MAXT = new Numbers<>("MaxTarget", 1d, 1d, 50d, 1d);
    private ArrayList<Vec3> path = new ArrayList<>();
    private List<Vec3>[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private STimer cps = new STimer();
    public static TimerUtil timer = new TimerUtil();
    private int ticks;
    private int tpdelay;
    public EntityLivingBase TPcurtarget;
    public static boolean canReach;

    //MODIFICATION DE LA REACH DANS ENTITYRENDERER
    public TPAura() {
        super("TPAura",new String[]{"InfiniteAura"}, ModuleType.Combat);
            this.addValues(TPMode,RANGE, PLAYERS, ANIMALS, TEAMS, INVISIBLES, ESP, PATHESP, CPS, MAXT);
    }

    @Override
    public void onEnable() {
        timer.reset();
        targets.clear();
    }
    @Override
    public void onDisable(){
//        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
//                && AUTOBLOCK.getValue() && mc.thePlayer.isBlocking()){
//            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),false);
//            mc.playerController.onStoppedUsingItem(mc.thePlayer);
//        }
    }

    @EventHandler
    public void onTick(EventTick e){
        if(TPMode.getValue() == Modes.ETB){
            ++this.ticks;
            ++this.tpdelay;
            if (this.ticks >= 20 - this.speed()) {
                this.ticks = 0;
                for (Object object : this.mc.theWorld.loadedEntityList) {
                    EntityLivingBase entity;
                    if (!(object instanceof EntityLivingBase) || (entity = (EntityLivingBase)object) instanceof EntityPlayerSP || this.mc.thePlayer.getDistanceToEntity(entity) > 10.0f || !entity.isEntityAlive()) continue;
                    if (FriendManager.isFriend(entity.getName())) continue;
                    this.TPcurtarget = entity;
                    if (this.tpdelay >= 4) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(entity.posX, entity.posY, entity.posZ, false));
                    }
                    if (mc.thePlayer.getDistanceToEntity(entity) >= 10.0f) continue;
                    this.attack(entity);
                }
            }
        }else{
            float delayValue = (20f / CPS.getValue().floatValue()) * 50f;
            targets = getTargets();
            if (cps.check(delayValue)) {
                if (targets.size() > 0) {
//                    if ((mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)&&AUTOBLOCK.getValue()) {
//                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
//                        if (mc.playerController.sendUseItem(this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.inventory.getCurrentItem())) {
//                            mc.getItemRenderer().resetEquippedProgress2();
//                        }
//                    }
                    test = new ArrayList[50];
                    for (int i = 0; i < (targets.size() > MAXT.getValue() ? MAXT.getValue() : targets.size()); i++) {
                        EntityLivingBase T = targets.get(i);
                        Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        Vec3 to = new Vec3(T.posX, T.posY, T.posZ);

                        path = computePath(topFrom, to);
                        test[i] = path;
                        for (Vec3 pathElm : path) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }
                        mc.thePlayer.swingItem();
                        mc.playerController.attackEntity(mc.thePlayer, T);
                        Collections.reverse(path);
                        for (Vec3 pathElm : path) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }
                    }
                    cps.reset();
                }else {
//                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
//                            && AUTOBLOCK.getValue() && mc.thePlayer.isBlocking()){
//                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(),false);
//                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
//                    }
                }
            }
        }
    }
    @EventHandler
    public void onRender2D(EventRender2D e){
        this.setSuffix(TPMode.getValue());
    }
    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if(TPMode.getValue() == Modes.ETB){
        return;
        }
            }
    @EventHandler
    public void onRender(EventRender3D event) {
            if (!targets.isEmpty() && ESP.getValue()) {
                if (targets.size() > 0) {
                    for (int i = 0; i < (targets.size() > MAXT.getValue() ? MAXT.getValue() : targets.size()); i++) {
                        int color = targets.get(i).hurtResistantTime > 15 ? SuperLib.reAlpha(Colors.RED.c,0.2f) : SuperLib.reAlpha(Colors.AQUA.c,0.2f);
                        drawESP(targets.get(i), color);
                    }

                }
            }
            if (!path.isEmpty() && PATHESP.getValue()) {
                for (int i = 0; i < targets.size(); i++) {
                    try {
                        if (test != null)
                            for (Vec3 pos : test[i]) {
                                if (pos != null)
                                    drawPath(pos);
                            }
                    } catch (Exception e) {

                    }
                }

                if (cps.check(1000)) {
                    test = new ArrayList[50];
                    path.clear();
                }
            }
        }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
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


    boolean validEntity(EntityLivingBase entity) {
        double range = RANGE.getValue();
        boolean players = PLAYERS.getValue();
        boolean animals = ANIMALS.getValue();

        if ((mc.thePlayer.isEntityAlive())
                && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {

                if (AntiBot.isServerBot(entity)) {
                    return false;
                }
                if (HypixelAntibot.isBot(entity)) {
                    return false;
                }
                if (entity.isPlayerSleeping()) {
                    return false;
                }
                if (FriendManager.isFriend(entity.getName())) {
                    return false;
                }

                if (entity instanceof EntityPlayer) {
                    if (players) {

                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive()
                                && player.getHealth() == 0.0) {
                            return false;
                        } else if (Teams.isOnSameTeam(player)
                                && (Boolean) TEAMS.getValue()) {
                            return false;
                        } else if (player.isInvisible()
                                && !INVISIBLES.getValue())
                        {
                            return false;
                        } else if (FriendManager.isFriend(player.getName())) {
                            return false;
                        } else
                            return true;
                    }
                } else {
                    if (!entity.isEntityAlive()) {

                        return false;
                    }
                }

                if (entity instanceof EntityMob && animals) {

                    return true;
                }
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager)
                        && animals) {
                    if (entity.getName().equals("Villager")) {
                        return false;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.boundingBox.maxX - entity.boundingBox.minX);
        double height = Math.abs(entity.boundingBox.maxY - entity.boundingBox.minY);
        Vec3 vec = new Vec3(x - width / 2, y, z - width / 2);
        Vec3 vec2 = new Vec3(x + width / 2, y + height, z + width / 2);
        RenderingUtil.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderingUtil.glColor(color);
        RenderingUtil.drawBoundingBox(new AxisAlignedBB(
                vec.getX() - mc.getRenderManager().renderPosX, vec.getY() - mc.getRenderManager().renderPosY, vec.getZ() - mc.getRenderManager().renderPosZ,
                vec2.getX() - mc.getRenderManager().renderPosX, vec2.getY() - mc.getRenderManager().renderPosY, vec2.getZ() - mc.getRenderManager().renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderingUtil.post3D();
    }

    public void drawPath(Vec3 vec) {
        double x = vec.getX() - mc.getRenderManager().renderPosX;
        double y = vec.getY() - mc.getRenderManager().renderPosY;
        double z = vec.getZ() - mc.getRenderManager().renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight();
        RenderingUtil.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int colors[] = {Colors.getColor(Color.WHITE), Colors.getColor(Color.white)};
            RenderingUtil.glColor(colors[1]);
            GL11.glLineWidth(3 - 1 * 2);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();

        RenderingUtil.post3D();
    }
    public void attack(EntityLivingBase entity) {
        this.attack(entity, false);
    }

    public void attack(EntityLivingBase entity, boolean crit) {
        this.mc.thePlayer.swingItem();
        float sharpLevel = EnchantmentHelper.func_152377_a(this.mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        boolean vanillaCrit = this.mc.thePlayer.fallDistance > 0.0f && !this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder() && !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isPotionActive(Potion.blindness) && this.mc.thePlayer.ridingEntity == null;
        this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
        if (crit || vanillaCrit) {
            this.mc.thePlayer.onCriticalHit(entity);
        }
        if (sharpLevel > 0.0f) {
            this.mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    private int speed() {
        return 8;
    }
    enum Modes{
        Sigma,
        ETB
    }
}
