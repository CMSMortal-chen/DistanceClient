package my.distance.util.math;

import my.distance.api.events.World.EventStrafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public final class Rotation {
    private float yaw;
    private float pitch;

    public void toPlayer(EntityPlayer player) {
        float var2 = this.yaw;
        if (!Float.isNaN(var2)) {
            var2 = this.pitch;
            if (!Float.isNaN(var2)) {
                this.fixedSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
                player.rotationYaw = this.yaw;
                player.rotationPitch = this.pitch;
            }
        }
    }

    public void fixedSensitivity(float sensitivity) {
        float f = sensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        this.yaw -= this.yaw % gcd;
        this.pitch -= this.pitch % gcd;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float var1) {
        this.yaw = var1;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float var1) {
        this.pitch = var1;
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation copy(float yaw, float pitch) {
        return new Rotation(yaw, pitch);
    }


    public void applyStrafeToPlayer(EventStrafe event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        int dif = (int) ((MathHelper.wrapAngleTo180_float(player.rotationYaw - this.yaw
                - 23.5f - 135)
                + 180) / 45);

        float yaw = this.yaw;

        float strafe = event.getStrafe();
        float forward = event.getForward();
        float friction = event.getFriction();

        float calcForward = 0f;
        float calcStrafe = 0f;

        switch (dif) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5:{
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
            }
        }

        if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f;
        }

        if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f;
        }

        float d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = MathHelper.sqrt_float(d);
            if (d < 1.0f) d = 1.0f;
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180f));
            float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180f));
            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }
    public String toString() {
        return "Rotation(yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
    }
}

