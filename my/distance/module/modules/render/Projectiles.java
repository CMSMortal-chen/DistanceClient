package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Projectiles extends Module {

    public Projectiles() {
        super("Projectiles", new String[]{"Projectiles"}, ModuleType.Render);
    }

    private MovingObjectPosition blockCollision;

    @EventHandler
    public void onRender3D(EventRender3D eventRender3D) {
        if(mc.thePlayer.inventory.getCurrentItem() != null) {
            EntityPlayerSP player = mc.thePlayer;
            ItemStack stack = player.inventory.getCurrentItem();
            if (isThrowable(mc.thePlayer.getHeldItem().getItem())) {
                double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - Math.cos(Math.toRadians(player.rotationYaw)) * 0.16F;
                double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks + player.getEyeHeight() - 0.1D;
                double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - Math.sin(Math.toRadians(player.rotationYaw)) * 0.16F;
                double itemBow = stack.getItem() instanceof ItemBow ? 1.0F : 0.4F;

                double yaw = Math.toRadians(player.rotationYaw);
                double pitch = Math.toRadians(player.rotationPitch);

                double trajectoryX = -Math.sin(yaw) * Math.cos(pitch) * itemBow;
                double trajectoryY = -Math.sin(pitch) * itemBow;
                double trajectoryZ =  Math.cos(yaw) * Math.cos(pitch) * itemBow;
                double trajectory = Math.sqrt(trajectoryX * trajectoryX + trajectoryY * trajectoryY + trajectoryZ * trajectoryZ);

                trajectoryX /= trajectory;
                trajectoryY /= trajectory;
                trajectoryZ /= trajectory;

                if (stack.getItem() instanceof ItemBow) {
                    float bowPower = (72000 - player.getItemInUseCount()) / 20.0F;
                    bowPower = (bowPower * bowPower + bowPower * 2.0F) / 3.0F;
                    if (bowPower > 1.0F)
                    {
                        bowPower = 1.0F;
                    }
                    bowPower *= 3.0F;
                    trajectoryX *= bowPower;
                    trajectoryY *= bowPower;
                    trajectoryZ *= bowPower;
                } else {
                    trajectoryX *= 1.5D;
                    trajectoryY *= 1.5D;
                    trajectoryZ *= 1.5D;
                }

                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glLineWidth(2.0F);
                double gravity = stack.getItem() instanceof ItemBow ? 0.05D : 0.03D;
                GL11.glColor4f(0.3F, 0.6F, 1.0F, 0.5F);
                GL11.glBegin(GL11.GL_LINE_STRIP);

                for (int i = 0; i < 2000; i++) {
                    GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);

                    posX += trajectoryX * 0.1D;
                    posY += trajectoryY * 0.1D;
                    posZ += trajectoryZ * 0.1D;

                    trajectoryX *= 0.999D;
                    trajectoryY *= 0.999D;
                    trajectoryZ *= 0.999D;

                    trajectoryY = (trajectoryY - gravity * 0.1D);
                    Vec3 vec = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                    blockCollision = mc.theWorld.rayTraceBlocks(vec, new Vec3(posX, posY, posZ));

                    for (Entity o : mc.theWorld.getLoadedEntityList()) {
                        if (o instanceof EntityLivingBase && !(o instanceof EntityPlayerSP)) {
                            EntityLivingBase entity = (EntityLivingBase) o;
                            AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(0.3D, 0.3D, 0.3D);
                            MovingObjectPosition entityCollision = entityBoundingBox.calculateIntercept(vec, new Vec3(posX, posY, posZ));

                            if (entityCollision != null) {
                                blockCollision = entityCollision;
                            }

                            if (entityCollision != null) {
                                GL11.glColor4f(1.0F, 0.4F, 0.4F, 0.5F);
                            }

                            if (entityCollision != null) {
                                blockCollision = entityCollision;
                            }
                        }
                    }
                    if (blockCollision != null) {
                        break;
                    }
                }
                GL11.glEnd();
                double renderX = posX - mc.getRenderManager().renderPosX;
                double renderY = posY - mc.getRenderManager().renderPosY;
                double renderZ = posZ - mc.getRenderManager().renderPosZ;
                GL11.glPushMatrix();
                GL11.glTranslated(renderX - 0.5D, renderY - 0.5D, renderZ - 0.5D);
                AxisAlignedBB aim;
                switch (blockCollision.sideHit.getIndex()) {
                    case 2:
                    case 3:
                        GlStateManager.rotate(90, 1, 0, 0);
                        aim = new AxisAlignedBB(0.0D, 0.5D, -1.0D, 1.0D, 0.45D, 0.0D);
                        break;

                    case 4:
                    case 5:
                        GlStateManager.rotate(90, 0, 0, 1);
                        aim = new AxisAlignedBB(0.0D, -0.5D, 0.0D, 1.0D, -0.45D, 1.0D);
                        break;

                    default:
                        aim = new AxisAlignedBB(0.0D, 0.5, 0.0D, 1.0D, 0.45D, 1.0D);
                        break;
                }

                drawBox(aim);
                func_181561_a(aim);
                GL11.glPopMatrix();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glPopMatrix();
            }
        }
    }

    public static void func_181561_a(AxisAlignedBB p_181561_0_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }

    @EventHandler
    public void onRender3DArrowESP(EventRender3D eventRender3D) {
        for (Entity e2 : mc.theWorld.loadedEntityList) {
            if (!(e2 instanceof EntityArrow)) {
                continue;
            }
            EntityArrow arrow = (EntityArrow) e2;
            if (arrow.inGround) {
                continue;
            }

            final double viewerPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
            final double viewerPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
            final double viewerPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
            final double line_lenght = 0.25;
            final BlockPos pos = arrow.getPosition();
            final double x = -(viewerPosX - pos.getX()) + 0.5;
            final double y = -(viewerPosY - pos.getY()) + 0.5;
            final double z = -(viewerPosZ - pos.getZ()) + 0.5;
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(1.0f);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(true);
            GL11.glColor3f(255.0f, 255.0f, 1.0f);
            GL11.glBegin(1);
            //    for (double[] position : positions) {


            GL11.glVertex3d(x - line_lenght, y, z);
            GL11.glVertex3d(x + line_lenght, y, z);
            GL11.glVertex3d(x, y + line_lenght, z);
            GL11.glVertex3d(x, y - line_lenght, z);
            GL11.glVertex3d(x, y, z + line_lenght);
            GL11.glVertex3d(x, y, z - line_lenght);
            //       }
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            
            double posX = arrow.posX;
            double posY = arrow.posY;
            double posZ = arrow.posZ;
            double motionX = arrow.motionX;
            double motionY = arrow.motionY;
            double motionZ = arrow.motionZ;
            MovingObjectPosition landingPosition2;
            boolean hasLanded2 = false;
            Projectiles.enableRender3D(true);
            this.setColor(3196666);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            int limit2 = 0;
            while (!hasLanded2 && limit2 < 300) {
                Vec3 posBefore2 = new Vec3(posX, posY, posZ);
                Vec3 posAfter2 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition2 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(posBefore2, posAfter2, false, true, false);
                if (landingPosition2 != null) {
                    hasLanded2 = true;
                }
                if ((Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(posX += motionX, posY += motionY, posZ += motionZ)).getBlock()).getMaterial() == Material.water) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                } else {
                    motionX *= 0.99;
                    motionY *= 0.99;
                    motionZ *= 0.99;
                }
                motionY -= 0.05000000074505806;

                GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
                ++limit2;
            }
            GL11.glEnd();
            Projectiles.disableRender3D(true);
        }
    }

    public void drawBox(AxisAlignedBB bb2) {
        GL11.glBegin(7);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glEnd();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private double getGravity(Item item) {
        return item instanceof ItemBow ? 0.05D : 0.03D;
    }

    private boolean isThrowable(Item item) {
        return item instanceof ItemBow || item instanceof ItemSnowball
                || item instanceof ItemEgg || item instanceof ItemEnderPearl;
    }
}
