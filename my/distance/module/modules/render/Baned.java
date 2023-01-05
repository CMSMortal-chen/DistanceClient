package my.distance.module.modules.render;

import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.ui.gui.GuiBaned;
import net.minecraft.client.multiplayer.WorldClient;

public class Baned extends Module {
    public Baned(){
        super("DistanceBan",new String[]{"ban"}, ModuleType.World);
    }
    @Override
    public void onEnable(){
        if (mc.theWorld != null){
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld((WorldClient)null);
        }
        mc.displayGuiScreen(new GuiBaned());
    }
}
