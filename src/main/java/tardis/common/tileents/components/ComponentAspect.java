package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

public class ComponentAspect extends AbstractComponent implements IAspectSource, IScrewable
{
	/**
	 * suckMode = 0 -> all aspects sucked equally and weakly
	 * suckMode = 1 -> all aspects sucked equally and strongly
	 * suckMode = 2 -> strongest suction transferred.
	 */
	private int suckMode = 0;
	private boolean amMax = false;
	private int mySuck = 16;

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

		if(dump.canInputFrom(o))
		{
			Aspect suckt = dump.getSuctionType(o);
			int suckam = dump.getSuctionAmount(o);
			if(suckam > (ds.maxSuck + 1))
			{
				amMax = true;
				mySuck = suckam-1;
				ds.maxSuck = mySuck;
				ds.maxSuckT = dump.getSuctionType(o);
			}
			for(Aspect a : myAspects)
			{
				if(((suckt == null) || (suckt == a)) && (suckam > getSuction()))
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
			if(source.getSuctionAmount(o) < getSuction())
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
		boolean wasMax = amMax && (ds.maxSuck == mySuck);
		for(ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
		{
			amMax = false;
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
		if(wasMax && !amMax)
		{
			ds.maxSuck = 16;
			ds.maxSuckT = null;
		}
	}

	public int getSuction()
	{
		if(suckMode == 2)
		{
			TardisDataStore ds = getDatastore();
			if (ds != null) return  Math.max(16,ds.maxSuck);
		}
		else if(suckMode == 1)
			return 48;
		return 16;
	}

	public Aspect getSuctionAspect()
	{
		if(suckMode == 2)
		{
			TardisDataStore ds = getDatastore();
			if (ds != null) return ds.maxSuckT;
		}
		return null;
	}

	@Override
	public boolean screw(ScrewdriverMode mode, EntityPlayer player)
	{
		if(!ServerHelper.isServer())
			return true;
		CoreTileEntity core = getCore();
		if(mode == ScrewdriverMode.Reconfigure)
		{
			if((core == null) || core.canModify(player))
			{
				suckMode = (suckMode + 1) % 3;
				if(suckMode == 2)
					ServerHelper.sendString(player, "This essentia interface will transfer the strongest suction");
				else if(suckMode == 1)
					ServerHelper.sendString(player, "This essentia interface will suck all aspects equally strongly");
				else
					ServerHelper.sendString(player, "This essentia interface will suck all aspects equally weakly");
			}
			else
				ServerHelper.sendString(player, CoreTileEntity.cannotModifyMessage);
		}
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("suckmode", suckMode);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		suckMode = nbt.getInteger("suckmode");
	}

}
