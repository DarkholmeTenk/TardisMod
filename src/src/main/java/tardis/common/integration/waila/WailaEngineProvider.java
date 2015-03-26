package tardis.common.integration.waila;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.store.TwoIntStore;
import tardis.common.tileents.EngineTileEntity;

public class WailaEngineProvider extends AbstractWailaProvider
{
	{
		controlNames.put(new TwoIntStore(0), "Owner display");
		controlNames.put(new TwoIntStore(1), "Hull gauge");
		controlNames.put(new TwoIntStore(2), "Shields gauge");
		controlNames.put(new TwoIntStore(3), "Currently selected player");
		controlNames.put(new TwoIntStore(4), "Next player");
		controlNames.put(new TwoIntStore(5), "Previous player");
		controlNames.put(new TwoIntStore(6), "Player modification permission");
		controlNames.put(new TwoIntStore(7), "Toggle player modification permission");
		controlNames.put(new TwoIntStore(10), "Upgrade energy level");
		controlNames.put(new TwoIntStore(11), "Upgrade energy regeneration level");
		controlNames.put(new TwoIntStore(12), "Upgrade max rooms level");
		controlNames.put(new TwoIntStore(13), "Upgrade shields level");
		controlNames.put(new TwoIntStore(20), "Energy level gauge");
		controlNames.put(new TwoIntStore(21), "Energy regen level gauge");
		controlNames.put(new TwoIntStore(22), "Max rooms level gauge");
		controlNames.put(new TwoIntStore(23), "Shields level gauge");
		controlNames.put(new TwoIntStore(30), "Unspent points gauge");
		controlNames.put(new TwoIntStore(39), "Screwdriver slot");
		controlNames.put(new TwoIntStore(40,49), "Screwdriver permissions buttons");
		controlNames.put(new TwoIntStore(50,59), "Screwdriver permissions lights");
		controlNames.put(new TwoIntStore(60), "TARDIS Landing Pad lockdown");
		controlNames.put(new TwoIntStore(70), "Console Room setting");
		controlNames.put(new TwoIntStore(71), "Prev Console Room");
		controlNames.put(new TwoIntStore(72), "Next Console Room");
		controlNames.put(new TwoIntStore(73), "Switch Console Room");
	}
	
	private boolean isEngine(IWailaDataAccessor accessor)
	{
		if(accessor.getBlock()== TardisMod.tardisEngineBlock || (accessor.getBlock()==TardisMod.schemaComponentBlock && accessor.getMetadata() == 7))
			return true;
		return false;
	}
	
	@Override
	public String[] extraInfo(IWailaDataAccessor accessor, int control)
	{
		return null;
	}

	@Override
	public int getControlHit(IWailaDataAccessor accessor)
	{
		if(isEngine(accessor))
		{
			World w = accessor.getWorld();
			if(w == null)
				return -1;
			EngineTileEntity e = Helper.getTardisEngine(w);
			if(e != null)
			{
				MovingObjectPosition pos = accessor.getPosition();
				if(pos.hitVec.yCoord <= e.yCoord+1.5)
					return e.getControlFromHit(pos.blockX,pos.blockY,pos.blockZ,pos.hitVec, accessor.getPlayer());
			}
		}
		return -1;
	}

}
