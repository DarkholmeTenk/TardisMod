package tardis.common.integration.waila;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.store.TwoIntStore;
import tardis.common.tileents.EngineTileEntity;

public class WailaEngineProvider extends AbstractWailaProvider
{
	{
		addControl(new TwoIntStore(0), "Owner display","Shows who the owner of this TARDIS is");
		addControl(new TwoIntStore(1), "Hull gauge","Shows the level of the hull");
		addControl(new TwoIntStore(2), "Shields gauge","Shows the level of the shields");
		addControl(new TwoIntStore(3), "Currently selected player","The player whose permissions are being modified");
		addControl(new TwoIntStore(4), "Next player","Select the next online player");
		addControl(new TwoIntStore(5), "Previous player","Select the previous online player");
		addControl(new TwoIntStore(10), "Upgrade energy level","Upgrades the Maximum Artron Energy of the TARDIS");
		addControl(new TwoIntStore(11), "Upgrade energy regeneration level","Upgrades the rate at which Artron Energy is generated");
		addControl(new TwoIntStore(12), "Upgrade shields level","Increases the maximum Shield points");
		addControl(new TwoIntStore(13), "Upgrade max rooms level","Increases the maximum number of rooms");
		addControl(new TwoIntStore(20), "Energy level gauge","Shows the current Energy upgrade level");
		addControl(new TwoIntStore(21), "Energy regen level gauge","Shows the current Energy Regen upgrade level");
		addControl(new TwoIntStore(22), "Shields level gauge","Shows the current Shields upgrade level");
		addControl(new TwoIntStore(23), "Max rooms level gauge","Shows the current Max Rooms upgrade level");
		addControl(new TwoIntStore(30), "Unspent points gauge","Shows how many upgrade points are left to spend");
		addControl(new TwoIntStore(39), "Screwdriver slot");
		addControl(new TwoIntStore(40,49), "Screwdriver permissions buttons","Allows you to toggle permissions on a screwdriver");
		addControl(new TwoIntStore(50,59), "Screwdriver permissions lights","Displays which permissions a screwdriver has");
		addControl(new TwoIntStore(60), "Landing Pad lockdown");
		addControl(new TwoIntStore(70), "Console Room setting");
		addControl(new TwoIntStore(71), "Prev Console Room");
		addControl(new TwoIntStore(72), "Next Console Room");
		addControl(new TwoIntStore(73), "Switch Console Room","Right click then sneak right click to change console room");
		for(TardisPermission p : TardisPermission.values())
		{
			int o = p.ordinal();
			addControl(new TwoIntStore(80+o), "Toggle permission: " + p.name);
			addControl(new TwoIntStore(90+o), "Permission light: " + p.name);
		}
		addControl(new TwoIntStore(100), "Engine Panel Release","Opens or closes the engine panel");
		addControl(new TwoIntStore(101,108), "Upgrade Slot","Allows you to insert upgrades");
		addControl(new TwoIntStore(110), "Master Damage Repair Unit");
		addControl(new TwoIntStore(111,119), "Damage Repair Unit");
		addControl(130, "Spawn Protection Lever", "Allows you to vary the radius of spawn prevention");
		addControl(131, "Screwdriver Style Button", "Allows you to change your Screwdriver's style");
	}

	private boolean isEngine(IWailaDataAccessor accessor)
	{
		if((accessor.getBlock()== TardisMod.tardisEngineBlock) || ((accessor.getBlock()==TardisMod.schemaComponentBlock) && (accessor.getMetadata() == 7)))
			return true;
		return false;
	}

	private void addControl(int i, String name, String desc)
	{
		addControl(new TwoIntStore(i), name, desc);
	}

	@Override
	public String[] extraInfo(IWailaDataAccessor accessor, int control)
	{
		if(isEngine(accessor))
		{
			World w = accessor.getWorld();
			EngineTileEntity eng = Helper.getTardisEngine(w);
			if(eng != null)
				return eng.getExtraInfo(control);
		}
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
				if(pos.hitVec.yCoord <= (e.yCoord+1.5))
					return e.getControlFromHit(pos.blockX,pos.blockY,pos.blockZ,pos.hitVec, accessor.getPlayer());
			}
		}
		return -1;
	}

}
