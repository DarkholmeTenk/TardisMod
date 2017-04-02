package tardis.common.integration.waila;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tardis.common.TMRegistry;
import tardis.common.tileents.TardisTileEntity;

public class WailaTardisProvider extends AbstractWailaProvider
{

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity te = null;
		if(accessor.getBlock() == TMRegistry.tardisBlock)
			te = accessor.getTileEntity();
		else if(accessor.getBlock() == TMRegistry.tardisTopBlock)
		{
			World w = accessor.getWorld();
			MovingObjectPosition p = accessor.getPosition();
			te = w.getTileEntity(p.blockX, p.blockY-1,p.blockZ);
		}
		if(te instanceof TardisTileEntity)
		{
			TardisTileEntity tte = (TardisTileEntity)te;
			if(tte.owner != null)
				currenttip.add("Owner: " + tte.owner);
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public String[] extraInfo(IWailaDataAccessor accessor, int control)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getControlHit(IWailaDataAccessor accessor)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
