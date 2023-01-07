package my.distance.module.modules.combat;

import my.distance.api.EventBus;
import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.*;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.entity.EntityUtils;
import my.distance.util.entity.RaycastUtils;
import my.distance.util.math.Rotation;
import my.distance.util.misc.liquidbounce.LiquidRender;
import my.distance.util.misc.liquidbounce.RotationUtils;
import my.distance.util.render.EaseUtils;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.MSTimer;
import my.distance.util.time.TimeUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Distance client
 *
 * @author Mymylesaws
 */
public class KillAura extends Module {
    // Value
    public final Option playerValue = new Option("TargetPlayer", true);
    public final Option invisibleValue = new Option("TargetInvisible", true);
    public final Option mobsValue = new Option("TargetMobs", true);
    public final Option animalsValue = new Option("TargetAnimals", false);
    public final Option deadValue = new Option("TargetDead", false);
    public final Option toggleWhenDeadValue = new Option("DisableOnDeath", true);

    public final Mode priorityValue = new Mode("Priority", PriorityMode.values(), PriorityMode.Distance);
    public final Mode targetModeValue = new Mode("TargetMode", TargetMode.values(), TargetMode.Single);
    public final Mode swingValue = new Mode("SwingMode", SwingMode.values(), SwingMode.Normal);

    public final Numbers<Double> maxCPS = new Numbers<>("MaxCPS", 10d, 1d, 20d, 1d);
    public final Numbers<Double> minCPS = new Numbers<>("MinCPS", 8d, 1d, 20d, 1d);
    public final Numbers<Double> rangeValue = new Numbers<>("Range", 4.2d, 1d, 10d, 0.01d);
    public final Numbers<Double> hurtTimeValue = new Numbers<>("HurtTime", 10d, 0d, 10d, 1d);
    public final Mode attackTimingValue = new Mode("AttackTiming", AttackTimingMode.values(),AttackTimingMode.Post);

    public final Numbers<Double> throughWallsRangeValue = new Numbers<>("ThroughWallsRange", 1.5d, 0d, 8d, 0.01d);
    public final Numbers<Double> discoverRangeValue = new Numbers<>("DiscoverRange", 6d, 0d, 15d, 0.01d);
    public final Numbers<Double> rangeSprintReducementValue = new Numbers<>("RangeSprintReducement", 0d, 0d, 0.4d, 0.01d);

    public final Mode autoBlockValue = new Mode("AutoBlockMode", AutoBlockMode.values(), AutoBlockMode.Off);
    public final Numbers<Double> autoBlockRangeValue = new Numbers<>("AutoBlockRange", 2.5d, 0d, 8d, 0.01d);
    public final Mode autoBlockPacketValue = new Mode("AutoBlockPacketMode", AutoBlockPacketMode.values(), AutoBlockPacketMode.AfterTick);
    public final Option interactAutoBlockValue = new Option("InteractAutoBlock", true);
    public final Numbers<Double> blockRate = new Numbers<>("BlockRate", 100d, 1d, 100d, 1d);
    public final Option combatDelayValue = new Option("1.9CombatDelay", false);
    public final Option keepSprintValue = new Option("KeepSprint", true);
    // Raycast
    public final Option raycastValue = new Option("RayCast", true);
    public final Option raycastIgnoredValue = new Option("RayCastIgnored", false);
    public final Option livingRaycastValue = new Option("LivingRayCast", true);
    public final Option aacValue = new Option("AAC", true);
    public final Mode rotationModeValue = new Mode("RotationMode", RotationMode.values(), RotationMode.Normal);

    public final Numbers<Double> maxTurnSpeed = new Numbers<>("MaxTurnSpeed", 180d, 0d, 180d, 1d);
    public final Numbers<Double> minTurnSpeed = new Numbers<>("MinTurnSpeed", 180d, 0d, 180d, 1d);
    public final Option silentRotationValue = new Option("SilentRotation", true);
    public final Mode rotationStrafeValue = new Mode("Strafe", RotationStrafeMode.values(), RotationStrafeMode.Silent);
    public final Option strafeOnlyGroundValue = new Option("StrafeOnlyGround", true);
    public final Option randomCenterValue = new Option("RandomCenter", false);
    public final Option outborderValue = new Option("Outborder", false);
    public final Option hitableValue = new Option("AlwaysHitable", true);
    public final Numbers<Double> fovValue = new Numbers<>("FOV", 180d, 0d, 180d, 0.01d);
    // Predict
    public final Option predictValue = new Option("Predict", true);
    public final Numbers<Double> maxPredictSize = new Numbers<>("MaxPredictSize", 1d, 0.1d, 5d, 0.01d);
    public final Numbers<Double> minPredictSize = new Numbers<>("MinPredictSize", 1d, 0.1d, 5d, 0.01d);

    public final Numbers<Double> failRateValue = new Numbers<>("FailRate", 0d, 0d, 100d, 0.01d);
    public final Option fakeSwingValue = new Option("FakeSwing", true);
    public final Option noInventoryAttackValue = new Option("NoInvAttack", false);
    public final Numbers<Double> noInventoryDelayValue = new Numbers<>("NoInvDelay", 200d, 0d, 500d, 1d);
    public final Numbers<Double> switchDelayValue = new Numbers<>("SwitchDelay", 300d, 1d, 2000d, 1d);
    public final Numbers<Double> limitedMultiTargetsValue = new Numbers<>("LimitedMultiTargets", 0d, 0d, 50d, 1d);
    // Render
    public final Mode markValue = new Mode("Esp", MarkMode.values(), MarkMode.Distance);

    // Other value
    // Target
    EntityLivingBase target = null;
    private final MSTimer markTimer = new MSTimer();
    public static EntityLivingBase currentTarget = null;
    private boolean hitable = false;
    private final CopyOnWriteArrayList<Integer> prevTargetEntities = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<EntityLivingBase> discoveredTargets = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<EntityLivingBase> inRangeDiscoveredTargets = new CopyOnWriteArrayList<>();

    // Attack delay
    private final MSTimer attackTimer = new MSTimer();
    private final MSTimer switchTimer = new MSTimer();
    private long attackDelay = 0L;
    private int clicks = 0;


    // Swing
    private MSTimer swingTimer = new MSTimer();
    private long swingDelay = 0L;
    private boolean canSwing = false;

    // Container Delay
    private long containerOpen = -1L;

    // Fake block status
    public boolean blockingStatus = false;

    public KillAura() {
        super("KillAura", new String[]{"ka", "aura"}, ModuleType.Combat);
        addValues(playerValue, invisibleValue, mobsValue, animalsValue, deadValue,toggleWhenDeadValue, priorityValue, targetModeValue, swingValue,
                combatDelayValue, maxCPS, minCPS, attackTimingValue,rangeValue, hurtTimeValue, throughWallsRangeValue, discoverRangeValue, rangeSprintReducementValue,
                autoBlockValue, autoBlockRangeValue, autoBlockPacketValue, interactAutoBlockValue, blockRate,
                keepSprintValue, raycastValue, raycastIgnoredValue, livingRaycastValue, aacValue, rotationModeValue,
                maxTurnSpeed, minTurnSpeed, silentRotationValue, rotationStrafeValue, strafeOnlyGroundValue, randomCenterValue,
                outborderValue, hitableValue, fovValue, predictValue, maxPredictSize, minPredictSize, failRateValue,
                fakeSwingValue, noInventoryAttackValue, noInventoryDelayValue, switchDelayValue, limitedMultiTargetsValue,
                markValue);
    }

    @EventHandler
    public void onWorldChange(EventWorldChanged e){
        if (toggleWhenDeadValue.getValue()) {
            Notifications.getManager().post("KilAura", "检测到世界变更！已自动关闭Killaura");
            this.setEnabled(false);
        }
    }

    /**
     * Enable killaura module
     */
    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        if (mc.theWorld == null) return;
        updateTarget();
    }

    /**
     * Disable killaura module
     */
    @Override
    public void onDisable() {
        target = null;
        currentTarget = null;
        hitable = false;
        prevTargetEntities.clear();
        attackTimer.reset();
        clicks = 0;

        stopBlocking();
    }


    /**
     * Motion event
     */
    @EventHandler
    public void onMotion(EventMotion event) {
        if (mc.thePlayer.isRiding())
            return;

        if (attackTimingValue.getValue().equals(AttackTimingMode.All) || attackTimingValue.getValue().equals(AttackTimingMode.Both) ||
                (attackTimingValue.getValue().equals(AttackTimingMode.Pre) && event.getTypes() == EventMotion.Type.PRE) ||
                (attackTimingValue.getValue().equals(AttackTimingMode.Post) && event.getTypes() == EventMotion.Type.POST)) {
            runAttackLoop();
        }

        if (!event.isPre()) {
            // AutoBlock
            if (!autoBlockValue.get().equals(AutoBlockMode.Off) && !discoveredTargets.isEmpty() && (!autoBlockPacketValue.get().equals(AutoBlockPacketMode.AfterAttack) || discoveredTargets.stream().anyMatch(it -> mc.thePlayer.getDistanceToEntity(it) > getMaxRange())) && canBlock()) {
                EntityLivingBase target = discoveredTargets.get(0);
                if (mc.thePlayer.getDistanceToEntity(target) < autoBlockRangeValue.get())
                    startBlocking(target, interactAutoBlockValue.get() && (mc.thePlayer.getDistanceToEntity(target) < getMaxRange()));
            }

            // Update hitable
            updateHitable();

            return;
        }

        if (rotationStrafeValue.get().equals(RotationStrafeMode.Off)) {
            update();
        }
    }

    private void runAttackLoop() {
        if (clicks <= 0 && canSwing && swingTimer.hasTimePassed(swingDelay)) {
            swingTimer.reset();
            swingDelay = getAttackDelay(minCPS.get().intValue(), maxCPS.get().intValue());
            runSwing();
            return;
        }

        while (clicks > 0) {
            runAttack();
            clicks--;
        }
    }

    private void runSwing() {
        switch ((SwingMode)swingValue.get()){
            case Packet:
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                break;
            case Normal:
                mc.thePlayer.swingItem();
                break;
        }
    }

    /**
     * Strafe event
     */
    @EventHandler
    public void onStrafe(EventStrafe event) {
        if (rotationStrafeValue.get().equals(RotationStrafeMode.Off) && !mc.thePlayer.isRiding()) {
            return;
        }

        update();

        if (strafeOnlyGroundValue.get() && !mc.thePlayer.onGround){
            return;
        }

        if (!discoveredTargets.isEmpty() && RotationUtils.targetRotation != null) {
            switch ((RotationStrafeMode) rotationStrafeValue.get()) {
                case Strict: {
                    if (RotationUtils.targetRotation == null) return;
                    float yaw = RotationUtils.targetRotation.getYaw();
                    float strafe = event.getStrafe();
                    float forward = event.getForward();
                    float friction = event.getFriction();

                    float f = strafe * strafe + forward * forward;

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f);

                        if (f < 1.0F)
                            f = 1.0F;

                        f = friction / f;
                        strafe *= f;
                        forward *= f;

                        float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180F));
                        float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180F));

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
                    }
                    event.setCancelled(true);
                    break;
                }
                case Silent: {
                    update();

                    RotationUtils.targetRotation.applyStrafeToPlayer(event);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    /**
     * Update event
     */
    @EventHandler
    public void onUpdate(EventMotionUpdate event) {
        if (cancelRun()) {
            target = null;
            currentTarget = null;
            hitable = false;
            stopBlocking();
            discoveredTargets.clear();
            inRangeDiscoveredTargets.clear();
            return;
        }

        if (noInventoryAttackValue.get() && (mc.currentScreen instanceof GuiContainer ||
                System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null;
            currentTarget = null;
            hitable = false;
            if (mc.currentScreen instanceof GuiContainer) containerOpen = System.currentTimeMillis();
            return;
        }

        if (!rotationStrafeValue.get().equals(RotationStrafeMode.Off) && !mc.thePlayer.isRiding())
            return;

        if (mc.thePlayer.isRiding())
            update();

        if (attackTimingValue.getValue().equals(AttackTimingMode.All)) {
            runAttackLoop();
        }
    }

    /**
     * Render event
     */
    @EventHandler
    public void onRender3D(EventRender3D event) {
        setSuffix(targetModeValue.getValue());
        if (cancelRun()) {
            target = null;
            currentTarget = null;
            hitable = false;
            stopBlocking();
            discoveredTargets.clear();
            inRangeDiscoveredTargets.clear();
        }
        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) && currentTarget.hurtTime <= hurtTimeValue.get()) {
            clicks++;
            attackTimer.reset();
            attackDelay = getAttackDelay(minCPS.get().intValue(), maxCPS.get().intValue());
        }
        if (!discoveredTargets.isEmpty()) {
            Entity it = discoveredTargets.get(0);
            EntityLivingBase entity = discoveredTargets.get(0);

            switch ((MarkMode) markValue.get()) {
                case Liquid: {
                    LiquidRender.drawPlatform(entity, (entity.hurtResistantTime <= 0) ? new Color(37, 126, 255, 170)
                            : new Color(255, 0, 0, 170));
                    break;
                }
                case Distance:
                    float red1 = entity.hurtTime > 0 ? 1.0F : 0.0F;
                    float green1 = entity.hurtTime > 0 ? 0.2F : 0.60F;
                    float blue1 = 1.0F;
                    float lineRed1 = entity.hurtTime > 0 ? 1.0F : 0.0F;
                    float lineGreen1 = entity.hurtTime > 0 ? 0.2F : 0.60F;
                    float lineBlue1 = 1.0F;
                    double x1 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                    double y1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                    double z1 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                    double width1 = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX - 0.1;
                    double height1 = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY + 0.2;
                    RenderUtil.drawEntityESP(x1, y1, z1, width1, height1, red1, green1, blue1, 0.2F, lineRed1, lineGreen1, lineBlue1, 0.0F, 4.0F);
                    break;
                case Plat:
                    LiquidRender.drawPlatform(entity, hitable ? new Color(37, 126, 255, 70) : new Color(255, 0, 0, 70));
                    break;
                case Jello: {
                    int drawTime = (int) (System.currentTimeMillis() % 2000);
                    boolean drawMode = drawTime > 1000;
                    double drawPercent = drawTime / 1000.0;
                    //true when goes up
                    if (!drawMode) {
                        drawPercent = 1 - drawPercent;
                    } else {
                        drawPercent -= 1;
                    }
                    drawPercent = EaseUtils.easeInOutQuad(drawPercent);
                    CopyOnWriteArrayList<Vec3> points = new CopyOnWriteArrayList<>();
                    AxisAlignedBB bb = it.getEntityBoundingBox();
                    double radius = bb.maxX - bb.minX;
                    double height = bb.maxY - bb.minY;
                    double posX = it.lastTickPosX + (it.posX - it.lastTickPosX) * mc.timer.renderPartialTicks;
                    double posY = it.lastTickPosY + (it.posY - it.lastTickPosY) * mc.timer.renderPartialTicks;
                    if (drawMode) {
                        posY -= 0.5;
                    } else {
                        posY += 0.5;
                    }
                    double posZ = it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * mc.timer.renderPartialTicks;
                    for (int i = 0; i <= 360; i += 7) {
                        points.add(new Vec3(posX - Math.sin(i * Math.PI / 180F) * radius, posY + height * drawPercent, posZ + Math.cos(i * Math.PI / 180F) * radius));
                    }
                    points.add(points.get(0));
                    //draw
                    mc.entityRenderer.disableLightmap();
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    double baseMove = (drawPercent > 0.5 ? 1 - drawPercent : drawPercent) * 2;
                    double min = (height / 60) * 20 * (1 - baseMove) * ((drawMode) ? -1 : 1);
                    for (int i = 0; i <= 20; i++) {
                        double moveFace = (height / 60F) * i * baseMove;
                        if (drawMode) {
                            moveFace = -moveFace;
                        }
                        Vec3 firstPoint = points.get(0);
                        GL11.glVertex3d(
                                firstPoint.xCoord - mc.getRenderManager().viewerPosX, firstPoint.yCoord - moveFace - min - mc.getRenderManager().viewerPosY,
                                firstPoint.zCoord - mc.getRenderManager().viewerPosZ
                        );
                        GL11.glColor4f(1F, 1F, 1F, 0.7F * (i / 20F));
                        for (Vec3 vec3 : points) {
                            GL11.glVertex3d(
                                    vec3.xCoord - mc.getRenderManager().viewerPosX, vec3.yCoord - moveFace - min - mc.getRenderManager().viewerPosY,
                                    vec3.zCoord - mc.getRenderManager().viewerPosZ
                            );
                        }
                        GL11.glColor4f(0F, 0F, 0F, 0F);
                    }
                    GL11.glEnd();
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    /**
     * Handle entity move
     */
    @EventHandler
    public void onEntityMove(EventEntityMovement event) {
        Entity movedEntity = event.getMovedEntity();

        if (target == null || movedEntity != currentTarget)
            return;

        updateHitable();
    }

    /**
     * Attack Delay
     */
    private long getAttackDelay(int maxCps, int minCps) {
        long delay = TimeUtils.randomClickDelay(Math.min(minCps, maxCps), Math.max(minCps, maxCps));
        if (combatDelayValue.get()) {
            double value = 4.0;
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                Item currentItem = mc.thePlayer.inventory.getCurrentItem().getItem();
                if (currentItem instanceof ItemSword) {
                    value -= 2.4;
                } else if (currentItem instanceof ItemPickaxe) {
                    value -= 2.8;
                } else if (currentItem instanceof ItemAxe) {
                    value -= 3;
                }
            }
            delay = (long) Math.max(delay, (1000 / value));
        }
        return delay;
    }

    /**
     * Update current target
     */
    private void updateTarget() {
        // Reset fixed target to null
        target = null;

        // Settings
        Double hurtTime = hurtTimeValue.get();
        Double fov = fovValue.get();
        boolean switchMode = targetModeValue.get().equals(TargetMode.Switch);

        // Find possible targets
        discoveredTargets.clear();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || !EntityUtils.isSelected(entity, true) || (switchMode && prevTargetEntities.contains(entity.getEntityId())))
                continue;

            float distance = mc.thePlayer.getDistanceToEntity(entity);
            double entityFov = RotationUtils.getRotationDifference(entity);

            if (distance <= discoverRangeValue.get() && (fov == 180F || entityFov <= fov) && ((EntityLivingBase) entity).hurtTime <= hurtTime)
                discoveredTargets.add((EntityLivingBase) entity);
        }

        // Sort targets by priority
        switch ((PriorityMode) (priorityValue.get())) {
            case Armor:
                discoveredTargets.sort(Comparator.comparing(EntityLivingBase::getTotalArmorValue)); // Sort by Armor
                break;
            case Distance:
                discoveredTargets.sort(Comparator.comparingDouble(o1 -> mc.thePlayer.getDistanceToEntity(o1))); // Sort by Distance
                break;
            case Health:
                discoveredTargets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth)); // Sort by Health
                break;
            case Direction:
                discoveredTargets.sort(Comparator.comparingDouble(RotationUtils::getRotationDifference)); // Sort by FOV
                break;
            case LivingTime:
                discoveredTargets.sort(Comparator.comparing(o1 -> -o1.ticksExisted));// Sort by Livingtime
                break;
        }

        inRangeDiscoveredTargets.clear();
        inRangeDiscoveredTargets.addAll(discoveredTargets.stream().filter(it -> mc.thePlayer.getDistanceToEntity(it) < getRange(it)).collect(Collectors.toList()));

        // Cleanup last targets when no targets found and try again
        if (inRangeDiscoveredTargets.isEmpty() && !prevTargetEntities.isEmpty()) {
            prevTargetEntities.clear();
            updateTarget();
            return;
        }

        // Find best target
        for (EntityLivingBase entity : discoveredTargets) {
            // Update rotations to current target
            if (!updateRotations(entity)) { // when failed then try another target
                continue;
            }

            // Set target to current entity
            if (mc.thePlayer.getDistanceToEntity(entity) < getMaxRange())
                target = entity;

            return;
        }
    }

    /**
     * Return true if player is holding a sword
     *
     * @return isHolding a sword?
     */
    private boolean canBlock() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private float getRange(Entity entity) {
        float range;
        if (mc.thePlayer.getDistanceToEntity(entity) >= throughWallsRangeValue.get()) {
            range = rangeValue.get().floatValue();
        } else {
            range = throughWallsRangeValue.get().floatValue();
        }
        float sprint = mc.thePlayer.isSprinting() ? rangeSprintReducementValue.get().floatValue() : 0f;
        return range - sprint;
    }

    /**
     * Update killaura rotations to enemy
     */
    private boolean updateRotations(Entity entity) {
        if (maxTurnSpeed.get() <= 0F)
            return true;

        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();

        if (predictValue.get())
            boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get().floatValue(), maxPredictSize.get().floatValue()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get().floatValue(), maxPredictSize.get().floatValue()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get().floatValue(), maxPredictSize.get().floatValue())
            );

        Rotation rotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        try {
            switch ((RotationMode) rotationModeValue.getValue()) {
                case Nya:
                    rotation = RotationUtils.searchCenterLnk(boundingBox, mc.thePlayer.getDistanceToEntity(entity) < throughWallsRangeValue.get()
                            , (float) getMaxRange()).getRotation();
                    break;
                case Normal:
                    rotation = RotationUtils.searchCenter(
                            boundingBox,
                            outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                            randomCenterValue.get(),
                            predictValue.get(),
                            mc.thePlayer.getDistanceToEntity(entity) < throughWallsRangeValue.get(),
                            (float) getMaxRange()
                    ).getRotation();
                    break;
                case Hypixel:
                    break;
                case OldMatrix:
                    rotation = RotationUtils.calculateCenter(
                            "CenterLine",
                            "Off",
                            0d,
                            boundingBox,
                            predictValue.get(),
                            mc.thePlayer.getDistanceToEntity(entity) <= throughWallsRangeValue.get()
                    ).getRotation();
                    rotation.setPitch((float) 89.9);
                    break;
            }

            Rotation limitedRotation;
            if (rotationModeValue.getValue().equals(RotationMode.Hypixel)) {
                float[] rot = RotationUtils.rotateNCP(entity);
                limitedRotation = new Rotation(rot[0], rot[1]);
            }else if(rotationModeValue.getValue().equals(RotationMode.OldMatrix)){
                double diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, rotation);
                if (diffAngle <0) diffAngle = -diffAngle;
                if (diffAngle> 180.0) diffAngle = 180.0;
                limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation, (float) ((float) (-Math.cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * maxTurnSpeed.get() + (Math.cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * minTurnSpeed.get()));
            }else {
                limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation
                        , (float) (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()));
            }
            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, aacValue.get() ? 15 : 0);
            } else {
                limitedRotation.toPlayer(mc.thePlayer);
            }

            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Start blocking
     */
    private void startBlocking(Entity interactEntity, boolean interact) {
        if (autoBlockValue.get().equals(AutoBlockMode.Range) && mc.thePlayer.getDistanceToEntity(interactEntity) > autoBlockRangeValue.get())
            return;

        if (blockingStatus)
            return;

        if (interact) {
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(interactEntity, interactEntity.getPositionVector()));
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT));
        }
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        blockingStatus = true;
    }

    /**
     * Stop blocking
     */
    private void stopBlocking() {
        if (blockingStatus) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, autoBlockPacketValue.get().equals(AutoBlockPacketMode.Hypixel) ? new BlockPos(-1, -1, -1) : BlockPos.ORIGIN, EnumFacing.DOWN));
            blockingStatus = false;
        }
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private void updateHitable() {
        if (hitableValue.get()) {
            hitable = true;
            return;
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeed.get() <= 0F) {
            hitable = true;
            return;
        }

        double reach = getMaxRange();

        if (raycastValue.get()) {
            Entity raycastedEntity = RaycastUtils.raycastEntity(reach, it -> (!livingRaycastValue.get() || it instanceof EntityLivingBase && !(it instanceof EntityArmorStand) &&
                    (EntityUtils.isSelected(it, true) || raycastIgnoredValue.get() || aacValue.get() && !mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.getEntityBoundingBox()).isEmpty())));

            if (raycastValue.get() && raycastedEntity instanceof EntityLivingBase
                    && !EntityUtils.isFriend(raycastedEntity))
                currentTarget = (EntityLivingBase) raycastedEntity;

            hitable = !(maxTurnSpeed.get() > 0F) || currentTarget == raycastedEntity;
        } else {
            hitable = RotationUtils.isFaced(currentTarget, reach);
        }
    }

    private void update() {
        if (cancelRun() || (noInventoryAttackValue.get() && (mc.currentScreen instanceof GuiContainer ||
                System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get()))) {
            return;
        }
        // Update target
        updateTarget();

        if (discoveredTargets.isEmpty()) {
            stopBlocking();
            currentTarget = null;
            return;
        }

        // Target
        currentTarget = target;

        if (!targetModeValue.get().equals(TargetMode.Switch) && EntityUtils.isSelected(currentTarget, true))
            target = currentTarget;
    }

    /**
     * Attack enemy
     */
    private void runAttack() {
        if (target == null) return;
        if (currentTarget == null) return;

        // Settings
        double failRate = failRateValue.get();
        boolean openInventory = aacValue.get() && mc.currentScreen instanceof GuiInventory;
        boolean failHit = failRate > 0 && new Random().nextInt(100) <= failRate;

        if (hitable && !failHit) {
            // Close inventory when open
            if (openInventory) {
                mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());
            }

            // Hypixel Unblock
            if (autoBlockPacketValue.getValue().equals(AutoBlockPacketMode.Hypixel)) {
                stopBlocking();
            }

            // Attack
            if (!targetModeValue.getValue().equals(TargetMode.Multi)) {
                attackEntity(currentTarget);
            } else {
                for (int index = 0; index <= inRangeDiscoveredTargets.size(); index++) {
                    if (index >= limitedMultiTargetsValue.get()) {
                        break;
                    }
                    if (limitedMultiTargetsValue.get() == 0)
                        attackEntity(inRangeDiscoveredTargets.get(index));
                }
            }


            if (targetModeValue.getValue().equals(TargetMode.Switch)) {
                if (switchTimer.hasTimePassed(switchDelayValue.get().longValue())) {
                    prevTargetEntities.add((aacValue.get())? target.getEntityId() : currentTarget.getEntityId());
                    switchTimer.reset();
                }
            } else {
                prevTargetEntities.add((aacValue.get())? target.getEntityId() : currentTarget.getEntityId());
            }

            if (target == currentTarget) {
                target = null;
            }

            // Open inventory
            if (openInventory) {
                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
        } else if (fakeSwingValue.get()) {
            runSwing();
        }
    }

    /**
     * Attack [entity]
     */
    private void attackEntity(EntityLivingBase entity) {
        // Stop blocking
        if (!autoBlockPacketValue.get().equals(AutoBlockPacketMode.Vanilla) && (mc.thePlayer.isBlocking() || blockingStatus)) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            blockingStatus = false;
        }

        EventAttack eventAttack = new EventAttack(entity, true);
        // Call attack event
        EventBus.getInstance().register(eventAttack);
        markTimer.reset();

        // Attack target
        runSwing();

        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

        if (keepSprintValue.get()) {
            // Critical Effect
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() &&
                    !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isRiding())
                mc.thePlayer.onCriticalHit(entity);

            // Enchant Effect
            if (EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), entity.getCreatureAttribute()) > 0F)
                mc.thePlayer.onEnchantmentCritical(entity);
        } else {
            if (mc.playerController.getCurrentGameType() != WorldSettings.GameType.SPECTATOR)
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity);
        }

        // Start blocking after attack
        if (mc.thePlayer.isBlocking() || (!autoBlockValue.get().equals(AutoBlockMode.Off) && canBlock())) {
            if (autoBlockPacketValue.get().equals(AutoBlockPacketMode.AfterTick))
                return;

            if (!(blockRate.get() > 0 && new Random().nextInt(100) <= blockRate.get()))
                return;

            startBlocking(entity, interactAutoBlockValue.get());
        }
    }

    public boolean getBlockingStatus() {
        return blockingStatus;
    }

    /**
     * Check if run should be cancelled
     */
    private boolean cancelRun() {
        return mc.thePlayer.isSpectator() || !
                isAlive(mc.thePlayer);
    }

    /**
     * Check if [entity] is alive
     */
    private boolean isAlive(EntityLivingBase entity) {
        return entity.isEntityAlive() && entity.getHealth() > 0 ||
                aacValue.get() && entity.hurtTime > 3;
    }

    private double getMaxRange() {
        return Math.max(rangeValue.get(), throughWallsRangeValue.get());
    }

    public enum MarkMode {
        Liquid, Jello, Plat, None, Distance,
    }

    enum PriorityMode {
        Health, Distance, Direction, LivingTime, Armor
    }

    enum TargetMode {
        Single, Switch, Multi
    }

    enum SwingMode {
        Normal, Packet, None
    }

    enum AutoBlockMode {
        Range, Fake, Off
    }

    enum AutoBlockPacketMode {
        AfterTick, AfterAttack, Vanilla, Hypixel
    }

    enum RotationStrafeMode {
        Off, Strict, Silent
    }

    enum AttackTimingMode {
        Pre, Post, All, Both
    }

    enum RotationMode {
        None, Normal, Hypixel, Nya, OldMatrix
    }
}
