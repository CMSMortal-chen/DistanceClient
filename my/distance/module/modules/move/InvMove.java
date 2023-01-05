package my.distance.module.modules.move;

import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.api.EventHandler;
import my.distance.api.events.World.EventPacketSend;
import my.distance.api.events.World.EventTick;
import my.distance.api.value.Option;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class InvMove extends Module {

    public static Option AACP = new Option("AACP", "AACP", Boolean.FALSE);
    boolean inInventory = false;
    public InvMove() {
        super("InvMove", new String[]{"InvMove", "crit"}, ModuleType.Movement);
        this.setColor(new Color(235, 194, 138).getRGB());
        this.addValues(AACP);
    }
    @EventHandler
    public void onPacket(EventPacketSend e){
        Packet<?> packet = e.getPacket();
        if(packet instanceof C0BPacketEntityAction){
            C0BPacketEntityAction p = (C0BPacketEntityAction)packet;
            if(p.getAction() == Action.START_SPRINTING && inInventory && AACP.getValue())
                e.setCancelled(true);
        }
    }
    @EventHandler
    public void onTick(EventTick event) {
        if (mc.currentScreen instanceof GuiChat) {
            return;
        }
        if (mc.currentScreen != null) {
            KeyBinding[] moveKeys = new KeyBinding[]{
                    mc.gameSettings.keyBindForward,
                    mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft,
                    mc.gameSettings.keyBindRight,
                    mc.gameSettings.keyBindJump
            };
            for (KeyBinding bind : moveKeys){
                KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
            }
            if(!inInventory){
                if(AACP.getValue()){
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
                }
                inInventory = true;
            }
        }else{
            if(inInventory){
                if(AACP.getValue()){
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                }
                inInventory = false;
            }
        }
    }
}
