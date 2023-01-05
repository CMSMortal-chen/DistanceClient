package my.distance.module.modules.player;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.Client;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.math.RandomUtil;
import my.distance.util.world.SpammerUtils;
import my.distance.util.time.TimeHelper;

import java.util.Random;

public class AutoSay extends Module {
    //Code by Mymylesaws
    public static String CustomString = Client.name + " Client";
    TimeHelper delay = new TimeHelper();
    public static Mode mode = new Mode("Mode", "Mode", Spammode.values(), Spammode.Penshen);
    private final Numbers<Double> Abusedelay = new Numbers<>("Abusedelay", "Abusedelay", 1000.0, 500.0, 20000.0, 500.0);
    private final Option allmsg = new Option("AllMsg", "allmsg", true);
    private final Option Random = new Option("Random", true);
     public AutoSay() {
        super("Spammer", new String[]{"AutoSay", "AutoAbuse"}, ModuleType.World);
        this.addValues(this.Abusedelay , mode,this.allmsg,Random);
    }
    Random r = new Random();
    int i = 0;
    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        Random r = new Random();
        String all = "";
        if(this.delay.isDelayComplete(this.Abusedelay.getValue().longValue())) {
            if (allmsg.getValue()) all = "@";
            String fuck = "?";
            switch ((Spammode)mode.getValue()){
                case Distance:
                    fuck = Client.name + " Client | "+"Made by "+ Client.author +" | " + Client.distanceVersion;
                    break;
                case Penshen:
                    fuck = SpammerUtils.intcihui[random.nextInt(SpammerUtils.intcihui.length)];
                    break;
                case Math:
                    if (i > SpammerUtils.Maths.length - 1)i = 0;
                    fuck = SpammerUtils.Maths[i];
                    i++;
                    break;
                case CXK:
                    fuck = SpammerUtils.CXK[random.nextInt(SpammerUtils.CXK.length)];
                    break;
                case Politics:
                    if (i > SpammerUtils.Politics.length - 1)i = 0;
                    fuck = SpammerUtils.Politics[i];
                    i++;
                    break;
                case HHAF:
                    fuck = SpammerUtils.HHAF[random.nextInt(SpammerUtils.HHAF.length)];
                    break;
                case TCC:
                    if (i > SpammerUtils.TCC.length - 1)i = 0;
                    fuck = SpammerUtils.TCC[i];
                    i++;
                    break;
                case Custom:
                    fuck = CustomString;
                    break;
            }
            mc.thePlayer.sendChatMessage( all+"[Distance]" + fuck + (Random.getValue()? " " + RandomUtil.randomString(5):""));
            delay.reset();
        }

    }
    public enum Spammode{
        Distance,
        Penshen,
        Math,
        CXK,
        Politics,
        TCC,
        HHAF,
        Custom

    }
    @Override
    public void onDisable() {
        i = 0;

    }
    @Override
    public void onEnable() {
        i = 0;
    }

}
