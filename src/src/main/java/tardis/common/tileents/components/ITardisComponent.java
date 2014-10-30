package tardis.common.tileents.components;

import net.minecraft.nbt.NBTTagCompound;
import tardis.common.tileents.TardisComponentTileEntity;

public interface ITardisComponent
{
	public ITardisComponent create(TardisComponentTileEntity parent);
	
	public void updateTick();
	
	public void readFromNBT(NBTTagCompound nbt);
	
	public void writeToNBT(NBTTagCompound nbt);
	
	public void revive(TardisComponentTileEntity parent);
	public void die();
}
