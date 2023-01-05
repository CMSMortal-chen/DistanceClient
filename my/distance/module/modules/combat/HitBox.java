
package my.distance.module.modules.combat;

import java.awt.Color;

import my.distance.api.value.Numbers;
import my.distance.module.Module;
import my.distance.module.ModuleType;


public class HitBox
extends Module {
	public static Numbers<Double> Size = new Numbers<>("Size", "Size", 0.0, 0.0, 5.0, 0.1);
    public HitBox() {
        super("HitBox", new String[]{"HitBox"}, ModuleType.Combat);
        this.setColor(new Color(208, 30, 142).getRGB());
        super.addValues(Size);
    }
}


