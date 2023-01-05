/*
 * Decompiled with CFR 0_132.
 */
package my.distance.module.modules.player;

import my.distance.Client;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.module.modules.combat.KillAura;
import my.distance.module.modules.world.Scaffold;
import my.distance.util.time.Timer;
import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Mode;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class AntiAim
extends Module {
	   float[] lastAngles;
	   public static float rotationPitch;
	   private boolean fake;
	   private boolean fake1;
       public static byte var4 = -1;
       public static float pitchDown;
       public static float lastMeme;
       public static float reverse;
       public static float sutter;
       private static float lastP;
       Minecraft var10000;
	   Timer fakeJitter = new Timer();
	   private final Mode AAYAW = new Mode("AAYAW", "AAYAW", YAW.values(), YAW.FakeJitter);
	   private final Mode AAPITCH = new Mode("AAPITCH", "AAPITCH", PITCH.values(), PITCH.HalfDown);

    public AntiAim() {
        super("AntiAim", new String[]{"AntiAim"}, ModuleType.Player);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.addValues(AAYAW,AAPITCH);
    }
    public void updateAngles(float yaw, float pitch) {
        if(mc.gameSettings.thirdPersonView != 0) {
           Client.Pitch = pitch;
           mc.thePlayer.rotationYawHead = yaw;
           mc.thePlayer.renderYawOffset = yaw;
        }

     }

    @Override
    public void onDisable() {
       Client.Pitch = lastP;
       this.fake1 = true;
       this.lastAngles = null;
       rotationPitch = 0.0F;
       mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;

       super.onDisable();
    }

    @Override
    public void onEnable() {
        lastP = Client.Pitch;
       this.fake1 = true;
       this.lastAngles = null;
       rotationPitch = 0.0F;
       super.onEnable();
    }

    @EventHandler
    public void onEvent(EventPreUpdate event) {
    	EventPreUpdate em = (EventPreUpdate)event;
    	Scaffold Scaffold = (Scaffold) ModuleManager.getModuleByClass(Scaffold.class);
          if( KillAura.currentTarget == null&& !Scaffold.isEnabled()) {
             if(this.lastAngles == null) {
                float[] var10001 = new float[2];
                Minecraft var10004 = mc;
                var10001[0] = mc.thePlayer.rotationYaw;
                var10004 = mc;
                var10001[1] = mc.thePlayer.rotationPitch;
                this.lastAngles = var10001;
             }

             this.fake = !this.fake;
            

             if(this.AAYAW.getValue() == YAW.Jitter) {
                 var4 = 0;
              }

              if(this.AAYAW.getValue() == YAW.SpinFast) {
                 var4 = 7;
              }

              if(this.AAYAW.getValue() == YAW.SpinSlow) {
                 var4 = 8;
              }

              if(this.AAYAW.getValue() == YAW.Freestanding) {
                 var4 = 6;
              }

              if(this.AAYAW.getValue() == YAW.Reverse) {
                 var4 = 2;
              }

              if(this.AAYAW.getValue() == YAW.FakeJitter) {
                 var4 = 4;
              }

              if(this.AAYAW.getValue() == YAW.Lisp) {
                 var4 = 1;
              }

              if(this.AAYAW.getValue() == YAW.Sideways) {
                 var4 = 3;
              }

              if(this.AAYAW.getValue() == YAW.FakeHead) {
                 var4 = 5;
              }
          


             switch(var4) {
             case 0:
                pitchDown = 0.0F;
                em.setYaw(pitchDown = this.lastAngles[0] + 90.0F);
                this.lastAngles = new float[]{pitchDown, this.lastAngles[1]};
                this.updateAngles(pitchDown, this.lastAngles[1]);
                var10000 = mc;
                mc.thePlayer.renderYawOffset = pitchDown;
                var10000 = mc;
                mc.thePlayer.prevRenderYawOffset = pitchDown;
                break;
             case 1:
                lastMeme = this.lastAngles[0] + 150000.0F;
                this.lastAngles = new float[]{lastMeme, this.lastAngles[1]};
                em.setYaw(lastMeme);
                this.updateAngles(lastMeme, this.lastAngles[1]);
                break;
             case 2:
                var10000 = mc;
                reverse = mc.thePlayer.rotationYaw + 180.0F;
                this.lastAngles = new float[]{reverse, this.lastAngles[1]};
                em.setYaw(reverse);
                this.updateAngles(reverse, this.lastAngles[1]);
                break;
             case 3:
                var10000 = mc;
                sutter = mc.thePlayer.rotationYaw + -90.0F;
                this.lastAngles = new float[]{sutter, this.lastAngles[1]};
                em.setYaw(sutter);
                this.updateAngles(sutter, this.lastAngles[1]);
                break;
             case 4:
                if(this.fakeJitter.delay(350)) {
                   this.fake1 = !this.fake1;
                   this.fakeJitter.reset();
                }

                var10000 = mc;
                float yawRight = mc.thePlayer.rotationYaw + (float)(this.fake1?90:-90);
                this.lastAngles = new float[]{yawRight, this.lastAngles[1]};
                em.setYaw(yawRight);
                this.updateAngles(yawRight, this.lastAngles[1]);
                break;
             case 5:
                if(this.fakeJitter.delay(1100)) {
                   this.fake1 = !this.fake1;
                   this.fakeJitter.reset();
                }

                var10000 = mc;
                float yawFakeHead = mc.thePlayer.rotationYaw + (float)(this.fake1?90:-90);
                if(this.fake1) {
                   this.fake1 = false;
                }

                this.lastAngles = new float[]{yawFakeHead, this.lastAngles[1]};
                em.setYaw(yawFakeHead);
                this.updateAngles(yawFakeHead, this.lastAngles[1]);
                break;
             case 6:
                var10000 = mc;
                float freestandHead = (float)((double)(mc.thePlayer.rotationYaw + 5.0F) + Math.random() * 175.0D);
                this.lastAngles = new float[]{freestandHead, this.lastAngles[1]};
                em.setYaw(freestandHead);
                this.updateAngles(freestandHead, this.lastAngles[1]);
                break;
             case 7:
                float yawSpinFast = this.lastAngles[0] + 45.0F;
                this.lastAngles = new float[]{yawSpinFast, this.lastAngles[1]};
                em.setYaw(yawSpinFast);
                this.updateAngles(yawSpinFast, this.lastAngles[1]);
                break;
             case 8:
                float yawSpinSlow = this.lastAngles[0] + 10.0F;
                this.lastAngles = new float[]{yawSpinSlow, this.lastAngles[1]};
                em.setYaw(yawSpinSlow);
                this.updateAngles(yawSpinSlow, this.lastAngles[1]);
             }


             if(this.AAPITCH.getValue() == PITCH.Normal) {
                 var4 = 2;
              }

              if(this.AAPITCH.getValue() == PITCH.Reverse) {
                 var4 = 3;
              }

              if(this.AAPITCH.getValue() == PITCH.Stutter) {
                 var4 = 4;
              }

              if(this.AAPITCH.getValue() == PITCH.Up) {
                 var4 = 5;
              }

              if(this.AAPITCH.getValue() == PITCH.Meme) {
                 var4 = 1;
              }

              if(this.AAPITCH.getValue() == PITCH.Zero) {
                 var4 = 6;
              }

              if(this.AAPITCH.getValue() == PITCH.HalfDown) {
                 var4 = 0;
              }
             

             switch(var4) {
             case 0:
                pitchDown = 90.0F;
                this.lastAngles = new float[]{this.lastAngles[0], pitchDown};
                em.setPitch(pitchDown);
                this.updateAngles(this.lastAngles[0], pitchDown);
                break;
             case 1:
                lastMeme = this.lastAngles[1];
                lastMeme += 10.0F;
                if(lastMeme > 90.0F) {
                   lastMeme = -90.0F;
                }

                this.lastAngles = new float[]{this.lastAngles[0], lastMeme};
                em.setPitch(lastMeme);
                this.updateAngles(this.lastAngles[0], lastMeme);
                break;
             case 2:
                this.updateAngles(this.lastAngles[0], mc.thePlayer.rotationPitch);
                break;
             case 3:
                var10000 = mc;
                reverse = mc.thePlayer.rotationPitch + 180.0F;
                this.lastAngles = new float[]{this.lastAngles[0], reverse};
                em.setPitch(reverse);
                this.updateAngles(this.lastAngles[0], reverse);
                break;
             case 4:
                if(this.fake) {
                   sutter = 90.0F;
                   em.setPitch(sutter);
                } else {
                   sutter = -45.0F;
                   em.setPitch(sutter);
                }

                this.lastAngles = new float[]{this.lastAngles[0], sutter};
                this.updateAngles(this.lastAngles[0], sutter);
                break;
             case 5:
                this.lastAngles = new float[]{this.lastAngles[0], -90.0F};
                em.setPitch(-90.0F);
                this.updateAngles(this.lastAngles[0], -90.0F);
                break;
             case 6:
                this.lastAngles = new float[]{this.lastAngles[0], -179.0F};
                em.setPitch(-180.0F);
                this.updateAngles(this.lastAngles[0], -179.0F);
             }
          }
    }

    
    static enum YAW {
    	Reverse, Jitter, Lisp, SpinSlow, SpinFast, Sideways, FakeJitter, FakeHead, Freestanding;
    }static enum PITCH {
    	Normal, HalfDown, Zero, Up, Stutter, Reverse, Meme
    }
}

