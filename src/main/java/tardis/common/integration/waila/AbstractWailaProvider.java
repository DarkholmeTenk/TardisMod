package tardis.common.integration.waila;

import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import tardis.common.core.store.TwoIntStore;
import tardis.core.console.control.AbstractControl;

public abstract class AbstractWailaProvider implements IWailaDataProvider
{
	protected HashMap<TwoIntStore,String> controlNames = new HashMap<>();
	protected HashMap<TwoIntStore,String> controlText = new HashMap<>();

	public void addControl(TwoIntStore key, String name, String extra)
	{
		controlNames.put(key, name);
		controlText.put(key, extra);
	}

	public void addControl(TwoIntStore key, String name)
	{
		controlNames.put(key, name);
	}

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
		AbstractControl control = getControlHit(accessor);
		if(control != null)
		{
			boolean f = false;
			control.addManualText(currenttip);
//			for(TwoIntStore store : controlNames.keySet())
//				if(store.within(control))
//				{
//					f = true;
//					currenttip.add("Control: "+controlNames.get(store));
//					break;
//				}
//			String[] extra = extraInfo(accessor,control);
//			if(extra != null)
//				for(String extraString : extra)
//					currenttip.add(extraString);
//			if(accessor instanceof DummyWailaAccessor) //If we're using the handheld manual
//				for(TwoIntStore store : controlText.keySet())
//					if(store.within(control))
//					{
//						currenttip.add("");
//						currenttip.add("- " + controlText.get(store));
//						break;
//					}
		}
		return currenttip;
	}
	public abstract String[] extraInfo(IWailaDataAccessor accessor, int control);

	public abstract AbstractControl getControlHit(IWailaDataAccessor accessor);

}
