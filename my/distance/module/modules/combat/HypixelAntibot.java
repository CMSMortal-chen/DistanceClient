package my.distance.module.modules.combat;

import my.distance.api.EventHandler;
import my.distance.api.events.Misc.EventChat;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.events.World.EventWorldChanged;
import my.distance.api.value.Mode;
import my.distance.api.value.Option;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class HypixelAntibot extends Module {
    public Mode mode = new Mode("Mode", "Mode", AntiMode.values(), AntiMode.Hypixel);
    public static CopyOnWriteArrayList<EntityPlayer> Bots = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<EntityPlayer> RemoveBots = new CopyOnWriteArrayList<>();
    private final TimerUtil timer;
    private final TimerUtil delayTimer = new TimerUtil();
    public static List<EntityPlayer> invalid = new ArrayList<>();
    int bots;
    public static Option remove = new Option("Remove",true);

    ConcurrentHashMap<String, Long> botMap = new ConcurrentHashMap<>();

    public HypixelAntibot() {
        super("AntiBot", new String[]{"AntiBot"}, ModuleType.Combat);
        this.timer = new TimerUtil();
        this.bots = 0;
        this.addValues(this.mode,remove);

    }
    @EventHandler
    public void onWorldChange(EventWorldChanged e){
        Bots.clear();
        botMap.clear();
        RemoveBots.clear();
    }

    @EventHandler
    public void onUpdate(final EventPreUpdate event) {
        this.setSuffix(this.mode.getValue());
        //if (delayTimer.hasReached(500L)) {
            if (this.mode.getValue() == AntiMode.Hypixel) {
                if (timer.hasReached(1000L)) {
                    Bots.clear();
                    timer.reset();
                }
                for (final Object entities : mc.theWorld.loadedEntityList) {
                    if (entities instanceof EntityPlayer) {
                        final EntityPlayer entityPlayer2;
                        final EntityPlayer entity = entityPlayer2 = (EntityPlayer) entities;
                        if (entityPlayer2 != mc.thePlayer) {
                            if (mc.thePlayer.getDistanceToEntity(entity) < 10) {
                                if (!entity.getDisplayName().getFormattedText().contains("\u00a7") || entity.isInvisible() //startwith
                                        || entity.getDisplayName().getFormattedText().contains("§8[NPC]")
                                        || entity.getDisplayName().getFormattedText().toLowerCase().contains("\u00a7c")) {
                                    Bots.add(entity);
                                }
                            }
                            if (!Bots.contains(entity)) {
                                continue;
                            }
                            Bots.remove(entity);
                        }
                    }
                    for (final Entity entity2 : mc.theWorld.getLoadedEntityList()) {
                        if (entity2 instanceof EntityPlayer) {
                            final EntityPlayer entityPlayer;
                            final EntityPlayer ent = entityPlayer = (EntityPlayer) entity2;
                            if (entityPlayer == mc.thePlayer) {
                                continue;
                            }
                            if (!ent.isInvisible()) {
                                continue;
                            }
                            if (!getTabPlayerList().contains(ent) && mc.thePlayer.ticksExisted > 100) {
                                if (remove.getValue()) {
                                    mc.theWorld.removeEntity(ent);
                                    bots++;
                                } else if (!RemoveBots.contains(ent)) {
                                    RemoveBots.add(ent);
                                }
                            }
                        }
                    }
                }
                if (this.bots != 0) {
                    Notifications.getManager().post("Watchdog Killer", "移除了" + bots + "个假人 =ω=", Notifications.Type.INFO);
                    bots = 0;
                }
            }else if (this.mode.getValue() == AntiMode.Huayuting){
                for (Map.Entry<String,Long> entry: botMap.entrySet()){
                    if (entry.getValue() <= (System.currentTimeMillis())){
                        botMap.remove(entry.getKey());
                    }
                }
            }
            delayTimer.reset();
        //}
    }

    @EventHandler
    public void onChat(EventChat e){
        if (this.mode.getValue() == AntiMode.Huayuting) {
            if (e.getMessage().contains("杀死了") && e.getMessage().startsWith("起床战争")) {
                String[] names = e.getMessage().split("杀死了");
                if (names.length <= 2) {
                    if (!names[1].split("\\(")[0].equals(" " + mc.session.getUsername())) {
                        botMap.put(names[1].split("\\(")[0], (System.currentTimeMillis() + 6000L));
                    }
                }
            }
        }
    }

    private String processName(String nameIn){
        return nameIn;
    }

    public static boolean isEntityBot(EntityPlayer player) {
        if (player.getGameProfile() == null) {
            return true;
        }
        NetworkPlayerInfo npi = mc.getNetHandler().getPlayerInfo(player.getGameProfile().getId());
        return (npi == null || npi.getGameProfile() == null || npi.getResponseTime() != 1);
    }

    public boolean isInGodMode(final Entity entity) {
        return this.isEnabled() && this.mode.getValue() == AntiMode.Hypixel && entity.ticksExisted <= 25;
    }

    public static boolean isBot(Entity e){
        return isServerBot(e);
    }
    public static boolean isServerBot(Entity entity) {
        return ((HypixelAntibot) ModuleManager.getModByClass(HypixelAntibot.class)).isBots(entity);
    }

    public boolean isBots(Entity entity) {
        if (isEnabled()) {
            if (mode.getValue() == AntiMode.Hypixel) {
                if (Bots.contains(entity)){
                    return true;
                }
                if (RemoveBots.contains(entity)){
                    return true;
                }

                if (entity instanceof EntityMob || entity instanceof EntitySlime) {
                    return false;
                }

                if (entity instanceof EntityAnimal || entity instanceof EntityVillager) {
                    return false;
                }
                if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible()
                        && !entity.getDisplayName().getFormattedText().contains("§8[NPC]")) {
                    return isInGodMode(entity);
                }
                return true;
            }
            if (mode.getValue().equals(AntiMode.Huayuting)) {
                if ((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) < 0.21) {
                    return true;
                }
                if (botMap.containsKey(" " + entity.getName())) {
                    return true;
                }
            }
            if (this.mode.getValue() == AntiMode.Mineplex) {
                return !entity.onGround;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        timer.reset();
        Bots.clear();
        bots = 0;
    }

    @Override
    public void onDisable() {
        timer.reset();
        Bots.clear();
        bots = 0;
    }

    public static List<EntityPlayer> getTabPlayerList() {
        final NetHandlerPlayClient var4 = mc.thePlayer.sendQueue;
        final ArrayList<EntityPlayer> list = new ArrayList<>();
        final List<NetworkPlayerInfo> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(mc.theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }

    enum AntiMode {
        Hypixel,
        Mineplex,
        Huayuting
    }
}
