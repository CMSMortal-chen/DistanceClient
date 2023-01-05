package my.distance.module.modules.render;


import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventPostRenderPlayer;
import my.distance.api.events.Render.EventPreRenderPlayer;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
	
	
    public Numbers<Double> visiblered = new Numbers<>("VisibleRed", "VisibleRed", 1.0, 0.001, 1.0, 0.001);
    public Numbers<Double> visiblegreen = new Numbers<>("VisibleGreen", "VisibleGreen", 0.0, 0.001, 1.0, 0.001);
    public Numbers<Double> visibleblue = new Numbers<>("VisibleBlue", "VisibleBlue", 0.0, 0.001, 1.0, 0.001);
    public Numbers<Double> hiddenred = new Numbers<>("HiddenRed", "HiddenRed", 1.0, 0.001, 1.0, 0.001);
    public Numbers<Double> hiddengreen = new Numbers<>("HiddenGreen", "HiddenGreen", 0.0, 0.001, 1.0, 0.001);
    public Numbers<Double> hiddenblue = new Numbers<>("HiddenBlue", "HiddenBlue", 1.0, 0.001, 1.0, 0.001);
    public Numbers<Double> alpha = new Numbers<>("Alpha", "Alpha", 1.0, 0.001, 1.0, 0.001);
    private Option players = new Option("Players","Players", true);
    private Option animals = new Option("Animals","Animals", true);
    private Option mobs = new Option("Mobs","Mobs", false);
    public Option invisibles = new Option("Invisibles","Invisibles", false);
    private Option passives = new Option("Passives","Passives", true);
    public Option colored = new Option("Colored","Colored", false);
    //public Option hands = new Option("Hands","Hands", false);
    public Option rainbow = new Option("Raindow","Raindow", false);

    public Chams() {
    	super("Chams", new String[]{"Chams"}, ModuleType.Render);
        addValues(visiblered, visiblegreen, visibleblue, hiddenred, hiddengreen, hiddenblue, alpha, players, animals, mobs, invisibles, passives,colored, rainbow);
    }
	@EventHandler
	private void preRenderPlayer(EventPreRenderPlayer e) {
		if(colored.getValue())return;
		GL11.glEnable((int) 32823);
		GL11.glPolygonOffset((float) 1.0f, (float) -1100000.0f);
	}

	@EventHandler
	private void postRenderPlayer(EventPostRenderPlayer e) {
		if(colored.getValue())return;
		GL11.glDisable((int) 32823);
		GL11.glPolygonOffset((float) 1.0f, (float) 1100000.0f);
	}
    public boolean isValid(EntityLivingBase entity) {
        return isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.getValue());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.getValue() && entity instanceof EntityPlayer) || (mobs.getValue() && (entity instanceof EntityMob || entity instanceof EntitySlime) || (passives.getValue() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.getValue() && entity instanceof EntityAnimal));
    }


}
