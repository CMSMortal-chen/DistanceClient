package my.distance.module.modules.world;


import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.world.BlockUtils;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
	public AutoTool() {
		super("AutoTool", new String[] {"AutoTool"}, ModuleType.Player);
    }
	public Class type() {
        return EventPacketSend.class;
    }

	@EventHandler
	    public void onEvent(EventTick event) {
	        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
	            return;
	        }
	        if (mc.objectMouseOver == null) {
	            return;
	        }
	        BlockPos pos = mc.objectMouseOver.getBlockPos();
	        if (pos == null) {
	            return;
	        }
	        BlockUtils.updateTool(pos);
	    }
	}
