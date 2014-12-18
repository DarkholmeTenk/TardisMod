package tardis.common.tileents;

import tardis.common.core.Helper;
import net.minecraft.nbt.NBTTagCompound;

public class LabTileEntity extends TardisAbstractTileEntity
{
	private Boolean powered = null;
	private boolean active = false;

	@Override
	public void init()
	{
		if(Helper.isServer())
		{
			isPowered();
		}
	}
	
	private void processTick()
	{
		
	}
	
	@Override
	public void updateEntity()
	{
		if(isActive())
			processTick();
	}
	
	public boolean isPowered()
	{
		if(powered == null)
		{
			if(Helper.isServer())
				powered = Helper.isTardisWorld(worldObj);
			else
				return false;
		}
		return powered;
	}
	
	public boolean isActive()
	{
		return active && isPowered();
	}
	
	@Override
	public void writeTransmittableOnly(NBTTagCompound nbt)
	{
		nbt.setBoolean("powered", isPowered());
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setBoolean("active", active);
	}

	@Override
	public void readTransmittableOnly(NBTTagCompound nbt)
	{
		powered = nbt.getBoolean("powered");
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		active = nbt.getBoolean("active");
	}

}
