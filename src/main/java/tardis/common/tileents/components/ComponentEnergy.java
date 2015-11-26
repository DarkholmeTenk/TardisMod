package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.Configs;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

@Optional.Interface(iface="cofh.api.energy.IEnergyHandler",modid="CoFHCore")
public class ComponentEnergy extends AbstractComponent implements IEnergyHandler, IActivatable
{
	private HashMap<ForgeDirection,AtomicInteger> hasFilled = new HashMap<ForgeDirection,AtomicInteger>(ForgeDirection.VALID_DIRECTIONS.length);
	protected ComponentEnergy() {}
	private int rfc = 0;

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
	public boolean activate(EntityPlayer ent, int side)
	{
		ServerHelper.sendString(ent, "Energy: " + getEnergyStored(null) + "/" + getMaxEnergyStored(null)+"RF");
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
		if(!Loader.isModLoaded("CoFHCore")) return;
		rfc = 0;
		scanNearby();
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



}
