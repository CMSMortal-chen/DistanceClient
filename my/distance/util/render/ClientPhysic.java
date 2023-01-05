
package my.distance.util.render;

import my.distance.util.time.TimerUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ClientPhysic {
	public static Random random = new Random();
	public static Minecraft mc = Minecraft.getMinecraft();
	public static RenderItem renderItem = mc.getRenderItem();
	public static long tick;
	public static double rotation;
	public static final ResourceLocation RES_ITEM_GLINT;
	public static final TimerUtil delayTimer = new TimerUtil();

	static {
		RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	}

	public static void doRenderItemPhysic(Entity par1Entity, double x, double y, double z, float par8, float par9) {
		EntityItem item;
		ItemStack itemstack;
		rotation = (double) (System.nanoTime() - tick) / 3000000.0;
		if (!ClientPhysic.mc.inGameHasFocus) {
			rotation = 0.0;
		}
		if ((itemstack = (item = (EntityItem) par1Entity).getEntityItem()).getItem() != null) {
			random.setSeed(187L);
			boolean flag = false;
			ClientPhysic.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			ClientPhysic.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture)
					.setBlurMipmap(false, false);
			flag = true;
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc((int) 516, (float) 0.1f);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 1, (int) 0);
			GlStateManager.pushMatrix();
			IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(itemstack);
			int i = ClientPhysic.func_177077_a(item, x, y - 0.10000000149011612, z, par9, ibakedmodel);
			BlockPos pos = new BlockPos((Entity) item);
			if (item.rotationPitch > 360.0f) {
				item.rotationPitch = 0.0f;
			}
			item.getAir();
			if (!(item.getPosition() == null)) {
				if (item.onGround) {
					if (item.rotationPitch != 0.0f && item.rotationPitch != 90.0f && item.rotationPitch != 180.0f
							&& item.rotationPitch != 270.0f) {
						double Abstand0 = ClientPhysic.formPositiv(item.rotationPitch);
						double Abstand90 = ClientPhysic.formPositiv(item.rotationPitch - 90.0f);
						double Abstand180 = ClientPhysic.formPositiv(item.rotationPitch - 180.0f);
						double Abstand270 = ClientPhysic.formPositiv(item.rotationPitch - 270.0f);
						if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270) {
							if (item.rotationPitch < 0.0f) {
								EntityItem e1 = item;
								e1.rotationPitch = (float) ((double) e1.rotationPitch + rotation);
							} else {
								EntityItem e2 = item;
								e2.rotationPitch = (float) ((double) e2.rotationPitch - rotation);
							}
						}
						if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270) {
							if (item.rotationPitch - 90.0f < 0.0f) {
								EntityItem e3 = item;
								e3.rotationPitch = (float) ((double) e3.rotationPitch + rotation);
							} else {
								EntityItem e4 = item;
								e4.rotationPitch = (float) ((double) e4.rotationPitch - rotation);
							}
						}
						if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270) {
							if (item.rotationPitch - 180.0f < 0.0f) {
								EntityItem e5 = item;
								e5.rotationPitch = (float) ((double) e5.rotationPitch + rotation);
							} else {
								EntityItem e6 = item;
								e6.rotationPitch = (float) ((double) e6.rotationPitch - rotation);
							}
						}
						if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0) {
							if (item.rotationPitch - 270.0f < 0.0f) {
								EntityItem e7 = item;
								e7.rotationPitch = (float) ((double) e7.rotationPitch + rotation);
							} else {
								EntityItem e8 = item;
								e8.rotationPitch = (float) ((double) e8.rotationPitch - rotation);
							}
						}
					}
				} else {
					BlockPos posUp = new BlockPos((Entity) item);
					posUp.add(0.0, 0.20000000298023224, 0.0);
					Material m1 = item.worldObj.getBlockState(posUp).getBlock().getMaterial();
					Material m2 = item.worldObj.getBlockState(pos).getBlock().getMaterial();
					boolean m3 = item.isInsideOfMaterial(Material.water);
					boolean m4 = item.isInWater();
					if (m3 | m1 == Material.water | m2 == Material.water | m4) {
						EntityItem tmp748_746 = item;
						tmp748_746.rotationPitch = (float) ((double) tmp748_746.rotationPitch + rotation / 4.0);
					} else {
						EntityItem tmp770_768 = item;
						tmp770_768.rotationPitch = (float) ((double) tmp770_768.rotationPitch + rotation * 2.0);
					}
				}
			}
			GL11.glRotatef((float) item.rotationYaw, (float) 0.0f, (float) 1.0f, (float) 0.0f);
			GL11.glRotatef((float) (item.rotationPitch + 90.0f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
			for (int j = 0; j < i; ++j) {
				if (ibakedmodel.isAmbientOcclusion()) {
					GlStateManager.pushMatrix();
					GlStateManager.scale((float) 0.7f, (float) 0.7f, (float) 0.7f);
					renderItem.renderItem(itemstack, ibakedmodel);
					GlStateManager.popMatrix();
					continue;
				}
				GlStateManager.pushMatrix();
				if (j > 0 && ClientPhysic.shouldSpreadItems()) {
					GlStateManager.translate((float) 0.0f, (float) 0.0f, (float) (0.046875f * (float) j));
				}
				renderItem.renderItem(itemstack, ibakedmodel);
				if (!ClientPhysic.shouldSpreadItems()) {
					GlStateManager.translate((float) 0.0f, (float) 0.0f, (float) 0.046875f);
				}
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			ClientPhysic.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			if (flag) {
				ClientPhysic.mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture)
						.restoreLastBlurMipmap();
			}
		}
	}

	public static int func_177077_a(EntityItem itemIn, double x, double y, double z, float p_177077_8_,
			IBakedModel p_177077_9_) {
		ItemStack itemstack = itemIn.getEntityItem();
		Item item = itemstack.getItem();
		boolean var12 = p_177077_9_.isAmbientOcclusion();
		int var13 = RenderEntityItem.func_177078_a(itemstack);
		if (!(item instanceof ItemBlock))
			GlStateManager.translate((float) x, (float) y + 0.1, (float) z);
		else
			GlStateManager.translate((float) x, (float) y + 0.2, (float) z);

		float var16;

		float pitch = itemIn.onGround ? 90 : itemIn.rotationPitch;

		if(delayTimer.hasReached(5)) {
			itemIn.rotationPitch += 1;
		}

		if (itemIn.rotationPitch > 180)
			itemIn.rotationPitch = -180;

		GlStateManager.rotate(pitch, 1, 0, 0);

		GlStateManager.rotate(itemIn.rotationYaw, 0, 0, 1);

		if (!var12) {
			var16 = -0.0F * (float) (var13 - 1) * 0.5F;
			float var17 = -0.0F * (float) (var13 - 1) * 0.5F;
			float var18 = -0.046875F * (float) (var13 - 1) * 0.5F;
			GlStateManager.translate(var16, var17, var18);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		return var13;
	}

	public static int getModelCount(ItemStack stack) {
		int b0 = 1;
		if (stack.animationsToGo > 48) {
			b0 = 5;
		} else if (stack.animationsToGo > 32) {
			b0 = 4;
		} else if (stack.animationsToGo > 16) {
			b0 = 3;
		} else if (stack.animationsToGo > 1) {
			b0 = 2;
		}
		return b0;
	}

	public static byte getMiniBlockCount(ItemStack stack, byte original) {
		return original;
	}

	public static byte getMiniItemCount(ItemStack stack, byte original) {
		return original;
	}

	public static boolean shouldSpreadItems() {
		return true;
	}

	public static double formPositiv(float rotationPitch) {
		if (rotationPitch > 0.0f) {
			return rotationPitch;
		}
		return -rotationPitch;
	}
}
