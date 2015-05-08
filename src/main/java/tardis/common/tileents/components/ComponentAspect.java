package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

public class ComponentAspect extends AbstractComponent implements IAspectSource
{
	protected ComponentAspect()
	{
	}

	public ComponentAspect(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentAspect(parent);
	}

	@Override
	public AspectList getAspects()
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.getAspectList();
		return new AspectList();
	}

	@Override
	public void setAspects(AspectList aspects)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) ds.setAspectList(aspects);
	}

	@Override
	public boolean doesContainerAccept(Aspect tag)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.canHaveAspect(tag, 0);
		return false;
	}

	@Override
	public int addToContainer(Aspect tag, int amount)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.addAspect(tag, amount);
		return 0;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.removeAspect(tag, amount);
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList ot)
	{
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.getAspectList().getAmount(tag) >= amount;
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList ot)
	{
		return false;
	}

	@Override
	public int containerContains(Aspect tag)
	{
		TardisDataStore ds = getDatastore();
		if (ds != null) return ds.getAspectList().getAmount(tag);
		return 0;
	}

	private void dumpAspects(TardisDataStore ds, IEssentiaTransport dump, ForgeDirection f)
	{
		Aspect[] myAspects = ds.getAspectList().getAspects();
		ForgeDirection o = f.getOpposite();
		for(Aspect a : myAspects)
		{
			if(dump.canInputFrom(o))
			{
				if(((dump.getSuctionType(o) == null) || (dump.getSuctionType(o) == a)) && (dump.getSuctionAmount(o) > parentObj.getSuctionAmount(f)))
				{
					int added = dump.addEssentia(a, ds.getAspectList().getAmount(a), o);
					if(added > 0)
					{
						ds.removeAspect(a, added);
						break;
					}
				}
			}
		}
	}

	private void takeAspects(TardisDataStore ds, IEssentiaTransport source, ForgeDirection f)
	{
		Aspect[] myAspects = ds.getAspectList().getAspects();
		ForgeDirection o = f.getOpposite();
		if(source.canOutputTo(o))
		{
			Aspect a = source.getEssentiaType(o);
			if(source.getSuctionAmount(o) < parentObj.getSuctionAmount(f))
				if(doesContainerAccept(a))
				{
					int max = maxEachAspect - ds.getAspectList().getAmount(a);
					max = source.takeEssentia(a, max, o);
					addToContainer(a,max);
				}
		}
	}

	@Override
	public void updateTick()
	{
		super.updateTick();
		if((tt % 10) != 1)
			return;
		TardisDataStore ds = getDatastore();
		if(ds == null) return;
		for(ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
		{
			SimpleCoordStore me = parentObj.coords();
			SimpleCoordStore next = me.getNearby(f);
			TileEntity te = next.getTileEntity();
			if(te instanceof IEssentiaTransport)
			{
				IEssentiaTransport other = (IEssentiaTransport) te;
				takeAspects(ds,other,f);
				dumpAspects(ds,other,f);
			}
		}
	}

}
