package my.distance.module.modules.combat;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.time.Timer;
import my.distance.util.math.RotationUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;



public class AutoPot
extends Module {
    private final Option REGEN = new Option("Regen", true);
    private final Option SPEED = new Option("Speed", true);
    private final Option PREDICT = new Option("Predict", true);
    private final Option Ground = new Option("GroundCheck", true);
    private final Numbers<Double> HEALTH = new Numbers<>("Health", 6.0, 0.5, 10.0, 0.5);

    public AutoPot() {
        super("AutoPot", new String[]{"AutoPot"}, ModuleType.Combat);
        super.addValues(this.REGEN, this.SPEED, this.PREDICT, this.HEALTH, this.Ground);
        this.setColor(new Color(208, 30, 142).getRGB());
    }

    public static boolean potting;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    private void onUpdate(EventPreUpdate em) {
        if (!mc.thePlayer.onGround && Ground.getValue()) return;
        final boolean speed = this.SPEED.getValue();
        final boolean regen = this.REGEN.getValue();

        if (timer.delay(200)) {
            if (potting)
                potting = false;
        }
        int spoofSlot = getBestSpoofSlot();
        int[] pots = {6, -1, -1};
        if (regen)
            pots[1] = 10;
        if (speed)
            pots[2] = 1;

        for (int pot : pots) {
            if (pot == -1)
                continue;
            if (pot == 6 || pot == 10) {
                if (timer.delay(900) && !mc.thePlayer.isPotionActive(pot)) {
                    if (mc.thePlayer.getHealth() < this.HEALTH.getValue() * 2) {
                        getBestPot(spoofSlot, pot);
                    }
                }
            } else if (timer.delay(1000) && !mc.thePlayer.isPotionActive(pot)) {
                getBestPot(spoofSlot, pot);
            }
        }
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    float[] getRotations() {
        double movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 26.0D;
        double movedPosY = mc.thePlayer.boundingBox.minY - 3.6D;
        double movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 26.0D;
        if (this.PREDICT.getValue())
            return RotationUtil.getRotationFromPosition(movedPosX, movedPosZ, movedPosY);
        else
            return new float[]{mc.thePlayer.rotationYaw, 90};
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            } else if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                spoofSlot = i - 36;
                break;
            }
        }
        return spoofSlot;
    }

    void getBestPot(int hotbarSlot, int potID) {
        if (KillAura.currentTarget != null) return;
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is).isEmpty())
                        return;
                    PotionEffect effect = pot.getEffects(is).get(0);
                    int potionID = effect.getPotionID();
                    if (potionID == potID)
                        if (ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)) {
                            if (36 + hotbarSlot != i)
                                swap(i, hotbarSlot);
                            timer.reset();
                            int oldSlot = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(hotbarSlot));
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(getRotations()[0], getRotations()[1], mc.thePlayer.onGround));
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
                            potting = true;
                            break;
                        }
                }
            }
        }
    }

    boolean isBestPot(ItemPotion potion, ItemStack stack) {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
            return false;
        PotionEffect effect = potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier();
        int duration = effect.getDuration();
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPotion) {
                    ItemPotion pot = (ItemPotion) is.getItem();
                    if (pot.getEffects(is) != null) {
                        for (PotionEffect o : pot.getEffects(is)) {
                            int id = o.getPotionID();
                            int ampl = o.getAmplifier();
                            int dur = o.getDuration();
                            if (id == potionID && ItemPotion.isSplash(is.getItemDamage())) {
                                if (ampl > amplifier) {
                                    return false;
                                } else if (ampl == amplifier && dur > duration) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

