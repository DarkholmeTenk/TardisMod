package tardis.common.tileents;

import java.util.HashMap;

import cofh.api.energy.IEnergyHandler;

import appeng.api.DimentionalCoord;
import appeng.api.WorldCoord;
import appeng.api.me.tiles.IGridTeleport;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import tardis.TardisMod;
import tardis.api.IActivatable;
import tardis.api.IChunkLoader;
import tardis.api.IScrewable;
import tardis.api.IWatching;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.items.TardisComponentItem;
import tardis.common.tileents.components.ITardisComponent;
import tardis.common.tileents.components.TardisComponentGrid;
import tardis.common.tileents.components.TardisTEComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TardisComponentTileEntity extends TardisAbstractTileEntity implements IScrewable, IActivatable, IWatching, IGridTeleport,
																		IGridTileEntity, IEnergyHandler, IInventory, IFluidHandler, IChunkLoader
{
	private HashMap<Integer,ITardisComponent> comps = new HashMap<Integer,ITardisComponent>();
	private boolean valid = false;
	private boolean inited = false;
	private boolean compAdded = false;
	
	public boolean addComponent(TardisTEComponent comp)
	{
		if(!hasComponent(comp))
		{
			compAdded = true;
			comps.put(comp.ordinal(), comp.baseObj.create(this));
			return true;
		}
		return false;
		
	}
	
	public boolean hasComponent(TardisTEComponent comp)
	{
		if(comps != null)
			return comps.containsKey(comp.ordinal());
		return false;
	}
	
	public ITardisComponent getComponent(TardisTEComponent comp)
	{
		if(hasComponent(comp))
			return comps.get(comp.ordinal());
		return null;
	}
	
	public ItemStack[] getComponentItems()
	{
		ItemStack[] retIS = new ItemStack[comps.size()];
		if(retIS.length > 0)
		{
			int i = 0;
			for(Integer compVal : comps.keySet())
			{
				ItemStack is = new ItemStack(TardisMod.componentItem.itemID,1,compVal);
				retIS[i] = is;
				i++;
			}
		}
		return retIS;
	}
	
	private void reviveComps()
	{
		if(comps.size() > 0)
		{
			for(ITardisComponent comp : comps.values())
			{
				if(comp != null)
					comp.revive(this);
			}
		}
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!inited || compAdded)
		{
			compAdded = false;
			reviveComps();
			inited = true;
			if(Helper.isServer() && valid)
			{
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord,yCoord,zCoord));
				sendUpdate();
			}
		}
		
		if(comps.size() > 0)
		{
			for(ITardisComponent comp : comps.values())
				comp.updateTick();
		}
	}
	
	@Override
	public boolean screw(TardisScrewdriverMode mode, EntityPlayer player)
	{
		if(mode == TardisScrewdriverMode.Dismantle)
		{
			TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
			if(core.canModify(player))
			{
				ItemStack[] contained = getComponentItems();
				if(contained.length > 0)
				{
					for(ItemStack is : contained)
						Helper.giveItemStack(player, is);
				}
				int d = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.decoBlock.blockID, d == 0 ? 2 : 4, 3);
				return true;
			}
			else
				player.addChatMessage(TardisCoreTileEntity.cannotModifyMessage);
		}
		
		boolean screwed = false;
		if(comps.size() > 0)
		{
			for(ITardisComponent te : comps.values())
			{
				if(te instanceof IScrewable)
					screwed = ((IScrewable)te).screw(mode, player) || screwed;
			}
		}
		return screwed;
	}

	@Override
	public boolean activate(EntityPlayer pl, int side)
	{
		ItemStack is = pl.getHeldItem();
		if(is != null)
		{
			Item i = is.getItem();
			if(i instanceof TardisComponentItem)
			{
				TardisCoreTileEntity core = Helper.getTardisCore(worldObj);
				if(core.canModify(pl))
				{
					int dam = is.getItemDamage();
					TardisTEComponent[] possComps = TardisTEComponent.values();
					if(dam >= 0 && dam < possComps.length)
					{
						TardisTEComponent rel = possComps[dam];
						if(addComponent(rel))
						{
							if(!pl.capabilities.isCreativeMode)
							{
								pl.inventory.decrStackSize(pl.inventory.currentItem, 1);
								//pl.inventory.setInventorySlotContents(pl.inventory.currentItem, null);
								pl.inventory.onInventoryChanged();
							}
						}
						else
							pl.sendChatToPlayer(new ChatMessageComponent().addText("That component has already been fitted"));
					}
				}
				else
					pl.sendChatToPlayer(new ChatMessageComponent().addText("You do not have permission to modify this TARDIS"));
			}
		}
		boolean activated = false;
		if(comps.size() > 0)
		{
			for(ITardisComponent te : comps.values())
			{
				if(te instanceof IActivatable)
					activated = ((IActivatable)te).activate(pl, side) || activated;
			}
		}
		return activated;
	}

	@Override
	public void neighbourUpdated(int neighbourBlockID)
	{
		if(comps.size() > 0)
		{
			for(ITardisComponent te : comps.values())
			{
				if(te instanceof IWatching)
					((IWatching)te).neighbourUpdated(neighbourBlockID);
			}
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
		compAdded = true;
		valid = true;
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		if(comps.size() > 0)
		{
			for(ITardisComponent comp : comps.values())
			{
				if(comp != null)
					comp.die();
			}
		}
		comps = null;
		valid = false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		TardisTEComponent[] possibleComps = TardisTEComponent.values();
		for(Integer key: comps.keySet())
		{
			if(key >= 0 && key < possibleComps.length)
			{
				TardisTEComponent relevantComp = possibleComps[key];
				NBTTagCompound compNBT = new NBTTagCompound();
				ITardisComponent te = comps.get(key);
				if(te != null)
				{
					//TardisOutput.print("TCompTE", "Writing " + relevantComp.componentName + " to nbt");
					compNBT.setBoolean("exists", true);
					te.writeToNBT(compNBT);
					nbt.setCompoundTag(relevantComp.componentName, compNBT);
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		TardisTEComponent[] possibleComps = TardisTEComponent.values();
		for(TardisTEComponent comp : possibleComps)
		{
			if(nbt.hasKey(comp.componentName))
			{
				TardisOutput.print("TCompTE", "Reading " + comp.componentName + " from nbt");
				addComponent(comp);
				ITardisComponent te = getComponent(comp);
				if(te != null)
				{
					te.readFromNBT(nbt.getCompoundTag(comp.componentName));
				}
			}
		}
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
		String c = "";
		for(Integer i : comps.keySet())
			c += i + ",";
		nbt.setString("c", c);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		String c = nbt.getString("c");
		String[] d = c.split(",");
		if(d.length > 0)
		{
			TardisTEComponent[] vals = TardisTEComponent.values();
			for(String e : d)
			{
				int i = Helper.toInt(e, -1);
				if(i != -1)
				{
					addComponent(vals[i]);
				}
			}
		}
	}

	@Override
	public DimentionalCoord[] findRemoteSide()
	{
		ITardisComponent grid = getComponent(TardisTEComponent.GRID);
		if(grid != null && grid instanceof TardisComponentGrid)
		{
			return ((TardisComponentGrid)grid).findRemoteSide();
		}
		return null;
	}

	@Override
	public WorldCoord getLocation()
	{
		return new WorldCoord(xCoord,yCoord,zCoord);
	}

	@Override
	public boolean isValid()
	{
		return valid && !compAdded;
	}

	@Override
	public void setPowerStatus(boolean hasPower)
	{
		ITardisComponent te = getComponent(TardisTEComponent.GRID);
		if(te != null)
			((TardisComponentGrid)te).setPowered(hasPower);
	}

	@Override
	public boolean isPowered()
	{
		ITardisComponent te = getComponent(TardisTEComponent.GRID);
		if(te != null)
			return ((TardisComponentGrid)te).getPowered();
		return false;
	}

	@Override
	public IGridInterface getGrid()
	{
		ITardisComponent te = getComponent(TardisTEComponent.GRID);
		if(te != null)
			return ((TardisComponentGrid)te).getGrid();
		return null;
	}

	@Override
	public void setGrid(IGridInterface gi)
	{
		ITardisComponent te = getComponent(TardisTEComponent.GRID);
		if(te != null)
			((TardisComponentGrid)te).setGrid(gi);
	}

	@Override
	public World getWorld()
	{
		return worldObj;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from)
	{
		if(!valid || compAdded)
			return false;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).canInterface(from);
		}
		return false;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).getEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getSizeInventory()
	{
		if(!valid || compAdded)
			return 0;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if(!valid || compAdded)
			return null;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if(!valid || compAdded)
			return null;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).decrStackSize(i,j);
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
		if(!valid || compAdded)
			return;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				((IInventory)comp).setInventorySlotContents(i,itemstack);
		}
	}

	@Override
	public String getInvName()
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).getInvName();
		}
		return null;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){return false;}

	@Override
	public void openChest(){	}

	@Override
	public void closeChest(){	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if(!valid || compAdded)
			return false;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).isItemValidForSlot(i,itemstack);
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).fill(from,resource,doFill);
			}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).drain(from,resource,doDrain);
			}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).drain(from,maxDrain,doDrain);
			}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).canFill(from,fluid);
			}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).canDrain(from,fluid);
			}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IFluidHandler)
					return ((IFluidHandler)comp).getTankInfo(from);
			}
		return null;
	}

	@Override
	public boolean shouldChunkload()
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IChunkLoader)
					return ((IChunkLoader)comp).shouldChunkload();
			}
		return false;
	}

	@Override
	public SimpleCoordStore coords()
	{
		if(coords == null)
			coords = new SimpleCoordStore(this);
		return coords;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IChunkLoader)
					return ((IChunkLoader)comp).loadable();
			}
		return null;
	}

}
