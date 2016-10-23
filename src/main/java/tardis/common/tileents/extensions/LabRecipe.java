package tardis.common.tileents.extensions;

import java.util.EnumSet;

import io.darkcraft.darkcore.mod.handlers.containers.ItemStackContainer;
import io.darkcraft.darkcore.mod.handlers.containers.ItemStackContainer.IEntityItemInitialiser;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tardis.api.IArtronEnergyProvider;

public class LabRecipe
{
	public final String id;
	public final ItemStack[] source;
	public final ItemStack[] dest;
	public final ItemStackContainer displayIS;
	public final EnumSet<LabFlag> flags;
	public final int energyCost;

	public LabRecipe(String id, ItemStack _source, ItemStack _dest, EnumSet<LabFlag> _flags, int _enCost)
	{
		this(id, new ItemStack[]{_source}, new ItemStack[]{_dest}, _flags, _enCost);
	}

	public LabRecipe(String _id, ItemStack[] _source, ItemStack[] _dest, EnumSet<LabFlag> _flags, int _enCost)
	{
		id = _id;
		source = _source;
		dest = _dest;
		flags = _flags;
		energyCost = _enCost;
		ItemStack output = null;
		for(ItemStack is : dest)
			if(is != null)
				output = is;
		displayIS = ItemStackContainer.getContainer(new IEntityItemInitialiser(){
			@Override
			public void initEI(EntityItem ei)
			{
				ei.hoverStart = 0;
				ei.rotationYaw = 0;
			}
		});
		displayIS.setIS(output);
	}

	public boolean isValid()
	{
		if(energyCost < 0)
			return false;
		if((source == null) || (dest == null) || (flags == null))
			return false;
		if((source.length == 0) || (dest.length == 0) || (source.length > 5) || (dest.length > 5))
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
			if((is != null) && is.getItem().equals(i))
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
				if(!WorldHelper.sameItem(compIS, is))
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

	public boolean flagsSatisfied(IArtronEnergyProvider core)
	{
		if(core == null)
			return false;
		boolean sat = true;
		for(LabFlag flag: flags)
			sat = sat && core.doesSatisfyFlag(flag);
		return sat;
	}
}
