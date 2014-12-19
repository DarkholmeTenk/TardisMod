package tardis.common.integration.waila;

import java.util.HashMap;
import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.TwoIntStore;
import tardis.common.tileents.TardisConsoleTileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class TardisWailaConsoleProvider implements IWailaDataProvider
{
	
	private static HashMap<TwoIntStore,String> controlNames = new HashMap<TwoIntStore,String>();

	static
	{
		controlNames.put(new TwoIntStore(0), "Energy Gauge");
		controlNames.put(new TwoIntStore(1), "Rooms Counter");
		controlNames.put(new TwoIntStore(2), "Speedometer");
		controlNames.put(new TwoIntStore(3), "Facing Wheel");
		controlNames.put(new TwoIntStore(4), "Speed Lever");
		controlNames.put(new TwoIntStore(5), "Screwdriver Button");
		controlNames.put(new TwoIntStore(6,7), "Screwdriver Slot");
		controlNames.put(new TwoIntStore(8), "XP Gauge");
		controlNames.put(new TwoIntStore(10,15), "X Control");
		controlNames.put(new TwoIntStore(20,25), "Z Control");
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
		controlNames.put(new TwoIntStore(60), "Dimension Lever");
		controlNames.put(new TwoIntStore(100), "Coordinate Guesser");
		controlNames.put(new TwoIntStore(900), "Coordinate Save/Load Mode Control");
		controlNames.put(new TwoIntStore(901), "Room Deletion Control");
		controlNames.put(new TwoIntStore(902), "Last Coordinates Store");
		controlNames.put(new TwoIntStore(903), "Current Coordinates Store");
		controlNames.put(new TwoIntStore(904), "Landing Pad Control");
		controlNames.put(new TwoIntStore(1000,1019), "Save Slots");
		controlNames.put(new TwoIntStore(1020,1022), "Flight Controls");
	}
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}
	
	private boolean isConsole(IWailaDataAccessor accessor)
	{
		Block b = accessor.getBlock();
		if(b == TardisMod.schemaComponentBlock)
		{
			int metaData = accessor.getMetadata();
			return (metaData==3 || metaData == 6);
		}
		return false;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		if(isConsole(accessor))
		{
			World w = accessor.getWorld();
			TardisConsoleTileEntity con = Helper.getTardisConsole(w);
			if(con != null)
			{
				MovingObjectPosition pos = accessor.getPosition();
				if(pos.hitVec.yCoord > con.yCoord+1.5)
					return currenttip;
				int control = con.getControlFromHit(pos.blockX,pos.blockY,pos.blockZ,pos.hitVec, accessor.getPlayer());
				if(control != -1)
				{
					boolean f = false;
					for(TwoIntStore store : controlNames.keySet())
					{
						if(store.within(control))
						{
							f = true;
							currenttip.add("Control: "+controlNames.get(store));
						}
					}
					String[] extra = con.getExtraInfo(control);
					if(extra != null)
					{
						for(String extraString : extra)
							currenttip.add(extraString);
					}
					if(!f)
						TardisOutput.print("TWCP", "Control not found:" + control);
				}
			}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		// TODO Auto-generated method stub
		return currenttip;
	}

}
