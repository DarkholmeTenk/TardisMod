package tardis.common.tileents.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class ComponentInventory extends AbstractComponent implements IInventory
{
	protected ComponentInventory(){}
	
	public ComponentInventory(ComponentTileEntity parent)
	{
		parentObj = parent;
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentInventory(parent);
	}

	@Override
	public int getSizeInventory()
	{
		CoreTileEntity tce = getCore();
		if(tce != null)
			return tce.getInvSize();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		CoreTileEntity tce = getCore();
		if(tce != null)
			return tce.getIS(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		CoreTileEntity tce = getCore();
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
		CoreTileEntity tce = getCore();
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
		CoreTileEntity tce = getCore();
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
		CoreTileEntity core = getCore();
		if(core != null)
			core.markDirty();
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
