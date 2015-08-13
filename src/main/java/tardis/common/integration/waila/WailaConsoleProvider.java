package tardis.common.integration.waila;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.store.TwoIntStore;
import tardis.common.tileents.ConsoleTileEntity;

public class WailaConsoleProvider extends AbstractWailaProvider
{
	{
		addControl(new TwoIntStore(0), "Energy Gauge","Displays how much Artron Energy is available.");
		addControl(new TwoIntStore(1), "Rooms Counter","Displays how many rooms you have.");
		addControl(new TwoIntStore(2), "Speedometer","Displays the current speed after rooms have been taken into account.");
		addControl(new TwoIntStore(3), "Facing Wheel","Controls which way the TARDIS will face when landing.");
		addControl(new TwoIntStore(4), "Speed Lever","Controls the max speed at which you can fly");
		addControl(new TwoIntStore(5), "Screwdriver Button","Generates a new screwdriver or absorbs an old screwdriver");
		addControl(new TwoIntStore(6,7), "Screwdriver Slot");
		addControl(new TwoIntStore(8), "XP Gauge","Displays how much XP remaining until the next TARDIS level.");
		addControl(new TwoIntStore(10,16), "X Control");
		addControl(new TwoIntStore(20,26), "Z Control");
		addControl(new TwoIntStore(30,33), "Y Control");
		addControl(new TwoIntStore(34), "Land Ground Control","Controls whether the TARDIS can land in mid air");
		addControl(new TwoIntStore(40), "Temporal Primer","The first part of the Takeoff sequence");
		addControl(new TwoIntStore(41), "Helmic Regulator","The second part of the Takeoff sequence");
		addControl(new TwoIntStore(42), "Quantum Handbrake","The third and final part of the Takeoff sequence");
		addControl(new TwoIntStore(50), "Prev Schema Button","Selects the previous schema in the current category");
		addControl(new TwoIntStore(51), "Next Schema Button","Selects the next schema in the current category");
		addControl(new TwoIntStore(52), "Interior Temporal Control","Controls the internal projected time");
		addControl(new TwoIntStore(53), "Relative Coords Switch","Switches between using absolute (x=10 means you go to x=10) or relative (x=10 means you move 10 blocks to x) controls");
		addControl(new TwoIntStore(54), "External Scanner","Gives a basic idea of what is outside the TARDIS");
		addControl(new TwoIntStore(55), "Uncoordinated Flight Control","Switches between drifting (never landing) and coordinated (going to a destination) flight");
		addControl(new TwoIntStore(56), "Flight Stabilizer","Prevents buttons from becoming unstable, allowing unsupervised (low xp) flights");
		addControl(new TwoIntStore(57), "Prev Category Button","Selects the previous schema category");
		addControl(new TwoIntStore(58), "Next Category Button","Selects the next schema category");
		addControl(new TwoIntStore(60), "Dimension Lever","Controls which dimension the TARDIS will land in");
		addControl(new TwoIntStore(100), "Coordinate Guesser","Attempts to calculate where the TARDIS will land");
		addControl(new TwoIntStore(900), "Coordinate Save/Load Mode Control","Controls whether pressing a save slot will save to or load that slot");
		addControl(new TwoIntStore(901), "Room Deletion Control","Deletes all non-console rooms in the TARDIS");
		addControl(new TwoIntStore(902), "Last Coordinates Store","Sets the coordinates to the last place you landed");
		addControl(new TwoIntStore(903), "Current Coordinates Store","Sets the coordinates to the current location");
		addControl(new TwoIntStore(904), "Landing Pad Control","Sets whether the TARDIS will attempt to find a landing pad to land on or not");
		addControl(new TwoIntStore(1000,1019), "Save Slots");
		addControl(new TwoIntStore(1020,1032), "Flight Controls","Controls which may need to be pressed during flight. Increased speed increases difficulty");
	}

	private boolean isConsole(IWailaDataAccessor accessor)
	{
		Block b = accessor.getBlock();
		if(b == TardisMod.schemaComponentBlock)
		{
			int metaData = accessor.getMetadata();
			return ((metaData==3) || (metaData == 6));
		}
		return false;
	}

	@Override
	public int getControlHit(IWailaDataAccessor accessor)
	{
		if(isConsole(accessor))
		{
			World w = accessor.getWorld();
			ConsoleTileEntity con = Helper.getTardisConsole(w);
			if(con != null)
			{
				MovingObjectPosition pos = accessor.getPosition();
				if(pos.hitVec.yCoord <= (con.yCoord+1.5))
					return con.getControlFromHit(pos.blockX,pos.blockY,pos.blockZ,pos.hitVec, accessor.getPlayer());
			}
		}
		return -1;
	}

	@Override
	public String[] extraInfo(IWailaDataAccessor accessor, int control)
	{
		if(isConsole(accessor))
		{
			World w = accessor.getWorld();
			ConsoleTileEntity con = Helper.getTardisConsole(w);
			if(con != null)
				return con.getExtraInfo(control);
		}
		return null;
	}
}
