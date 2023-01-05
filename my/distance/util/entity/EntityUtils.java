package my.distance.util.entity;

import my.distance.manager.FriendManager;
import my.distance.manager.ModuleManager;
import my.distance.module.modules.combat.AntiBot;
import my.distance.module.modules.combat.HypixelAntibot;
import my.distance.module.modules.combat.KillAura;
import my.distance.module.modules.world.Teams;
import my.distance.util.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

public final class EntityUtils {
    static Minecraft mc = Minecraft.getMinecraft();

    public static KillAura killAura = (KillAura) ModuleManager.getModuleByClass(KillAura.class);

    public static boolean targetInvisible = killAura.invisibleValue.get();
    public static boolean targetPlayer = killAura.playerValue.get();
    public static boolean targetMobs = killAura.mobsValue.get();
    public static boolean targetAnimals = killAura.animalsValue.get();
    public static boolean targetDead = killAura.deadValue.get();

    public static boolean isSelected(final Entity entity, final boolean canAttackCheck) {
        killAura = (KillAura) ModuleManager.getModuleByClass(KillAura.class);
        targetInvisible = killAura.invisibleValue.get();
        targetPlayer = killAura.playerValue.get();
        targetMobs = killAura.mobsValue.get();
        targetAnimals = killAura.animalsValue.get();
        targetDead = killAura.deadValue.get();
        if(entity instanceof EntityLivingBase && (targetDead || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if(targetInvisible || !entity.isInvisible()) {
                if(targetPlayer && entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;

                    if(canAttackCheck) {
                        if(HypixelAntibot.isServerBot(entityPlayer))
                            return false;

                        if(AntiBot.isServerBot(entityPlayer))
                            return false;


                        if(entityPlayer.isSpectator())
                            return false;

                        final Teams teams = (Teams) ModuleManager.getModuleByClass(Teams.class);
                        return !teams.isEnabled() || !teams.isOnSameTeam(entityPlayer);
                    }

                    return true;
                }

                return targetMobs && isMob(entity) || targetAnimals && isAnimal(entity);

            }
        }
        return false;
    }

    public static boolean isFriend(final Entity entity) {
        return entity instanceof EntityPlayer && entity.getName() != null &&
               FriendManager.isFriend(ColorUtils.stripColor(entity.getName()));
    }

    public static boolean isAnimal(final Entity entity) {
        return entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityGolem ||
                entity instanceof EntityBat;
    }

    public static boolean isMob(final Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntityVillager || entity instanceof EntitySlime ||
                entity instanceof EntityGhast || entity instanceof EntityDragon;
    }

    public static String getName(final NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() :
                ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    public static int getPing(final EntityPlayer entityPlayer) {
        if(entityPlayer == null)
            return 0;

        final NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());

        return networkPlayerInfo == null ? 0 : networkPlayerInfo.getResponseTime();
    }
}
