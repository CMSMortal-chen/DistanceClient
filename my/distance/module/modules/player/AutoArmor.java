package my.distance.module.modules.player;



import my.distance.api.EventHandler;
import my.distance.api.events.World.EventTick;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.time.TimerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class AutoArmor extends Module {

	public static Numbers<Double> DELAY = new Numbers<>("DELAY", "DELAY", 1.0D, 0.0D, 10.0D, 1.0D);
	public static Mode MODE = new Mode("MODE", "MODE", EMode.values(), EMode.Basic);

	private TimerUtil timer = new TimerUtil();
    public AutoArmor() {
		super("AutoArmor", new String[] { "AutoArmor" }, ModuleType.Player);
		super.addValues(DELAY,MODE);
    }

    @EventHandler

    public void onEvent(EventTick event) {
        this.setSuffix(MODE.getValue());
    	if(ModuleManager.getModuleByClass(InvCleaner.class).isEnabled())
    		return;
    	long delay = DELAY.getValue().longValue()*50;
        if(MODE.getValue() == EMode.OpenInv && !(mc.currentScreen instanceof GuiInventory)){
        	return;
        }
        if(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat){
        	if(timer.hasReached(delay)){
        		getBestArmor();
        	}
        }
    }
    
    public void getBestArmor(){
    	for(int type = 1; type < 5; type++){
    		if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
    			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
    			if(isBestArmor(is, type)){
    				continue;
    			}else{
    				if( this.MODE.getValue() == EMode.FakeInv){
        				C16PacketClientStatus p = new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT);
        				mc.thePlayer.sendQueue.addToSendQueue(p);
    				}
    				drop(4 + type);
    			}
    		}
    		for (int i = 9; i < 45; i++) {
    			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
    				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
    				if(isBestArmor(is, type) && getProtection(is) > 0){
    					shiftClick(i);
    					timer.reset();
    					if(this.DELAY.getValue().longValue() > 0)
    						return;
    				}
    			}
            }
        }
    }
    public static boolean isBestArmor(ItemStack stack, int type){
    	float prot = getProtection(stack);
    	String strType = "";
    	if(type == 1){
    		strType = "helmet";
    	}else if(type == 2){
    		strType = "chestplate";
    	}else if(type == 3){
    		strType = "leggings";
    	}else if(type == 4){
    		strType = "boots";
    	}
    	if(!stack.getUnlocalizedName().contains(strType)){
    		return false;
    	}
    	for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                	return false;
            }
        }
    	return true;
    }
    public void shiftClick(int slot){
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public void drop(int slot){
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
    public static float getProtection(ItemStack stack){
    	float prot = 0;
    	if ((stack.getItem() instanceof ItemArmor)) {
    		ItemArmor armor = (ItemArmor)stack.getItem();
    		prot += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D;
    		prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack)/100d;
    		prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack)/100d;
    		prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)/100d;
    		prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)/50d;   	
    		prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack)/100d;   	
    	}
	    return prot;
    }
    public static enum EMode {
    	Basic, OpenInv,FakeInv;
    }
}
