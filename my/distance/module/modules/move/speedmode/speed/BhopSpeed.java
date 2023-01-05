package my.distance.module.modules.move.speedmode.speed;

import my.distance.module.modules.move.speedmode.SpeedModule;
import my.distance.util.entity.MoveUtils;
import my.distance.util.world.BlockUtils;
import my.distance.api.events.World.*;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class BhopSpeed extends SpeedModule {
    @Override
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        if (mc.thePlayer.moveForward == 0.0f && mc.thePlayer.moveStrafing == 0.0f) {
            movementSpeed = MoveUtils.defaultSpeed();
        }
        if (stage == 1 && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
            movementSpeed = 1.35 + MoveUtils.defaultSpeed() - 0.01;
        }
        if (!BlockUtils.isInLiquid() && stage == 2 && mc.thePlayer.isCollidedVertically && MoveUtils.isOnGround(0.01) && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
            if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump))
                e.setY(mc.thePlayer.motionY = 0.41999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
            else
                e.setY(mc.thePlayer.motionY = 0.41999998688698);
            mc.thePlayer.jump();
            movementSpeed *= 1.533D;
        } else if (stage == 3) {
            final double difference = 0.66 * (distance - MoveUtils.defaultSpeed());
            movementSpeed = distance - difference;
        } else {
            final List<AxisAlignedBB> collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
            if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
            }
            movementSpeed = distance - distance / 159.0;
        }
        movementSpeed = Math.max(movementSpeed, MoveUtils.defaultSpeed());

        //Stage checks if you're greater than 0 as step sets you -6 stage to make sure the player wont flag.
        if (stage > 0) {
            //Set strafe motion.
            if (BlockUtils.isInLiquid())
                movementSpeed = 0.1;
            setMotion(e, movementSpeed);
        }
        //If the player is moving, step the stage up.
        if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
            ++stage;
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
}
