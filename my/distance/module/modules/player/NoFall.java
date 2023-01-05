package my.distance.module.modules.player;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.combat.Criticals;
import my.distance.module.modules.player.Nofalls.NofallModule;
import my.distance.module.modules.player.Nofalls.impl.AAC5NoFall;
import my.distance.module.modules.player.Nofalls.impl.SpoofGroundNoFall;

public class NoFall extends Module {
	public Mode mode = new Mode("Mode", "Mode", NoFallMode.values(), NoFallMode.SpoofGround);


	public NoFall() {
		super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
		super.addValues(mode);
	}

	@Override
	public void onEnable(){
		((NoFallMode)mode.getValue()).get().onEnable();
	}

	@EventHandler
	private void onUpdate(EventPreUpdate e) {
		super.setSuffix(mode.getValue());
		if (mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
				|| mc.thePlayer.motionY >= 0.0d)
			return;
		if (Criticals.mode.getValue().equals(Criticals.CritMode.NoGround) && ModuleManager.getModuleByClass(Criticals.class).isEnabled()) {
			return;
		}
		((NoFallMode)mode.getValue()).get().onUpdate(e);

	}

	@EventHandler
	public void onPacket(EventPacketSend e) {
		((NoFallMode)mode.getValue()).get().onPacketSend(e);
	}

	public enum NoFallMode {
		SpoofGround(new SpoofGroundNoFall()),
		AAC5(new AAC5NoFall());

		final NofallModule nofallModule;
		NoFallMode(NofallModule nofallModuleIn){
			nofallModule = nofallModuleIn;
		}

		public NofallModule get() {
			return nofallModule;
		}
	}
}
