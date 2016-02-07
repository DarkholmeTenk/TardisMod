package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tardis.Configs;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;

public class ComponentFluid extends AbstractComponent implements IFluidHandler, IScrewable, IActivatable
{
	private int currentTank=-1;
	protected ComponentFluid() { }

	public ComponentFluid(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentFluid(parent);
	}

	private FluidStack[] getTanks(boolean limit)
	{
		if((parentObj != null) && (parentObj.getWorldObj() != null))
		{
			TardisDataStore ds = getDatastore();
			if(ds != null)
			{
				FluidStack[] tanks = ds.getTanks();
				if((currentTank < 0) || (currentTank >= tanks.length) || !limit)
					return tanks;
				return new FluidStack[] {tanks[currentTank]};
			}

		}
		return null;
	}

	private int fill(FluidStack[] tanks, int i, boolean doFill, FluidStack resource)
	{
		if(tanks[i] == null)
		{
			if(doFill)
				tanks[i] = new FluidStack(resource,Math.min(Configs.maxFlu, resource.amount));
			return Math.min(Configs.maxFlu, resource.amount);
		}
		else if(tanks[i].equals(resource))
		{
			int maxFill = Math.min(Configs.maxFlu - tanks[i].amount,resource.amount);
			if(doFill)
				tanks[i].amount += maxFill;
			return maxFill;
		}
		return 0;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null)
			return 0;
		FluidStack[] tanks = getTanks(false);
		int ret = 0;
		if(tanks != null)
		{
			if(currentTank == -1)
				for(int i = 0;(i<tanks.length)&&(ret==0);i++)
					ret = fill(tanks, i, doFill, resource);
			else
				ret = fill(tanks, currentTank, doFill, resource);
		}
		return ret;
	}

	private FluidStack drain(FluidStack[] tanks, int i, FluidStack resource, boolean doDrain)
	{
		if((tanks[i] != null) && tanks[i].equals(resource))
		{
			int toDrain = Math.min(tanks[i].amount,resource.amount);
			if(doDrain)
			{
				if(toDrain == tanks[i].amount)
					tanks[i] = null;
				else
					tanks[i].amount -= toDrain;
			}
			return new FluidStack(resource,toDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if(resource == null)
			return null;
		FluidStack[] tanks = getTanks(false);
		FluidStack ret = null;
		if(tanks != null)
		{
			if(currentTank == -1)
				for(int i = 0;(i<tanks.length)&& (ret != null);i++)
					ret = drain(tanks, i, resource, doDrain);
			else
				ret = drain(tanks, currentTank, resource, doDrain);
		}
		return ret;
	}

	private FluidStack drain(FluidStack[] tanks, ForgeDirection from, IFluidHandler other, int i, int maxDrain, boolean doDrain)
	{
		if(tanks == null)
			return null;
		if(tanks[i] != null)
		{
			Fluid f = tanks[i].getFluid();
			if((other != null) && !other.canFill(from.getOpposite(), f))
				return null;
			int toDrain = Math.min(tanks[i].amount,maxDrain);
			Fluid fluidID = tanks[i].getFluid();
			if(doDrain)
			{
				if(toDrain == tanks[i].amount)
					tanks[i] = null;
				else
					tanks[i].amount -= toDrain;
			}
			return new FluidStack(fluidID,toDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		FluidStack[] tanks = getTanks(false);
		FluidStack ret = null;
		if(tanks != null)
		{
			SimpleCoordStore n = parentObj.coords().getNearby(from);
			TileEntity te = n.getTileEntity();
			IFluidHandler other = null;
			if(te instanceof IFluidHandler)
				other = (IFluidHandler)te;
			if(currentTank == -1)
				for(int i = 0;(i<tanks.length)&&(ret==null);i++)
					ret = drain(tanks, from, other, i, maxDrain, doDrain);
			else
				ret = drain(tanks,from,other,currentTank,maxDrain,doDrain);
		}
		return ret;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		boolean foundEmpty = false;
		FluidStack[] tanks = getTanks(true);
		if(tanks != null)
		{
			for(int i = 0;i<tanks.length;i++)
			{
				if(tanks[i] == null)
				{
					foundEmpty = true;
					continue;
				}
				if(tanks[i].getFluid().equals(fluid))
					return tanks[i].amount < Configs.maxFlu;
			}
		}
		return foundEmpty;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		FluidStack[] tanks = getTanks(true);
		if(tanks != null)
		{
			for(int i = 0;i<tanks.length;i++)
			{
				if(tanks[i] == null)
					continue;
				if(tanks[i].getFluid().equals(fluid))
					return tanks[i].amount > 0;
			}
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		FluidStack[] tanks = getTanks(true);
		if(tanks != null)
		{
			FluidTankInfo[] retVal = new FluidTankInfo[tanks.length];
			for(int i = 0;i<tanks.length;i++)
			{
				FluidTankInfo info = new FluidTankInfo(tanks[i],Configs.maxFlu);
				retVal[i] = info;
			}
			return retVal;
		}
		return null;
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		FluidStack[] tanks = getTanks(false);
		for(int i = 0; i < tanks.length; i++)
		{
			FluidStack ft = tanks[i];
			if(ft == null)
			{
				ServerHelper.sendString(ent, "Tank " + (i+1) + ": Empty");
				continue;
			}
			Fluid f = ft.getFluid();
			String name = f.getLocalizedName(ft);
			ServerHelper.sendString(ent, "Tank " + (i+1) + ": " + name + " " + ft.amount +"/"+Configs.maxFlu+"mb");
		}
		return true;
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		TardisDataStore ds = getDatastore();
		if((ds == null) || ds.hasPermission(player, TardisPermission.ROUNDEL))
		{
			currentTank = MathHelper.cycle(currentTank + (player.isSneaking() ? -1 : 1), -1, Configs.numTanks-1);
			if(currentTank == -1)
				ServerHelper.sendString(player, "Fluid interface configured to all tanks");
			else
				ServerHelper.sendString(player, "Fluid interface configured to tank " + (currentTank+1)	);
			return true;
		}
		else
		{
			ServerHelper.sendString(player, "You do not have permission to modify this");
		}
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("cT", currentTank);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		currentTank = nbt.hasKey("cT") ? nbt.getInteger("cT") : -1;
	}
}
