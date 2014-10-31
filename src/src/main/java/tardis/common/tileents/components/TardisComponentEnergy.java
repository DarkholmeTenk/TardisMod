package tardis.common.tileents.components;

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
	protected TardisComponentEnergy() {}
	private int rfc = 0;
	
	public TardisComponentEnergy(TardisComponentTileEntity parent) {}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentEnergy(parent);
	}
	
	private void scanNearby()
	{
		World w = parentObj.worldObj;
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity te = w.getBlockTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if(te instanceof IEnergyHandler && !(te instanceof TardisComponentTileEntity))
			{
				//TardisOutput.print("TCE", "Attempting to energy");
				IEnergyHandler ieh = (IEnergyHandler)te;
				if(ieh.canInterface(dir.getOpposite()))
					rfc += ieh.receiveEnergy(dir.getOpposite(), extractEnergy(dir,TardisMod.rfPerT - rfc,false), false);
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
			return core.addRF(maxReceive,simulate);
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
