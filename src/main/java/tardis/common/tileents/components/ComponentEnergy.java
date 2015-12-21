package tardis.common.tileents.components;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.Configs;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.integration.other.CofHCore;
import tardis.common.integration.other.IC2;
import tardis.common.tileents.ComponentTileEntity;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value={
		@Optional.Interface(iface="cofh.api.energy.IEnergyHandler",modid=CofHCore.modname),
		@Optional.Interface(iface="ic2.api.energy.tile.IEnergySource",modid=IC2.modname),
		@Optional.Interface(iface="ic2.api.energy.tile.IEnergySink",modid=IC2.modname)
})
public class ComponentEnergy extends AbstractComponent implements IEnergyHandler, IActivatable, IEnergySink, IEnergySource, IScrewable
{
	private HashMap<ForgeDirection,AtomicInteger> hasFilled = new HashMap<ForgeDirection,AtomicInteger>(ForgeDirection.VALID_DIRECTIONS.length);
	protected ComponentEnergy() {}
	private int rfc = 0;
	private boolean posted = false;
	private int tier = 0;

	public ComponentEnergy(ComponentTileEntity parent)
	{
		blankHashmap();
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentEnergy(parent);
	}

	@Override
	public void die()
	{
		if(IC2.isIC2Installed())
			post(false);
		super.die();
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		if(CofHCore.isCOFHInstalled())
			ServerHelper.sendString(ent, "Energy: " + getEnergyStored(null) + "/" + getMaxEnergyStored(null)+"RF");
		if(IC2.isIC2Installed())
		{
			ServerHelper.sendString(ent, "Energy: " + (getEnergyStored(null)/Configs.euRatio) + "/"
					+ (getMaxEnergyStored(null)/Configs.euRatio)+"EU");
			ServerHelper.sendString(ent, "Tier: " + tierString());
		}
		return true;
	}

	private void blankHashmap()
	{
		for(ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
		{
			if(hasFilled.containsKey(f))
				hasFilled.get(f).set(0);
			else
				hasFilled.put(f,new AtomicInteger(0));
		}
	}

	@Optional.Method(modid=CofHCore.modname)
	private void scanNearby()
	{
		World w = parentObj.getWorldObj();
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			int b = hasFilled.get(dir).getAndSet(0);
			int max = Configs.rfPerT - rfc;
			if(b > 0)
				//max = Math.min(max, b/2);
				max = 0;
			TileEntity te = w.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if((te instanceof IEnergyHandler) && !(te instanceof ComponentTileEntity))
			{
				IEnergyHandler ieh = (IEnergyHandler)te;
				if(ieh.canConnectEnergy(dir.getOpposite()))
				{
					int am = extractEnergy(dir,max,true);
					am = Math.min(am, ieh.receiveEnergy(dir.getOpposite(), am, true));
					rfc += ieh.receiveEnergy(dir.getOpposite(), extractEnergy(dir,am,false), false);
				}
			}
		}
	}

	@Override
	public void updateTick()
	{
		if(CofHCore.isCOFHInstalled())
		{
			rfc = 0;
			scanNearby();
		}
		if(IC2.isIC2Installed() && ServerHelper.isServer() && !posted)
			post(true);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
		{
			int rec = ds.addRF(maxReceive,simulate);
			if(!simulate)
				hasFilled.get(from).set(rec);
			return rec;
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
			return ds.remRF(maxExtract,simulate);
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
			return ds.getRF();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
			return ds.getMaxRF();
		return 0;
	}

	private void post(boolean on)
	{
		if((on == posted) || (parentObj == null) || parentObj.isInvalid() || ServerHelper.isClient()) return;
		posted = on;
		IC2.post(parentObj, on);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		if(emitter instanceof ComponentTileEntity)
			return false;
		return tier == 0;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction)
	{
		if(receiver instanceof ComponentTileEntity)
			return false;
		return tier > 0;
	}

	@Override
	public double getOfferedEnergy()
	{
		if(ServerHelper.isClient()) return 0;
		double v = getEnergyStored(null)/Configs.euRatio;
		return v;
	}

	@Override
	public void drawEnergy(double amount)
	{
		if(ServerHelper.isClient()) return;
		extractEnergy(null,(int)Math.ceil(amount * Configs.euRatio),ServerHelper.isClient());
		System.out.println("I:"+amount+":"+getEnergyStored(null));
	}

	@Override
	public int getSourceTier()
	{
		return tier + 1;
	}

	@Override
	public double getDemandedEnergy()
	{
		if(ServerHelper.isClient()) return 0;
		return (getMaxEnergyStored(null)-getEnergyStored(null)) / Configs.euRatio;
	}

	@Override
	public int getSinkTier()
	{
		return 8;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
		if(ServerHelper.isClient()) return 0;
		double accepted = receiveEnergy(directionFrom, (int) Math.floor(amount * Configs.euRatio),false)/(double)Configs.euRatio;
		System.out.println("I:"+amount+":"+voltage+":"+getEnergyStored(null));
		return 0;
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(IC2.isIC2Installed())
		{
			tier = (tier + 1) % 5;
			if(ServerHelper.isServer())
				ServerHelper.sendString(player, "Energy tier set to " + tierString());
			return true;
		}
		return false;
	}

	public String tierString()
	{
		switch(tier)
		{
			case 0: return "Input";
			case 1: return "LV";
			case 2: return "MV";
			case 3: return "HV";
			case 4: return "EV";
		}
		return "";
	}

}
