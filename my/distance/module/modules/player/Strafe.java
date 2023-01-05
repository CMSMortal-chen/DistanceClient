
package my.distance.module.modules.player;

import java.awt.Color;

import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.entity.PlayerUtil;


public class Strafe
extends Module {

    public Strafe() {
        super("Strafe", new String[]{"Strafe"}, ModuleType.Movement);
        this.setColor(new Color(208, 30, 142).getRGB());
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (PlayerUtil.MovementInput()) {
            PlayerUtil.setSpeed((double)PlayerUtil.getSpeed());
        }
    }
    }


