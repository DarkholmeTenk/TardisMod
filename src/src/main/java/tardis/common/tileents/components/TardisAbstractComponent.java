package tardis.common.tileents.components;

import net.minecraft.nbt.NBTTagCompound;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public abstract class TardisAbstractComponent implements ITardisComponent
{
	protected TardisComponentTileEntity parentObj;
	
	protected int world;
	protected int xCoord;
	protected int yCoord;
	protected int zCoord;
	
	protected void parentAdded(TardisComponentTileEntity parent)
	{
		parentObj = parent;
		world  = Helper.getWorldID(parent.getWorldObj());
		xCoord = parent.xCoord;
		yCoord = parent.yCoord;
		zCoord = parent.zCoord;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
	}

	@Override
	public void die()
	{
		parentObj = null;
	}

	@Override
	public void revive(TardisComponentTileEntity parent)
	{
		parentAdded(parent);
	}
	
	public TardisCoreTileEntity getCore()
	{
		if(parentObj != null)
			return parentObj.getCore();
		return null;
	}
	
	@Override
	public void updateTick()
	{
	}
}
