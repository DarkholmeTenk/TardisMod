package tardis.common.tileents.extensions;

import java.util.EnumSet;

import tardis.common.core.Helper;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LabRecipe
{
	public final ItemStack[] source;
	public final ItemStack[] dest;
	public final EnumSet<LabFlag> flags;
	public final int energyCost;
	
	public LabRecipe(ItemStack[] _source, ItemStack[] _dest, EnumSet<LabFlag> _flags, int _enCost)
	{
		source = _source;
		dest = _dest;
		flags = _flags;
		energyCost = _enCost;
	}

	public boolean isValid()
	{
		if(energyCost < 0)
			return false;
		if(source == null || dest == null || flags == null)
			return false;
		if(source.length == 0 || dest.length == 0 || source.length > 5 || dest.length > 5)
			return false;
		for(int i = 0;i<source.length;i++)
			if(source[i] == null)
				return false;
		for(int i = 0;i<dest.length;i++)
			if(dest[i] == null)
				return false;
		if(flags.contains(LabFlag.NOTINFLIGHT) && (flags.contains(LabFlag.INFLIGHT) || flags.contains(LabFlag.INCOORDINATEDFLIGHT) || flags.contains(LabFlag.INUNCOORDINATEDFLIGHT)))
			return false;
		if(flags.contains(LabFlag.INFLIGHT) && (flags.contains(LabFlag.INCOORDINATEDFLIGHT) || flags.contains(LabFlag.INUNCOORDINATEDFLIGHT)))
			return false;
		if(flags.contains(LabFlag.INCOORDINATEDFLIGHT) && flags.contains(LabFlag.INUNCOORDINATEDFLIGHT))
			return false;
		return true;
	}
	
	public boolean containsItemStack(ItemStack is)
	{
		if(is == null)
			return true;
		return containsItem(is.getItem());
	}
	
	public boolean containsItem(Item i)
	{
		for(ItemStack is: source)
		{
			if(is != null && is.getItem().equals(i))
				return true;
		}
		return false;
	}
	
	public boolean isSatisfied(ItemStack[] items)
	{
		if(items == null)
			return false;
		for(ItemStack is : source)
		{
			if(is == null)
				continue;
			boolean found = false;
			for(ItemStack compIS : items)
			{
				if(compIS == null)
					continue;
				if(!Helper.sameItem(compIS, is))
					continue;
				if(compIS.stackSize >= is.stackSize)
				{
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		return true;
	}
	
	public boolean flagsSatisfied(TardisCoreTileEntity core)
	{
		if(core == null)
			return false;
		if(flags.contains(LabFlag.NOTINFLIGHT))
			return !core.inFlight();
		if(flags.contains(LabFlag.INFLIGHT))
			return core.inFlight();
		if(flags.contains(LabFlag.INCOORDINATEDFLIGHT))
			return core.inCoordinatedFlight();
		if(flags.contains(LabFlag.INUNCOORDINATEDFLIGHT))
			return core.inFlight() && !core.inCoordinatedFlight();
		return true;
	}
}
