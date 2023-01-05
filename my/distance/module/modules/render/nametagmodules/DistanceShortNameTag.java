package my.distance.module.modules.render.nametagmodules;

import my.distance.Client;
import my.distance.manager.FriendManager;
import my.distance.module.modules.combat.AntiBot;
import my.distance.module.modules.combat.HypixelAntibot;
import my.distance.module.modules.world.Teams;
import my.distance.ui.font.FontLoaders;
import my.distance.util.misc.Helper;
import my.distance.util.render.Colors;
import my.distance.util.render.RenderUtil;
import my.distance.util.SuperLib;
import my.distance.fastuni.FastUniFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.GLUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class DistanceShortNameTag {
    public static DecimalFormat format = new DecimalFormat("0.0");
    private static final Minecraft mc = Helper.mc;
    private static final ScaledResolution scaledResolution = new ScaledResolution(mc);

    public static void renderNameTag(EntityLivingBase entity, boolean invis, Map<EntityLivingBase, double[]> entityPositions, boolean armor, int alpha) {
        if (entity != mc.thePlayer && (invis || !entity.isInvisible())) {
            GlStateManager.pushMatrix();
            if (entity instanceof EntityPlayer) {
                double[] array = entityPositions.get(entity);
                if (array[3] < 0.0D || array[3] >= 1.0D) {
                    GlStateManager.popMatrix();
                    return;
                }

                FastUniFontRenderer wqy16 = Client.FontLoaders.Chinese16;
                GlStateManager.translate(array[0] / scaledResolution.getScaleFactor(), array[1] / scaledResolution.getScaleFactor(), 0.0D);
                scale();
                GlStateManager.translate(0.0D, 5D, 0.0D);
                String s;
                if (HypixelAntibot.isBot(entity)) {
                    s = "§c§l[Bot]§r§7";
                } else if (AntiBot.isServerBot(entity)) {
                    s = "§1§l[Bot]§r§7";
                } else {
                    s = "";
                }
                String s2;
                if (FriendManager.isFriend(entity.getName())) {
                    s2 = "§6[F]";
                } else {
                    s2 = "";
                }
                String s3 = "";
                if (Teams.isOnSameTeam(entity)) {
                    s3 = "§a[T]";
                } else {
                    s3 = "";
                }
                String s4;
//					if (Teams.isClientFriend((EntityPlayer) entity)) {
//						s4 = "§e[ClientFriend]";
//					}
//					else {
                s4 = "";
//					}
                if ((s3 + s).equals("")) {
                    s3 = "§f";
                }
                String string = " Health: " + format.format(entity.getHealth());
                String string2 = s2 + s3 + s + s4 + entity.getDisplayName().getUnformattedText();
                float n = 60f;
                float n2 = (float) wqy16.getStringWidth(string2);
                float n3 = Math.max(n, n2);
                float n4 = n3 + 8.0f;
                RenderUtil.drawRect(-n4 / 2.0f, -25.0f, n4 / 2.0f, -8, new Color(20, 20, 20, alpha).getRGB());
                wqy16.drawStringWithShadow(string2, -n4 / 2.0f + 4.0f, -19.0f, Colors.WHITE.c);
                //Client.FontLoaders.Chinese13.drawStringWithShadow(string, -n4 / 2.0f + 4.0f, -9.0f, Colors.WHITE.c, 255);
                float n11 = (float) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() + entity.getAbsorptionAmount());
                int n12 = Colors.RED.c;
                String formattedText = entity.getDisplayName().getFormattedText();
                int i = 0;
                while (i < formattedText.length()) {
                    if (formattedText.charAt(i) == '§' && i + 1 < formattedText.length()) {
                        int index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(formattedText.charAt(i + 1)));
                        if (index < 16) {
                            try {
                                Color color = new Color(mc.fontRendererObj.colorCode[index]);
                                n12 = getColor(color.getRed(), color.getGreen(), color.getBlue(), 255);
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                        }
                    }
                    ++i;
                }
                RenderUtil.drawRect(-n4 / 2.0f, -9.5f, Math.min(n4, n4 / 2.0f - n4 / 2.0f * (1.0f - n11) * 2.0f), -8, SuperLib.reAlpha(n12, 0.8f));
                if (armor) {
                    ArrayList<ItemStack> list = new ArrayList<>();
                    int j = 0;
                    while (j < 5) {
                        ItemStack equipmentInSlot = entity.getEquipmentInSlot(j);
                        if (equipmentInSlot != null) {
                            list.add(equipmentInSlot);
                        }
                        ++j;
                    }
                    int p_renderItemOverlays_3_ = -(list.size() * 9);
                    for (ItemStack p_getEnchantmentLevel_1_ : list) {
                        GLUtils.enableGUIStandardItemLighting();
                        mc.getRenderItem().zLevel = -150.0f;
                        fixGlintShit();
                        mc.getRenderItem().renderItemIntoGUI(p_getEnchantmentLevel_1_, (int) (p_renderItemOverlays_3_ + 6), (int) (-42.0f));
                        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, p_getEnchantmentLevel_1_, p_renderItemOverlays_3_, -42);
                        mc.getRenderItem().zLevel = 0.0f;
                        p_renderItemOverlays_3_ += 3;
                        GLUtils.disableStandardItemLighting();
                        if (p_getEnchantmentLevel_1_ != null) {
                            int n13 = 21;
                            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, p_getEnchantmentLevel_1_);
                            int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, p_getEnchantmentLevel_1_);
                            int enchantmentLevel3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, p_getEnchantmentLevel_1_);
                            if (enchantmentLevel > 0) {
                                drawEnchantTag("Sh" + getColor(enchantmentLevel) + enchantmentLevel, p_renderItemOverlays_3_, n13);
                                n13 += 6;
                            }
                            if (enchantmentLevel2 > 0) {
                                drawEnchantTag("Fir" + getColor(enchantmentLevel2) + enchantmentLevel2, p_renderItemOverlays_3_, n13);
                                n13 += 6;
                            }
                            if (enchantmentLevel3 > 0) {
                                drawEnchantTag("Kb" + getColor(enchantmentLevel3) + enchantmentLevel3, p_renderItemOverlays_3_, n13);
                            } else if (p_getEnchantmentLevel_1_.getItem() instanceof ItemArmor) {
                                int enchantmentLevel4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, p_getEnchantmentLevel_1_);
                                int enchantmentLevel5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, p_getEnchantmentLevel_1_);
                                int enchantmentLevel6 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, p_getEnchantmentLevel_1_);
                                if (enchantmentLevel4 > 0) {
                                    drawEnchantTag("P" + getColor(enchantmentLevel4) + enchantmentLevel4, p_renderItemOverlays_3_, n13);
                                    n13 += 6;
                                }
                                if (enchantmentLevel5 > 0) {
                                    drawEnchantTag("Th" + getColor(enchantmentLevel5) + enchantmentLevel5, p_renderItemOverlays_3_, n13);
                                    n13 += 6;
                                }
                                if (enchantmentLevel6 > 0) {
                                    drawEnchantTag("Unb" + getColor(enchantmentLevel6) + enchantmentLevel6, p_renderItemOverlays_3_, n13);
                                }
                            } else if (p_getEnchantmentLevel_1_.getItem() instanceof ItemBow) {
                                int enchantmentLevel7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, p_getEnchantmentLevel_1_);
                                int enchantmentLevel8 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, p_getEnchantmentLevel_1_);
                                int enchantmentLevel9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, p_getEnchantmentLevel_1_);
                                if (enchantmentLevel7 > 0) {
                                    drawEnchantTag("Pow" + getColor(enchantmentLevel7) + enchantmentLevel7, p_renderItemOverlays_3_, n13);
                                    n13 += 6;
                                }
                                if (enchantmentLevel8 > 0) {
                                    drawEnchantTag("Pun" + getColor(enchantmentLevel8) + enchantmentLevel8, p_renderItemOverlays_3_, n13);
                                    n13 += 6;
                                }
                                if (enchantmentLevel9 > 0) {
                                    drawEnchantTag("Fir" + getColor(enchantmentLevel9) + enchantmentLevel9, p_renderItemOverlays_3_, n13);
                                }
                            } else if (p_getEnchantmentLevel_1_.getRarity() == EnumRarity.EPIC) {
                                drawEnchantTag("§6§lGod", p_renderItemOverlays_3_ - 2, n13);
                            }
                            float n14 = (float) (p_renderItemOverlays_3_ * 1.05) - 2.0f;

                            if (p_getEnchantmentLevel_1_.getMaxDamage() - p_getEnchantmentLevel_1_.getItemDamage() > 0) {
                                GlStateManager.pushMatrix();
                                GlStateManager.disableDepth();
                                FontLoaders.Comfortaa12.drawString("" + (p_getEnchantmentLevel_1_.getMaxDamage() - p_getEnchantmentLevel_1_.getItemDamage()), n14 + 6.0f, -32.0f, Colors.WHITE.c);
                                GlStateManager.enableDepth();
                                GlStateManager.popMatrix();
                            }
                            p_renderItemOverlays_3_ += 12;
                        }
                    }
                }
            }
            GlStateManager.popMatrix();
        }
    }

    private static void drawEnchantTag(String s, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        n2 -= 6;
        FontLoaders.Comfortaa10.drawStringWithShadow(s, (float) (n + 9), (float) (-30 - n2), Colors.getColor(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static String getColor(int n) {
        if (n == 1) {
        } else {
            if (n == 2) {
                return "§a";
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§6";
            }
        }
        return "§f";
    }

    private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private static void scale() {
        final float n = 1.0f;
        GlStateManager.scale(n, n, n);
    }

    public static int getColor(int p_clamp_int_0_, int p_clamp_int_0_2, int p_clamp_int_0_3, int p_clamp_int_0_4) {
        return MathHelper.clamp_int(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp_int(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp_int(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp_int(p_clamp_int_0_3, 0, 255);
    }
}
