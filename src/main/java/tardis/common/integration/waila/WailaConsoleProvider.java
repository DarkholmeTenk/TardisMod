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
		controlNames.put(new TwoIntStore(0), "Energy Gauge");
		controlNames.put(new TwoIntStore(1), "Rooms Counter");
		controlNames.put(new TwoIntStore(2), "Speedometer");
		controlNames.put(new TwoIntStore(3), "Facing Wheel");
		controlNames.put(new TwoIntStore(4), "Speed Lever");
		controlNames.put(new TwoIntStore(5), "Screwdriver Button");
		controlNames.put(new TwoIntStore(6,7), "Screwdriver Slot");
		controlNames.put(new TwoIntStore(8), "XP Gauge");
		controlNames.put(new TwoIntStore(9), "Shields");
		controlNames.put(new TwoIntStore(10,16), "X Control");
		controlNames.put(new TwoIntStore(20,26), "Z Control");
		controlNames.put(new TwoIntStore(30,33), "Y Control");
		controlNames.put(new TwoIntStore(34), "Land Ground Control");
		controlNames.put(new TwoIntStore(40), "Temporal Primer");
		controlNames.put(new TwoIntStore(41), "Helmic Regulator");
		controlNames.put(new TwoIntStore(42), "Quantum Handbrake");
		controlNames.put(new TwoIntStore(50), "Prev Schema Button");
		controlNames.put(new TwoIntStore(51), "Next Schema Button");
		controlNames.put(new TwoIntStore(52), "Interior Temporal Control");
		controlNames.put(new TwoIntStore(53), "Relative Coords Switch");
		controlNames.put(new TwoIntStore(54), "External Scanner");
		controlNames.put(new TwoIntStore(55), "Uncoordinated Flight Control");
		controlNames.put(new TwoIntStore(56), "Flight Stabilizer");
		controlNames.put(new TwoIntStore(57), "Prev Category Button");
		controlNames.put(new TwoIntStore(58), "Next Category Button");
		controlNames.put(new TwoIntStore(60), "Dimension Lever");
		controlNames.put(new TwoIntStore(100), "Coordinate Guesser");
		controlNames.put(new TwoIntStore(900), "Coordinate Save/Load Mode Control");
		controlNames.put(new TwoIntStore(901), "Room Deletion Control");
		controlNames.put(new TwoIntStore(902), "Last Coordinates Store");
		controlNames.put(new TwoIntStore(903), "Current Coordinates Store");
		controlNames.put(new TwoIntStore(904), "Landing Pad Control");
		controlNames.put(new TwoIntStore(1000,1019), "Save Slots");
		controlNames.put(new TwoIntStore(1020,1032), "Flight Controls");
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
