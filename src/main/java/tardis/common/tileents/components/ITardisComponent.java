package tardis.common.tileents.components;

import net.minecraft.nbt.NBTTagCompound;

import tardis.common.tileents.ComponentTileEntity;

public interface ITardisComponent
{
	public ITardisComponent create(ComponentTileEntity parent);
	
	public void updateTick();
	
	public void readFromNBT(NBTTagCompound nbt);
	
	public void writeToNBT(NBTTagCompound nbt);
	
	public void revive(ComponentTileEntity parent);
	public void die();
}
