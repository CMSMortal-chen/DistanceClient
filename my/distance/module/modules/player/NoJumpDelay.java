package my.distance.module.modules.player;

import my.distance.module.Module;
import my.distance.module.ModuleType;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NojumpDelay", new String[]{"nojumodelay"}, ModuleType.Player);
    }
}
