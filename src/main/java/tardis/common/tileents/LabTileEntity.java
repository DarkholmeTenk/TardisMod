package tardis.common.tileents;

import java.util.ArrayList;
import java.util.Iterator;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tardis.Configs;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.TardisOutput;
import tardis.common.core.helpers.Helper;
import tardis.common.recipes.LabRecipeRegistry;
import tardis.common.tileents.extensions.LabRecipe;

public class LabTileEntity extends AbstractTileEntity implements ISidedInventory, IActivatable
{
	private static final int[][] sideAccessibility = new int[][] { new int[] {5,6,7,8,9},new int[]{0,1,2,3,4}, new int[]{0,1,2,3,4,5,6,7,8,9}};

	private boolean wasWorking = false;
	private Boolean powered = null;
	private boolean active = true;
	public boolean generatingEnergy = false;
	private ItemStack[] inventory = new ItemStack[10];
	private int speed = 1;
	public int chargedEnergy = 0;
	public int requiredEnergy = 0;

	//CLIENT ONLY
	public double stickX = 0;
	public double stickZ = 0;

	@Override
	public void init()
	{
		if(ServerHelper.isServer())
			isPowered();
	}

	private int numPartiallyMatchingRecipes(ItemStack newItemStack)
	{
		ItemStack[] inputSlots = getInputSlots();

		//Copy all the recipes into a new array list
		ArrayList<LabRecipe> matching = new ArrayList<LabRecipe>();
		matching.addAll(LabRecipeRegistry.getRecipes());
		Iterator<LabRecipe> iter = matching.iterator();
		while(iter.hasNext()) //Loop through all the current recipes
		{
			LabRecipe comparison = iter.next();
			boolean doesMatch = true;
			for(int i = 0;i < inputSlots.length;i++)
			{
				if(!comparison.containsItemStack(inputSlots[i]))
				{
					doesMatch = false;
					continue;
				}
			}
			if(doesMatch)
				doesMatch = comparison.containsItemStack(newItemStack);
			if(!doesMatch)
				iter.remove();
		}
		return matching.size();
	}

	/**@return first recipe matched with the input items
	 */
	private LabRecipe getMatchedRecipe()
	{
		ItemStack[] inputSlots = getInputSlots();
		for(LabRecipe rec : LabRecipeRegistry.getRecipes()) //Find the first recipe matched by the input items
		{
			if(rec.isSatisfied(inputSlots))
			{
				return rec;
			}
		}
		return null;
	}

	public ItemStack[] getInputSlots()
	{
		ItemStack[] inputSlots = new ItemStack[5];
		for(int i = 0; i < 5; i++)
			inputSlots[i] = inventory[i];
		return inputSlots;
	}

	private void takeComponents(ItemStack[] components)
	{
		if(components == null)
			return;
		nextItem:
		for(ItemStack comp : components)
		{
			for(int i = 0;i<5;i++)
			{
				ItemStack invIS = inventory[i];
				if((invIS != null) && invIS.getItem().equals(comp.getItem()))
				{
					if(invIS.stackSize >= comp.stackSize)
					{
						invIS.stackSize-=comp.stackSize;
						if(invIS.stackSize == 0)
							inventory[i] = null;
						continue nextItem;
					}
				}
			}
		}
	}

	private boolean addResults(ItemStack[] results)
	{
		int freeSlots = 0;
		for(int i = 5;i<10;i++) // count the number of free slots available
		{
			if(inventory[i] == null)
				freeSlots++;
		}
		if(freeSlots >= results.length)
		{
			for(ItemStack is : results)
			{
				boolean outputted = false;
				for(int i = 5;i<10;i++)
				{
					if(inventory[i] == null)
					{
						inventory[i] = is.copy();
						outputted = true;
						break;
					}
					else if(WorldHelper.sameItem(inventory[i], is))
					{
						if((inventory[i].stackSize + is.stackSize) <= inventory[i].getMaxStackSize())
						{
							inventory[i].stackSize += is.stackSize;
							outputted = true;
							break;
						}
					}
				}
				/*if(outputted)
					TardisOutput.print("LTE", "Outputted " + is.getDisplayName());
				else
					TardisOutput.print("LTE", "Failed to output " + is.getDisplayName());*/
			}
			TardisOutput.print("LTE", "Outputted results");
			return true;
		}
		return false;
	}

	private void update(boolean working)
	{
		if(working != wasWorking)
		{
			wasWorking = working;
			sendUpdate();
		}
	}

	private void processTick()
	{
		IArtronEnergyProvider core = Helper.getArtronProvider(this,true);
		if(core != null)
		{
			LabRecipe matchedRecipe = getMatchedRecipe();
			if(isGeneratingEnergy(matchedRecipe,core))
			{
				//TardisOutput.print("LTE", "#03");
				chargedEnergy += core.takeArtronEnergy(1, false) ? 1 : 0;
				update(true);
			}
			else
			{
				//TardisOutput.print("LTE", "#02");
				update(false);
			}

			if(matchedRecipe != null)
			{
				if(chargedEnergy >= matchedRecipe.energyCost)
				{
					if(addResults(matchedRecipe.dest))
					{
						chargedEnergy = 0;
						takeComponents(matchedRecipe.source);
					}
				}
			}
			else
			{
				//TardisOutput.print("LTE", "#01");
				update(false);
			}
		}
	}

	private void attemptToEmpty()
	{
		TileEntity out = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
		if(out instanceof IInventory)
		{
			for(int i = 5;i<10;i++)
			{
				ItemStack is = inventory[i];
				if(is != null)
				{
					inventory[i] = WorldHelper.transferItemStack(is, (IInventory)out);
				}
			}
		}
	}

	public void dropEverything()
	{
		for(ItemStack is : inventory)
		{
			if(is == null)
				continue;
			EntityItem ei = new EntityItem(worldObj,xCoord+0.5,yCoord+0.5,zCoord+0.5,is);
			worldObj.spawnEntityInWorld(ei);
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(ServerHelper.isServer())
		{
			if(isActive())
				processTick();
			else
				update(false);
			if((tt % 1200) == 0)
				sendUpdate();
		}
		if((ServerHelper.isClient()) && isGeneratingEnergy(null,null))
			moveSticks();
		if(ServerHelper.isServer() && ((tt % 20) == 0))
		{
			powered = Helper.getArtronProvider(this, true) != null;
			if(!powered && active)
			{
				active = false;
				sendUpdate();
			}
			attemptToEmpty();
		}
	}

	public boolean isPowered()
	{
		if(powered == null)
		{
			if(ServerHelper.isServer())
				powered = Helper.getArtronProvider(this, true) != null;
			else
				return false;
		}
		return powered;
	}

	public boolean isActive()
	{
		return active && isPowered();
	}

	public boolean isGeneratingEnergy(LabRecipe rec,IArtronEnergyProvider core)
	{
		if(ServerHelper.isClient())
			return isActive() && generatingEnergy;
		boolean result = false;
		if((rec != null) && (core != null))
		{
			if((chargedEnergy < rec.energyCost) && rec.flagsSatisfied(core))
				result = true;
			if(result != generatingEnergy)
			{
				generatingEnergy = result;
				update(generatingEnergy);
			}
		}
		else if(core != null)
		{
			generatingEnergy = false;
			update(false);
		}

		return generatingEnergy;
	}

	@Override
	public int getSizeInventory()
	{
		return 10;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if(slot >= 5)
		{
			ItemStack toPull = inventory[slot];
			if(toPull != null)
			{
				int amToRem = Math.min(amount, toPull.stackSize);
				ItemStack pulled = toPull.copy();
				pulled.stackSize = amToRem;
				toPull.stackSize -= amToRem;
				if(toPull.stackSize == 0)
					inventory[slot] = null;
				return pulled;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item)
	{
		inventory[slot] = item;
	}

	@Override
	public String getInventoryName()
	{
		return "TardisMod.LabInventory";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
	{
		return false;
	}

	@Override
	public void openInventory(){}

	@Override
	public void closeInventory(){}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item)
	{
		if(slot < 5)
		{
			if(((inventory[slot] == null) && (numPartiallyMatchingRecipes(item) > 0)) || ((inventory[slot] != null) && inventory[slot].getItem().equals(item.getItem())))
				return true;
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if((side >= 0) && (side < 2))
			return sideAccessibility[2];
		return new int[]{};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side)
	{
		if((side < 0) || (side >= 2))
			return false;
		return isItemValidForSlot(slot,item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side)
	{
		if((side < 0) || (side >= 2) || (item == null))
			return false;
		return slot >= 5;
	}

	@Override
	public boolean activate(EntityPlayer pl, int side)
	{
		if(ServerHelper.isServer())
		{
			active = !active;
			powered = Helper.getArtronProvider(this, true) != null;
			sendUpdate();
		}
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		for(int i = 0;i<inventory.length;i++)
		{
			if(inventory[i] != null)
			{
				NBTTagCompound itemNBT = new NBTTagCompound();
				inventory[i].writeToNBT(itemNBT);
				nbt.setTag("i"+i, itemNBT);
			}
		}
	}

	@Override
	public void writeTransmittableOnly(NBTTagCompound nbt)
	{
		super.writeTransmittableOnly(nbt);
		nbt.setBoolean("powered", isPowered());
		nbt.setInteger("reqCharge", requiredEnergy);
		nbt.setBoolean("genEn", generatingEnergy);
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setBoolean("active", active);
		nbt.setInteger("speed", speed);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		for(int i = 0;i<inventory.length;i++)
		{
			if(nbt.hasKey("i"+i))
				inventory[i] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("i"+i));
		}
	}

	@Override
	public void readTransmittableOnly(NBTTagCompound nbt)
	{
		super.readTransmittableOnly(nbt);
		powered = nbt.getBoolean("powered");
		requiredEnergy = nbt.getInteger("reqCharge");
		generatingEnergy = nbt.getBoolean("genEn");
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		active = nbt.getBoolean("active");
		speed = MathHelper.clamp(nbt.getInteger("speed"), 1, Configs.maxLabSpeed);
	}

	private void moveSticks()
	{
		double inc = 0.0625;
		stickX = MathHelper.clamp(stickX, 0, 1);
		stickZ = MathHelper.clamp(stickZ, 0, 1);
		if(stickX < inc)
		{
			if(stickZ >= inc)
				stickZ -= inc;
			else
				stickX += inc;
		}
		else if(stickZ < inc)
		{
			if(stickX <= (1-inc))
				stickX += inc;
			else
				stickZ += inc;
		}
		else if(stickX > (1-inc))
		{
			if(stickZ <= (1-inc))
				stickZ += inc;
			else
				stickX -= inc;
		}
		else if(stickZ > (1-inc))
		{
			if(stickX >= inc)
				stickX -= inc;
			else
				stickZ += inc;
		}
	}

	public double[] getStick()
	{
		return new double[] { (stickX-0.5)*2, (stickZ-0.5)*2 };
	}

}
