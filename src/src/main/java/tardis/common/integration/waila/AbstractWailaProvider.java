package tardis.common.integration.waila;

import java.util.HashMap;
import java.util.List;

import tardis.common.core.TardisOutput;
import tardis.common.core.store.TwoIntStore;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public abstract class AbstractWailaProvider implements IWailaDataProvider
{
	protected HashMap<TwoIntStore,String> controlNames = new HashMap<TwoIntStore,String>();
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
	
	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		int control = getControlHit(accessor);
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
			String[] extra = extraInfo(accessor,control);
			if(extra != null)
			{
				for(String extraString : extra)
					currenttip.add(extraString);
			}
			if(!f)
				TardisOutput.print("TWCP", "Control not found:" + control);
		}
		return currenttip;
	}
	public abstract String[] extraInfo(IWailaDataAccessor accessor, int control);
	
	public abstract int getControlHit(IWailaDataAccessor accessor);

}
