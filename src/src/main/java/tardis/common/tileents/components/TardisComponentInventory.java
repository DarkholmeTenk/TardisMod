package tardis.common.tileents.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentInventory extends TardisAbstractComponent implements IInventory
{
	protected TardisComponentInventory(){}
	
	public TardisComponentInventory(TardisComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentInventory(parent);
	}
	
	private TardisCoreTileEntity getCore()
	{
		if(parentObj != null && parentObj.getWorldObj() != null)
			return Helper.getTardisCore(parentObj.getWorldObj());
		return null;
	}

	@Override
	public int getSizeInventory()
	{
		TardisCoreTileEntity tce = getCore();
		if(tce != null)
			return tce.getInvSize();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		TardisCoreTileEntity tce = getCore();
		if(tce != null)
			return tce.getIS(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		TardisCoreTileEntity tce = getCore();
		if(tce != null)
		{
			ItemStack is = tce.getIS(i);
			ItemStack newIS = is.splitStack(j);
			tce.setIS(is, i);
			return newIS;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		return getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		TardisCoreTileEntity tce = getCore();
		if(tce != null)
			tce.setIS(itemstack,i);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		TardisCoreTileEntity tce = getCore();
		if(tce != null)
		{
			if(tce.getIS(i) == null)
				return true;
		}
		return false;
	}

	@Override
	public String getInventoryName()
	{
		return "TardisMod.LinkedInventory";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void markDirty()
	{
		Helper.getTardisCore(parentObj.getWorldObj()).markDirty();
	}

	@Override
	public void openInventory()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory()
	{
		// TODO Auto-generated method stub
		
	}

}
