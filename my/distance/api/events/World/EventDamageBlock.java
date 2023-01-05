package my.distance.api.events.World;



import my.distance.api.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class EventDamageBlock extends Event {

		   public BlockPos pos;
		   public EnumFacing fac;
		   public boolean cal;
			
		    public EventDamageBlock(BlockPos p_180512_1_, EnumFacing p_180512_2_) {
		    	pos=p_180512_1_;
		    	fac=p_180512_2_;
		    	cal=false;
		    }

		    public BlockPos getpos() {
			return pos;
		    }
		    
		    public EnumFacing getfac() {
		    	return fac;
		        }

			public boolean isCancelled() {
				return cal;
			}
			
			public void setCancelled(boolean s) {
				cal=s;
			}}
		    

