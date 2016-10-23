package tardis.common.tileents;

import java.util.List;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntitySer;
import io.darkcraft.darkcore.mod.handlers.containers.ItemStackContainer;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatablePrecise;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.HitPosition;
import tardis.common.core.helpers.Helper;
import tardis.common.recipes.LabRecipeRegistry;
import tardis.common.tileents.extensions.LabRecipe;

@NBTSerialisable
public class AdvancedLab extends AbstractTileEntitySer implements IActivatablePrecise, ISidedInventory
{
	@NBTProperty
	ItemStackContainer[] inv = ItemStackContainer.getArray(10);

	@Override
	public int getSizeInventory()
	{
		return 10;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inv[slot].is();
	}

	public int getStackCount(ItemStack is)
	{
		int count = 0;
		for(int i = 0; i < 5; i++)
		{
			ItemStack toCheck = inv[i].is();
			if(WorldHelper.sameItem(is, toCheck))
				count += toCheck.stackSize;
		}
		return count;
	}

	public EntityItem getEntityItem(int slot)
	{
		return inv[slot].ei();
	}

	@Override
	public ItemStack decrStackSize(int slot, int num)
	{
		ItemStack old = inv[slot].is();
		if(old == null) return null;
		if(old.stackSize <= num)
		{
			inv[slot].setIS(null);
			sendUpdate();
			return old;
		}
		else
		{
			ItemStack newIS = old.splitStack(num);
			sendUpdate();
			return newIS;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_){ return null; }

	@Override
	public void setInventorySlotContents(int slot, ItemStack is)
	{
		inv[slot].setIS(is);
		sendUpdate();
	}

	@Override
	public String getInventoryName(){ return "TardisMod.AdvLabInventory"; }

	@Override
	public boolean hasCustomInventoryName(){ return true; }

	@Override
	public int getInventoryStackLimit(){ return 64; }

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_){ return false; }

	@Override
	public void openInventory(){}

	@Override
	public void closeInventory(){}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is)
	{
		if(inv[slot].is() == null)
			return true;
		ItemStack old = inv[slot].is();
		if(WorldHelper.sameItem(old, is))
			if(old.stackSize < old.getMaxStackSize())
				return true;
		return false;
	}

	private static final int[] input = new int[]{0,1,2,3,4};
	private static final int[] output = new int[]{5,6,7,8,9};
	public static final int[] none = new int[]{};
	@Override
	public int[] getAccessibleSlotsFromSide(int dir)
	{
		if(dir == 0) return output;
		if(dir == 1) return input;
		return none;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, int side)
	{
		if ((slot >= 0) && (slot < 5) && (side == ForgeDirection.UP.ordinal()))
		{
			boolean r = isItemValidForSlot(slot, is);
			if(r)
				queueUpdate();
			return r;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side)
	{
		return (slot >= 5) && (slot < 10) && (side == ForgeDirection.DOWN.ordinal());
	}

	@Override
	public void addDrops(List<ItemStack> drops)
	{
		for(ItemStackContainer isc : inv)
			if(isc.is() != null)
				drops.add(isc.is());
	}

	@NBTProperty public int page = 0;
	@NBTProperty public LabRecipe selectedRecipe;
	@NBTProperty public boolean active;
	@NBTProperty public int energyBuildup;

	private void turnOff()
	{
		active = false;
		energyBuildup = 0;
	}

	private void turnOn()
	{
		active = true;
	}

	@Override
	public boolean activate(EntityPlayer ent, int side, float x, float y, float z)
	{
		if(ServerHelper.isClient()) return true;
		if(side == ForgeDirection.UP.ordinal())
		{
			ItemStack inHand = ent.getCurrentEquippedItem();
			if(inHand == null)
			{
				for(int i = 0; i < 5; i++)
					if(inv[i].is() != null)
					{
						WorldHelper.giveItemStack(ent, inv[i].is());
						inv[i].setIS(null);
						queueUpdate();
						return true;
					}
			}
			else
			{
				ItemStack rem = WorldHelper.transferItemStack(inHand, this, ForgeDirection.UP);
				ent.inventory.mainInventory[ent.inventory.currentItem] = rem;
				ent.inventory.markDirty();
				queueUpdate();
			}
			return true;
		}
		else if(side == ForgeDirection.DOWN.ordinal())
		{
			ItemStack inHand = ent.getCurrentEquippedItem();
			if(inHand == null)
			{
				for(int i = 5; i < 10; i++)
					if(inv[i].is() != null)
					{
						WorldHelper.giveItemStack(ent, inv[i].is());
						inv[i].setIS(null);
						queueUpdate();
						return true;
					}
			}
			return true;
		}
		System.out.format("S:%d - M:%d%n", side, getBlockMetadata());
		int as = 0;
		switch(getBlockMetadata())
		{
			case 0: as = 2; break;
			case 1: as = 5; break;
			case 2: as = 3; break;
			case 3: as = 4; break;
		}
		HitPosition hp = new HitPosition(side, x, y, z);
		float r = 0.125f;
		float s = (1 - (r * 2));
		if(hp.within(as, r, r, 1-r, 1-r))
		{
			hp = new HitPosition((hp.posY-r)/s,(hp.posZ-r)/s,hp.side);
			if(selectedRecipe == null)
			{
				int xPress = MathHelper.floor(hp.posZ * 3);
				int yPress = MathHelper.floor(hp.posY * 3);
				if(yPress == 0)
				{
					if(xPress == 0)
						page = Math.max(page - 1,0);
					if(xPress == 2)
						page = Math.min(page + 1, (LabRecipeRegistry.getRecipes().size() - 1) / 6);
				}
				else
				{
					int slot = (page * 6) + xPress + ((2-yPress) * 3);
					List<LabRecipe> recs = LabRecipeRegistry.getRecipes();
					if((slot >= 0) && (slot < recs.size()))
						selectedRecipe = recs.get(slot);
				}
			}
			else
			{
				if(hp.posY < 0.2)
				{
					if(hp.posZ < 0.4)
					{
						selectedRecipe = null;
						turnOff();
					}
					else if(hp.posZ > 0.55)
					{
						if(active)
							turnOff();
						else
							turnOn();
					}
				}
			}
			sendUpdate();
			return true;
		}
		return true;
	}

	private boolean hasItems()
	{
		for(ItemStack is : selectedRecipe.source)
			if(getStackCount(is) < is.stackSize)
				return false;
		return true;
	}

	private void takeItems()
	{
		recLoop:
		for(ItemStack neededIS : selectedRecipe.source)
		{
			int needed = neededIS.stackSize;
			for(int i = 0; i < 5; i++)
			{
				ItemStack toCheck = inv[i].is();
				if(!WorldHelper.sameItem(neededIS, toCheck)) continue;
				int take = Math.min(needed, toCheck.stackSize);
				inv[i].decr(take);
				needed -= take;
				if(needed == 0) continue recLoop;
			}
		}
	}

	private boolean addResults()
	{
		ItemStack[] results = new ItemStack[selectedRecipe.dest.length];
		for(int i = 0; i < results.length; i ++) results[i] = selectedRecipe.dest[i].copy();
		ItemStack[] output = new ItemStack[5];
		for(int i = 0; i < 5; i++){ output[i] = inv[5+i].is(); if(output[i] != null) output[i] = output[i].copy();} // Generate array to test

		itemLoop:
		for(int i = 0; i < results.length; i++)
		{
			if(results[i] == null) continue;
			for(int j = 0; j < 5; j++)
			{
				if(output[j] == null)
				{
					output[j] = results[i];
					continue itemLoop;
				}
				else if((output[j].stackSize < output[j].getMaxStackSize()) && WorldHelper.sameItem(results[i], output[j]))
				{
					int amount = Math.min(results[i].stackSize, output[j].getMaxStackSize() - output[j].stackSize);
					output[j].stackSize += amount;
					results[i].stackSize -= amount;
					if(results[i].stackSize == 0) continue itemLoop;
				}
			}
			return false;
		}
		for(int i = 0; i < 5; i++)
			inv[5+i].setIS(output[i]);
		return true;
	}

	private void processRecipe()
	{
		if(ServerHelper.isClient()) return;
		if(energyBuildup < selectedRecipe.energyCost)
		{
			IArtronEnergyProvider iaep = Helper.getArtronProvider(this, true);
			if((iaep != null) && hasItems())
			{
				int energyRemaining = selectedRecipe.energyCost - energyBuildup;
				int take = Math.min(20, energyRemaining);
				if(iaep.takeArtronEnergy(take, false))
					energyBuildup += take;
			}
		}
		else if((energyBuildup >= selectedRecipe.energyCost) && hasItems())
		{
			if(addResults())
			{
				energyBuildup = 0;
				takeItems();
			}
		}
		queueUpdate();
	}

	private void emptyDown()
	{
		TileEntity te = coords().translate(0,-1,0).getTileEntity();
		if(te instanceof IInventory)
		{
			for(int i = 5; i < 10; i++)
			{
				ItemStack is = inv[i].is();
				if(is == null) continue;
				int oss = is.stackSize;
				is = WorldHelper.transferItemStack(is, (IInventory)te, ForgeDirection.UP);
				inv[i].setIS(is);
				if((is == null) || (is.stackSize != oss))
					queueUpdate();
			}
		}
	}

	@Override
	public void tick()
	{
		ItemStackContainer.tick(inv);
		if((tt % 20) == 0)
		{
			if((selectedRecipe != null) && active)
				processRecipe();
			emptyDown();
		}
	}

}
