package my.distance.ui.particles.particle;

import java.util.Random;

import my.distance.util.render.gl.GLUtils;
import my.distance.util.SuperLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class ParticleSnow extends Particle {
   private Random random = new Random();
   private ScaledResolution res;
   private int sizes = this.random.nextInt(4)+3;

   public void draw(int xAdd) {
      this.prepare();
      this.move();
      this.drawPixel(xAdd);
      this.resetPos();
   }

   private void prepare() {
      this.res = new ScaledResolution(Minecraft.getMinecraft());
   }

   private void drawPixel(int xAdd) {
//      float size = 10.0F;
      GLUtils.startSmooth();
//      for(int i = 0; i < 10; ++i) {
//         int alpha = 0;
//         Gui.drawFilledCircle(this.vector.x, this.vector.y, size + 1.0F + (float)i * 0.2F, SuperLib.reAlpha(Colors.WHITE.c, (float)alpha),5);
//      }

      Gui.drawFilledCircle(this.vector.x + (float)xAdd, this.vector.y, sizes, SuperLib.reAlpha(-1, 0.1F),5);
      GLUtils.endSmooth();
   }

   private void move() {
      float speed = 100.0F;
      this.vector.y -= this.random.nextFloat() + Math.max(vector.y / 80f,3f);
      this.vector.x -= this.random.nextFloat() + Math.max(vector.x / 250f,3f);
   }

   private void resetPos() {
      if(this.vector.x < -10.0F) {
         this.vector.x = (float)this.res.getScaledWidth() + 10;
      }

      if(this.vector.y < -10.0F) {
         sizes = this.random.nextInt(4)+3;
         this.vector.y = (float)this.res.getScaledHeight()+ 10;
      }

   }
}
