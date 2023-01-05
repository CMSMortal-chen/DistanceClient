package my.distance.module.modules.world;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.module.ModuleType;

import java.awt.Color;

public class FastPlace extends Module {
	public FastPlace() {
		super("FastPlace", new String[] { "fplace", "fc" }, ModuleType.World);
		this.setColor(new Color(226, 197, 78).getRGB());
	}

	@EventHandler
	private void onTick(EventTick e) {
		this.mc.rightClickDelayTimer = 0;
	}
}
