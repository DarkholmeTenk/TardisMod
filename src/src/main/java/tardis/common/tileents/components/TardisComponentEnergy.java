package tardis.common.tileents.components;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cofh.api.energy.IEnergyHandler;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentEnergy extends TardisAbstractComponent implements IEnergyHandler
{
	private HashMap<ForgeDirection,AtomicInteger> hasFilled = new HashMap<ForgeDirection,AtomicInteger>(ForgeDirection.VALID_DIRECTIONS.length);
	protected TardisComponentEnergy() {}
	private int rfc = 0;
	
	public TardisComponentEnergy(TardisComponentTileEntity parent)
	{
		blankHashmap();
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentEnergy(parent);
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
		World w = parentObj.worldObj;
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			int b = hasFilled.get(dir).getAndSet(0);
			int max = TardisMod.rfPerT - rfc;
			if(b > 0)
				max = Math.min(max, b/2);
			TileEntity te = w.getBlockTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if(te instanceof IEnergyHandler && !(te instanceof TardisComponentTileEntity))
			{
				//TardisOutput.print("TCE", "Attempting to energy");
				IEnergyHandler ieh = (IEnergyHandler)te;
				if(ieh.canInterface(dir.getOpposite()))
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
		rfc = 0;
		scanNearby();
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		if(core != null)
		{
			int rec = core.addRF(maxReceive,simulate);
			if(!simulate)
				hasFilled.get(from).set(rec);
			return rec;
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		if(core != null)
			return core.remRF(maxExtract,simulate);
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		return core != null;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		if(core != null)
			return core.getRF();
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		if(core != null)
			return core.getMaxRF();
		return 0;
	}
	
	

}
