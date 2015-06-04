package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;

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
		TardisDataStore ds = getDatastore();
		if(ds != null)
			return ds.getInvSize();
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
			return ds.getIS(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		TardisDataStore ds = getDatastore();
		if(ds != null)
		{
			ItemStack is = ds.getIS(i);
			ItemStack newIS = is.splitStack(j);
			ds.setIS(is, i);
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
		TardisDataStore ds = getDatastore();
		if(ds != null)
			ds.setIS(itemstack,i);
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
		TardisDataStore ds = getDatastore();
		if(ds != null)
		{
			ItemStack currentIS = ds.getIS(i);
			if(WorldHelper.sameItem(currentIS, itemstack))
				return true;
			if(currentIS == null)
			{
				for(int j = 0; j < getSizeInventory(); j++)
				{
					currentIS = ds.getIS(j);
					if(WorldHelper.sameItem(currentIS, itemstack))
						return false;
				}
				return true;
			}
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
		TardisDataStore ds = getDatastore();
		if(ds != null)
			ds.markDirty();
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
