package my.distance.module.modules.world;


import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketReceive;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.notifications.user.Notifications;
import my.distance.util.time.TimeHelper;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ScreenShotHelper;

import java.util.Random;

public class AutoGG extends Module {
    public static final String[] word = {
            "Get Distance Client -> distance dot cool",
            "Get Distance Client just for U! -> distance dot cool",
            "Distance just Better Client -> distance dot cool"
    };
    public Option gg = new Option("GG", true);
    public Option ad = new Option("AD", true);
    public Option screenshot = new Option("ScreenShot", false);
    public Option autoplay = new Option("Auto Play", true);
    public Numbers<Double> autoPlayDelay = new Numbers<>( "Play Delay", 3000.0, 500.0, 10000.0, 100.0);
    public Numbers<Double> delay = new Numbers<>( "Speak Delay", 100.0, 100.0, 3000.0, 100.0);

    public boolean needSpeak = false;
    public boolean speaked = false;

    public TimeHelper timer = new TimeHelper();

    public String playCommand = "";
    public String lastTitle = "";

    public int win = 0;

    public AutoGG() {
        super("AutoGG&Play", new String[]{"AutoPlay", "AutoGG"}, ModuleType.World);
        addValues(gg,ad,screenshot,autoplay,autoPlayDelay,delay);
    }

    public String content = "";
    public float animationY = 0;
    public boolean display = false;
    public boolean showed = false;

    @Override
    public void onEnable() {
        playCommand = "";
        this.setSuffix("None");
    }

    @Override
    public void onDisable() {}

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer != null) {
            if (gg.getValue() && autoplay.getValue()) {
                setCustomName("AutoGG&Play");
            }else if (gg.getValue() && !autoplay.getValue()){
                setCustomName("AutoGG");
            }else if (autoplay.getValue() && !gg.getValue()){
                setCustomName("AutoPlay");
            }else{
                setCustomName("Auto?");
            }
            if (needSpeak) {
                if (showed) {
                    Notifications.getManager().post("AutoGG&Play", "您将在" + autoPlayDelay.getValue() / 1000 + "秒内送往新游戏");
                    showed = false;
                }
                if (!speaked && timer.isDelayComplete(delay.getValue().longValue())) {
                    speaked = true;

                    win++;

                    if (this.isEnabled()) {
                        if (gg.getValue()) {
                            mc.thePlayer.sendChatMessage("/ac GG " + (ad.getValue() ? word[new Random().nextInt(word.length)] : ""));
                        }

                        if (screenshot.getValue()) {
                            mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
                        }
                    }
                }

                if (speaked) {
                    if (timer.isDelayComplete(autoPlayDelay.getValue().longValue() + delay.getValue().longValue())) {
                        speaked = false;
                        needSpeak = false;
                        this.display = false;
                        if (autoplay.getValue() && this.isEnabled()) mc.thePlayer.sendChatMessage(playCommand);
                    } else {
                        this.display = autoplay.getValue();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPacketR(EventPacketReceive e) {
        if (e.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) e.getPacket();
            if (packet.getMessage() == null) return;
            String title = packet.getMessage().getFormattedText();
            //Helper.sendMessage(title);
            if ((title.startsWith("\2476\247l") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYOU") && title.endsWith("\247r")) || (title.startsWith("\247c\247lGame") && title.endsWith("\247r")) || (title.startsWith("\247c\247lWITH") && title.endsWith("\247r")) || (title.startsWith("\247c\247lYARR") && title.endsWith("\247r"))) {
                timer.reset();
                showed = true;
                needSpeak = true;
            }
            lastTitle = title;
        }
    }

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (playCommand.startsWith("/play ")) {
            String display = playCommand.replace("/play ", "").replace("_", " ");
            boolean nextUp = true;
            StringBuilder result = new StringBuilder();
            for (char c : display.toCharArray()) {
                if (c == ' ') {
                    nextUp = true;
                    result.append(" ");
                    continue;
                }
                if (nextUp) {
                    nextUp = false;
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
            }
            this.setSuffix(result.toString());
        } else {
            this.setSuffix("None");
        }

        if (e.getPacket() instanceof C0EPacketClickWindow) {
            C0EPacketClickWindow packet = (C0EPacketClickWindow) e.getPacket();
            if (packet.getClickedItem() != null && packet.getClickedItem().getDisplayName().startsWith("\247a")) {
                String itemname = packet.getClickedItem().getDisplayName();
                Item item = packet.getClickedItem().getItem();
                if (itemname.contains("空岛战争") || itemname.contains("SkyWars")) {
                    if (itemname.contains("双人") || itemname.contains("Doubles")) {
                        if (itemname.contains("普通") || itemname.contains("Normal")) {
                            playCommand = "/play teams_normal";
                        } else if (itemname.contains("疯狂") || itemname.contains("Insane")) {
                            playCommand = "/play teams_insane";
                        }
                    } else if (itemname.contains("单挑") || itemname.contains("Solo")) {
                        if (itemname.contains("普通") || itemname.contains("Normal")) {
                            playCommand = "/play solo_normal";
                        } else if (itemname.contains("疯狂") || itemname.contains("Insane")) {
                            playCommand = "/play solo_insane";
                        }
                    }
                }
                if (itemname.contains("起床战争") || itemname.contains("Bed Wars")) {
                    if (itemname.contains("4v4")) {
                        playCommand = "/play bedwars_four_four";
                    } else if (itemname.contains("3v3")) {
                        playCommand = "/play bedwars_four_three";
                    } else if (itemname.contains("双人模式") || itemname.contains("Doubles")) {
                        playCommand = "/play bedwars_eight_two";
                    } else if (itemname.contains("单挑") || itemname.contains("Solo")) {
                        playCommand = "/play bedwars_eight_one";
                    }
                }
            }
        } else if (e.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
            if (packet.getMessage().startsWith("/play")) {
                playCommand = packet.getMessage();
            }
        }
    }
}
