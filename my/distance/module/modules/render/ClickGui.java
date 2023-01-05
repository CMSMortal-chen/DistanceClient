package my.distance.module.modules.render;

import my.distance.api.value.Mode;
import my.distance.api.value.Option;
import my.distance.api.verify.SHWID;
import my.distance.manager.Fucker;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.manager.RenderManager;
import my.distance.ui.gui.clikguis.ClickUi.ClickUi;
import my.distance.util.sound.SoundFxPlayer;
import my.distance.ui.gui.clikguis.clickgui3.ClientClickGui;

public class ClickGui extends Module {
	private static final Mode mode = new Mode("Mode", "mode", ClickGui.modes.values(), modes.Distance);
	public static final Option CustomColor = new Option("CustomColor",false);
	public ClickGui() {
		super("ClickGui", new String[] { "clickui" }, ModuleType.Render);
		this.addValues(mode,CustomColor);
	}

	@Override
	public void onEnable() {
        if (SHWID.BITCH != 0){
            RenderManager.doRender();
        }
        if (SHWID.hahaha != 1){
            Fucker.dofuck();
        }
        if (SHWID.id != 1){
            RenderManager.doRender();
            Fucker.dofuck();
        }
        if (SHWID.id2 != 2){
            RenderManager.doRender();
            Fucker.dofuck();
        }
        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.ClickGuiOpen,-4);
	    switch ((modes)mode.getValue()){
            case Nov:{
                mc.displayGuiScreen(new ClickUi());
                break;
            }
            case Azlips:{
                mc.displayGuiScreen(new my.distance.ui.gui.clikguis.clickgui4.ClickGui());
                break;
            }
            case Distance:{
                mc.displayGuiScreen(new ClientClickGui());
                break;
            }
        }
		this.setEnabled(false);
	}
    enum modes{
    	Nov,
        Distance,
		Azlips
    }
}
