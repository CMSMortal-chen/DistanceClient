package my.distance.module.modules.world;

import my.distance.api.value.Mode;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class Teams extends Module {

	private static final Mode mode = new Mode("Mode",TeamsMode.values(),TeamsMode.Color);

	public Teams() {
		super("Teams", new String[] {}, ModuleType.World);
		addValues(mode);
	}

	public static boolean isOnSameTeam(Entity entity) {
		if (!ModuleManager.getModuleByClass(Teams.class).isEnabled())
			return false;
		switch ((TeamsMode) mode.getValue()) {
			case Armor: {
				if (entity instanceof EntityPlayer) {
					EntityPlayer entityPlayer = (EntityPlayer) entity;
					if (mc.thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
						ItemStack myHead = mc.thePlayer.inventory.armorInventory[3];
						ItemArmor myItemArmor = (ItemArmor) myHead.getItem();

						ItemStack entityHead = entityPlayer.inventory.armorInventory[3];
						ItemArmor entityItemArmor = (ItemArmor) myHead.getItem();

						if (myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead)) {
							return true;
						}
					}
				}
				break;
			}
			case Color: {
				if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
					if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
							|| entity.getDisplayName().getUnformattedText().length() <= 2) {
						return false;
					}
					return Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2)
							.equals(entity.getDisplayName().getUnformattedText().substring(0, 2));
				}
				break;
			}
			case ScoreBoard: {
				if (entity instanceof EntityPlayer) {
					return mc.thePlayer.getTeam().isSameTeam(((EntityPlayer) entity).getTeam());
				}
				break;
			}
		}
		return false;
	}
	enum TeamsMode{
		Color,
		Armor,
		ScoreBoard
	}
}
