package my.distance.module.modules.render;

import my.distance.api.EventHandler;
import my.distance.api.events.Render.EventRender3D;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.module.Module;
import my.distance.module.ModuleType;
import my.distance.util.misc.liquidbounce.LiquidRender;
import my.distance.util.time.TimerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OreESP extends Module {
    private final Numbers<Double> Range = new Numbers<>("Range",40d, 5d, 120d,1d);
    private final Option Iron = new Option("Iron",false);
    private final Option Gold = new Option("Gold",false);
    private final Option Diamond = new Option("Diamond",false);
    private final Option Coal = new Option("Coal",false);
    private final Option Redstone = new Option("Redstone",false);
    private final Option Lapis = new Option("Lapis",false);
    private final Option Emerald = new Option("Emerald",false);
    private final Option OutLine = new Option("Outline",false);

    private Thread BlockFinderThread = null;
    private final my.distance.util.time.TimerUtil TimerUtil = new TimerUtil();
    private final CopyOnWriteArrayList<BlockPos> BlockList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<BlockPos> RenderBlockList = new CopyOnWriteArrayList<>();

    public OreESP(){
        super("OreESP",new String[]{"oreesp"}, ModuleType.Render);
        addValues(Range,Iron,Gold,Diamond,Coal,Redstone,Lapis,Emerald,OutLine);
    }
    @EventHandler
    public void onUpdate(EventPreUpdate e){
        if (TimerUtil.hasReached(1000L) && !(BlockFinderThread != null && BlockFinderThread.isAlive())){
            BlockFinderThread = new Thread(() -> {
                BlockList.clear();
                for (int x = -Range.getValue().intValue(); x < Range.getValue(); x++) {
                    for (int y = Range.getValue().intValue(); y > -Range.getValue(); y--) {
                        for (int z = -Range.getValue().intValue(); z < Range.getValue(); z++) {
                            BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore) && Diamond.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore) && Iron.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore) && Gold.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore) && Coal.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore) && Redstone.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore) && Lapis.getValue()){
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore) && Emerald.getValue()){
                                BlockList.add(pos);
                            }
                        }
                    }
                }
                TimerUtil.reset();
                synchronized(RenderBlockList) {
                    RenderBlockList.clear();
                    RenderBlockList.addAll(BlockList);
                }
            },"OreESP-FinderThread");
            BlockFinderThread.start();
        }
    }
    @EventHandler
    public void onRender3D(EventRender3D event) {
        for (final BlockPos pos: RenderBlockList){
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore) && Diamond.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(54, 194, 255,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore) && Iron.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(255, 192, 115,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore) && Gold.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(255, 221, 0,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore) && Coal.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(50, 50, 50,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore) && Redstone.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(255, 73, 73,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore) && Lapis.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(0, 42, 255,50),OutLine.getValue());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore) && Emerald.getValue()){
                LiquidRender.drawBlockBox(pos,new Color(103, 255, 48,50),OutLine.getValue());
            }
        }
    }
}
