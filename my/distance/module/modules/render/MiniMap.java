package my.distance.module.modules.render;

import my.distance.Client;
import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender2D;
import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.fastuni.FastUniFontRenderer;
import my.distance.manager.FriendManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.misc.StringConversions;
import my.distance.util.render.Blur;
import my.distance.util.render.Colors;
import my.distance.util.render.Colors2;
import my.distance.util.render.RenderUtil;
import my.distance.util.time.Timer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;


public class MiniMap
extends Module {
    private boolean dragging;
    private final Numbers<Double> scale = new Numbers<>("Scale", "Scale", 2.0, 1.0, 5.0, 0.1);
    private final Numbers<Double> x = new Numbers<>("X", "X", 0.0, 1.0, 1920.0, 1.0);
    private final Numbers<Double> y = new Numbers<>("Y", "Y", 80.0, 1.0, 1080.0, 1.0);
    private final Numbers<Double> size = new Numbers<>("Size", "Size", 50.0, 50.0, 500.0, 1.0);

    private final Option blur = new Option("Blur",true);
    public Mode mode = new Mode("Mode", "mode", RadarMode.values(), RadarMode.Normal);

    public MiniMap() {
        super("Radar", new String[]{"Radar", "minimap"}, ModuleType.Render);
        this.addValues(this.scale, this.x, this.y, this.size,blur, this.mode);
    }

    @EventHandler
    public void onGui(EventRender2D e) {
        if (this.mode.getValue() == RadarMode.Normal) {
            int size = this.size.getValue().intValue();
            ScaledResolution sr = new ScaledResolution(mc);
            int size1 = this.size.getValue().intValue();
            float xOffset = this.x.getValue().floatValue();
            float yOffset = this.y.getValue().floatValue();
            float playerOffsetX = (float) mc.thePlayer.posX;
            float playerOffSetZ = (float) mc.thePlayer.posZ;
            int var141 = sr.getScaledWidth();
            int var151 = sr.getScaledHeight();
            int mouseX = Mouse.getX() * var141 / mc.displayWidth;
            int mouseY = var151 - Mouse.getY() * var151 / mc.displayHeight - 1;
            if ((float) mouseX >= xOffset && (float) mouseX <= xOffset + (float) size1 && (float) mouseY >= yOffset - 3.0f && (float) mouseY <= yOffset + 10.0f && Mouse.getEventButton() == 0) {
                this.dragging = !this.dragging;
            }
            if (this.dragging && mc.currentScreen instanceof GuiChat) {
                Object newValue = StringConversions.castNumber(Double.toString(mouseX - size1 / 2f), 5);
                this.x.setValue((Double) newValue);
                Object newValueY = StringConversions.castNumber(Double.toString(mouseY - 2), 5);
                this.y.setValue((Double) newValueY);
            } else {
                this.dragging = false;
            }
            if (blur.get()) {
                Blur.blurAreaBoarderXY((int) xOffset, (int) yOffset, (int) (xOffset + size), (int) (yOffset + size));
            }
            RenderUtil.drawRect(xOffset, yOffset, (xOffset + size), (yOffset + size), RenderUtil.reAlpha(Colors.WHITE.c, blur.get()?0.2f:0.4f));

            RenderUtil.drawRect(xOffset + ((size / 2f) - 0.5f), yOffset + 3.5f, xOffset + (size / 2f) + 0.5f, (yOffset + (float) size) - 3.5f, new Color(235,235,235,120).getRGB());
            RenderUtil.drawRect(xOffset + 3.5f, yOffset + ((size / 2f) - 0.5f), (xOffset + (float) size) - 3.5f, yOffset + ((size / 2f) + 0.5f), new Color(235,235,235,120).getRGB());

            for (Object o : mc.theWorld.getLoadedEntityList()) {
                EntityPlayer ent;
                if (!(o instanceof EntityPlayer) || !(ent = (EntityPlayer) o).isEntityAlive() || ent == mc.thePlayer || ent.isInvisible() || ent.isInvisibleToPlayer(mc.thePlayer))
                    continue;
                float pTicks = mc.timer.renderPartialTicks;
                float posX = (float) ((ent.posX + (ent.posX - ent.lastTickPosX) * (double) pTicks - (double) playerOffsetX) * this.scale.getValue());
                float posZ = (float) ((ent.posZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - (double) playerOffSetZ) * this.scale.getValue());
                int color = mc.thePlayer.canEntityBeSeen(ent) ? new Color(255, 110, 110).getRGB() : new Color(160, 160, 160).getRGB();
                float cos = MathHelper.cos(mc.thePlayer.rotationYaw * 0.017453292519943295f);
                float sin = MathHelper.sin(mc.thePlayer.rotationYaw * 0.017453292519943295d);
                float rotY = -(posZ * cos - posX * sin);
                float rotX = -(posX * cos + posZ * sin);
                if (rotY > (float) (size / 2 - 5)) {
                    rotY = (float) (size / 2) - 5.0F;
                } else if (rotY < (float) (-(size / 2 - 5))) {
                    rotY = (float) (-(size / 2 - 5));
                }

                if (rotX > (float) (size / 2) - 5.0F) {
                    rotX = (float) (size / 2 - 5);
                } else if (rotX < (float) (-(size / 2 - 5))) {
                    rotX = -((float) (size / 2) - 5.0F);
                }

                RenderUtil.circle((xOffset + (size / 2f) + rotX), (yOffset + (size / 2f) + rotY), 1.5f, color);
            }
        }
        if (this.mode.getValue() == RadarMode.Round) {
            Timer timer = new Timer();
            ScaledResolution sr = new ScaledResolution(this.mc);
            int size = this.size.getValue().intValue();
            float xOffset = this.x.getValue().floatValue();
            float yOffset = this.y.getValue().floatValue();
            float playerOffsetX = (float) mc.thePlayer.posX;
            float playerOffSetZ = (float) mc.thePlayer.posZ;
            Gui.drawFilledCircle(xOffset + (size / 2f), yOffset + size / 2f, size / 2f - 4, Colors2.getColor(50, 100), 0);
            GlStateManager.pushMatrix();
            GlStateManager.translate(xOffset + size / 2f, yOffset + size / 2f, 0);
            GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0, 0, 1);
            RenderUtil.rectangle((-0.5), -size / 2f + 4, (0.5), size / 2f - 4, Colors2.getColor(255, 80));
            RenderUtil.rectangle(-size / 2f + 4, (-0.5), size / 2f - 4, (+0.5),
                    Colors2.getColor(255, 80));
            GlStateManager.popMatrix();

            RenderUtil.drawCircle(xOffset + (size / 2f), yOffset + size / 2f, size / 2f - 4, 72, Colors2.getColor(0, 200));

            FastUniFontRenderer normal = Client.FontLoaders.Chinese18;
            float angle2 = -mc.thePlayer.rotationYaw + 90;
            float x2 = (float) ((size / 2f + 4) * Math.cos(Math.toRadians(angle2))) + xOffset + size / 2f; // angle is in radians
            float y2 = ((size / 2f + 4) * MathHelper.sin(Math.toRadians(angle2))) + yOffset + size / 2f;
            normal.drawStringWithShadow("N", x2 - normal.getStringWidth("N") / 2f, y2 - 1, -1);
            x2 = (float) ((size / 2f + 4) * Math.cos(Math.toRadians(angle2 + 90))) + xOffset + size / 2f; // angle is in radians
            y2 = ((size / 2f + 4) * MathHelper.sin(Math.toRadians(angle2 + 90))) + yOffset + size / 2f;
            normal.drawStringWithShadow("E", x2 - normal.getStringWidth("E") / 2f, y2 - 1, -1);
            x2 = (float) ((size / 2f + 4) * Math.cos(Math.toRadians(angle2 + 180))) + xOffset + size / 2f; // angle is in radians
            y2 = ((size / 2f + 4) * MathHelper.sin(Math.toRadians(angle2 + 180))) + yOffset + size / 2f;
            normal.drawStringWithShadow("S", x2 - normal.getStringWidth("S") / 2f, y2 - 1, -1);
            x2 = (float) ((size / 2f + 4) * Math.cos(Math.toRadians(angle2 - 90))) + xOffset + size / 2f; // angle is in radians
            y2 = ((size / 2f + 4) * MathHelper.sin(Math.toRadians(angle2 - 90))) + yOffset + size / 2f;
            normal.drawStringWithShadow("W", x2 - normal.getStringWidth("W") / 2f, y2 - 1, -1);

            int var141 = sr.getScaledWidth();
            int var151 = sr.getScaledHeight();
            final int mouseX = Mouse.getX() * var141 / mc.displayWidth;
            final int mouseY = var151 - Mouse.getY() * var151 / mc.displayHeight - 1;
            if (mouseX >= xOffset && mouseX <= xOffset + size && mouseY >= yOffset - 3 && mouseY <= yOffset + 10 && Mouse.getEventButton() == 0) {
                timer.reset();
                dragging = !dragging;
            }
            if (dragging && mc.currentScreen instanceof GuiChat) {
                Object newValue = (StringConversions.castNumber(Double.toString(mouseX - size / 2f), 5));
                x.setValue((Double) newValue);
                Object newValueY = (StringConversions.castNumber(Double.toString(mouseY - 2), 5));
                y.setValue((Double) newValueY);
            } else {
                dragging = false;
            }

            for (Object o : mc.theWorld.getLoadedEntityList()) {
                if (o instanceof EntityPlayer) {
                    EntityPlayer ent = (EntityPlayer) o;
                    if (ent.isEntityAlive() && ent != mc.thePlayer && !(ent.isInvisible() || ent.isInvisibleToPlayer(mc.thePlayer))) {

                        float pTicks = mc.timer.renderPartialTicks;
                        float posX = (float) (((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks) -
                                playerOffsetX) * ((Number) scale.getValue()).doubleValue());

                        float posZ = (float) (((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks) -
                                playerOffSetZ) * ((Number) scale.getValue()).doubleValue());
                        int color;
                        if (FriendManager.isFriend(ent.getName())) {
                            color = Colors2.getColor(0, 195, 255);
                        } else {
                            color = mc.thePlayer.canEntityBeSeen(ent) ? Colors2.getColor(255, 0, 0)
                                    : Colors2.getColor(255, 255, 0);
                        }

                        float cos = MathHelper.cos((float) (mc.thePlayer.rotationYaw * (Math.PI * 2 / 360)));
                        float sin = MathHelper.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                        float rotY = -(posZ * cos - posX * sin);
                        float rotX = -(posX * cos + posZ * sin);
                        float var7 = 0 - rotX;
                        float var9 = 0 - rotY;
                        if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) > size / 2f - 4) {
                            float angle = findAngle(0, rotX, 0, rotY);
                            float x = (float) ((size / 2f) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2f; // angle is in radians
                            float y = ((size / 2f) * MathHelper.sin(Math.toRadians(angle))) + yOffset + size / 2f;
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(x, y, 0);
                            GlStateManager.rotate(angle, 0, 0, 1);
                            GlStateManager.scale(1.5f, 0.5, 0.5);
                            RenderUtil.drawCircle(0, 0, 1.5f, 3, Colors2.getColor(46));
                            RenderUtil.drawCircle(0, 0, 1, 3, color);
                            GlStateManager.popMatrix();
                        } else {
                            RenderUtil.rectangleBordered(xOffset + (size / 2f) + rotX - 1.5,
                                    yOffset + (size / 2f) + rotY - 1.5, xOffset + (size / 2f) + rotX + 1.5,
                                    yOffset + (size / 2f) + rotY + 1.5, 0.5, color, Colors2.getColor(46));
                        }
                    }
                }
            }


        }
    }

    public void onDisable() {
        super.onDisable();
    }

    public void onEnable() {
        super.isEnabled();
    }

    private float findAngle(float x, float x2, float y, float y2) {
        return (float) (Math.atan2(y2 - y, x2 - x) * 180 / Math.PI);
    }

    public enum RadarMode {
        Normal,
        Round;
    }
}
