package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.math.MathUtil;
import my.distance.api.events.World.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class HiveSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        if (canZoom() && this.stage == 1) {
            this.movementSpeed = 1.56D * MathUtil.getBaseMovementSpeed() - 0.01D;
            mc.timer.timerSpeed = 1.05F;
            GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
            if (gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY = 0.418698442D;
                e.setY(0.418698442D);
            }
        } else if (canZoom() && this.stage == 2) {
            mc.thePlayer.motionY = 0.3599D;
            e.setY(0.3599D);
            this.movementSpeed *= 1.58D;
            mc.timer.timerSpeed = 1.0F;
        } else if (this.stage == 3) {
            double difference = 0.66D * (this.distance - MathUtil.getBaseMovementSpeed());
            this.movementSpeed = this.distance - difference;
            mc.timer.timerSpeed = 1.1F;
        } else {
            List<AxisAlignedBB> collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D));
            if (collidingList.size() > 0 || (mc.thePlayer.isCollidedVertically && this.stage > 0)) {
                this.stage = mc.thePlayer.moving() ? 1 : 0;
            }
            this.movementSpeed = this.distance - this.distance / 159.0D;
        }
        this.movementSpeed = Math.max(this.movementSpeed, MathUtil.getBaseMovementSpeed());
        mc.thePlayer.setMoveSpeed(e, this.movementSpeed);
        if (mc.thePlayer.moving()) {
            this.stage++;
        }
    }

    @Override
    public void onPost(EventPostUpdate e) {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }
    @Override
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
