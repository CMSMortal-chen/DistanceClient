package my.distance.module.modules.render;


import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.value.Mode;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.render.RenderUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class ItemEsp extends Module {
	public static Option outlinedboundingBox = new Option("OutlinedBoundingBox", "OutlinedBoundingBox", false);
	public static Option boundingBox = new Option("BoundingBox", "BoundingBox", true);
    public static Mode heigh = new Mode("Height", "Height", height.values(), height.High);
	public ItemEsp() {
		super("ItemESP", new String[]{"ItemESP"}, ModuleType.Render);
        this.addValues(outlinedboundingBox, boundingBox, heigh);
	}
	
	@EventHandler
	public void onRender(EventRender3D event) {
		for (Object o : mc.theWorld.loadedEntityList) {
    		if (!(o instanceof EntityItem)) continue;
    		EntityItem item = (EntityItem)o;
 		   	double var10000 = item.posX;
 		   	double x = var10000 - mc.getRenderManager().renderPosX;
 		   	var10000 = item.posY + 0.5D;
 		   	double y = var10000 - mc.getRenderManager().renderPosY;
 		   	var10000 = item.posZ;
 		   	double z = var10000 - mc.getRenderManager().renderPosZ;
 		   	GL11.glEnable(3042);
 		   	GL11.glLineWidth(2.0F);
 		   	GL11.glColor4f(1, 1, 1, .75F);
 		   	GL11.glDisable(3553);
 		   	GL11.glDisable(2929);
 		   	GL11.glDepthMask(false);
            if(outlinedboundingBox.getValue() && heigh.getValue()==height.High) {
 	   		RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y-0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
 	   		}
            if(outlinedboundingBox.getValue() && heigh.getValue()==height.Low) {
 	   		RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
 	   		}
 	   		GL11.glColor4f(1, 1, 1, 0.15f);
            if(boundingBox.getValue() && heigh.getValue()==height.High) {
 	   		RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y-0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
 	   		}
 	   		GL11.glColor4f(1, 1, 1, 0.15f);
            if(boundingBox.getValue() && heigh.getValue()==height.Low) {
 	   		RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
 	   		}
 	   		GL11.glEnable(3553);
 	   		GL11.glEnable(2929);
 	   		GL11.glDepthMask(true);
 	   		GL11.glDisable(3042);
    	}
	}
	enum height{
		High,
		Low
	}
}
