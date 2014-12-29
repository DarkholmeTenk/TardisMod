package tardis.common.tileents;

import java.util.HashMap;

import cofh.api.energy.IEnergyHandler;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
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
import tardis.common.tileents.components.TardisTEComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TardisComponentTileEntity extends TardisAbstractTileEntity implements IScrewable, IActivatable, IWatching,
																		IGridHost, IEnergyHandler, IInventory, IFluidHandler, IChunkLoader
{
	private HashMap<Integer,ITardisComponent> comps = new HashMap<Integer,ITardisComponent>();
	private boolean valid = false;
	private boolean inited = false;
	private boolean compAdded = false;
	private Boolean inside = null;
	
	public boolean addComponent(TardisTEComponent comp)
	{
		if(!hasComponent(comp))
		{
			if(inside == null)
				inside = Helper.isTardisWorld(worldObj);
			if(comp.isValid(inside))
			{
				compAdded = true;
				comps.put(comp.ordinal(), comp.baseObj.create(this));
				return true;
			}
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
				ItemStack is = new ItemStack(TardisMod.componentItem,1,compVal);
				retIS[i] = is;
				i++;
			}
		}
		return retIS;
	}
	
	protected void reviveComps()
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
	
	protected void killComps()
	{
		if(comps.size() > 0)
		{
			for(ITardisComponent comp : comps.values())
			{
				if(comp != null)
					comp.die();
			}
		}
	}
	
	protected void restart()
	{
		killComps();
		reviveComps();
		updateNeighbours();
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!inited || compAdded)
		{
			inside = Helper.isTardisWorld(worldObj);
			compAdded = false;
			reviveComps();
			inited = true;
			if(Helper.isServer() && valid)
			{
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord,yCoord,zCoord));
				sendUpdate();
			}
		}
		
		if(comps.size() > 0)
		{
			for(ITardisComponent comp : comps.values())
				comp.updateTick();
		}
	}
	
	protected void dismantle(EntityPlayer pl)
	{
		int d = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.decoBlock, d == 0 ? 2 : 4, 3);
	}
	
	@Override
	public boolean screw(TardisScrewdriverMode mode, EntityPlayer player)
	{
		if(mode == TardisScrewdriverMode.Dismantle)
		{
			TardisCoreTileEntity core = getCore();
			if(core == null || core.canModify(player))
			{
				ItemStack[] contained = getComponentItems();
				if(contained.length > 0)
				{
					for(ItemStack is : contained)
						Helper.giveItemStack(player, is);
				}
				dismantle(player);
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
				TardisCoreTileEntity core = getCore();
				if(core == null || core.canModify(pl))
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
								pl.inventory.markDirty();
							}
						}
						else
							pl.addChatMessage(new ChatComponentText("That component has already been fitted"));
					}
				}
				else
					pl.addChatMessage(new ChatComponentText("You do not have permission to modify this"));
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
	public void neighbourUpdated(Block neighbourBlock)
	{
		if(comps.size() > 0)
		{
			for(ITardisComponent te : comps.values())
			{
				if(te instanceof IWatching)
					((IWatching)te).neighbourUpdated(neighbourBlock);
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
					nbt.setTag(relevantComp.componentName, compNBT);
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
	
	public TardisCoreTileEntity getCore()
	{
		TardisCoreTileEntity core = Helper.getTardisCore(this);
		return core;
	}
	
	public boolean isValid()
	{
		return valid && !compAdded;
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
	public boolean canConnectEnergy(ForgeDirection from)
	{
		if(!valid || compAdded)
			return true;
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IEnergyHandler)
				return ((IEnergyHandler)comp).canConnectEnergy(from);
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
	public String getInventoryName()
	{
		for(ITardisComponent comp : comps.values())
		{
			if(comp instanceof IInventory)
				return ((IInventory)comp).getInventoryName();
		}
		return null;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){return false;}

	@Override
	public void openInventory(){	}

	@Override
	public void closeInventory(){	}

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

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public IGridNode getGridNode(ForgeDirection dir)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IGridHost)
					return ((IGridHost)comp).getGridNode(dir);
			}
		return null;
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IGridHost)
					return ((IGridHost)comp).getCableConnectionType(dir);
			}
		return AECableType.NONE;
	}

	@Override
	public void securityBreak()
	{
		if(valid && !compAdded)
			for(ITardisComponent comp : comps.values())
			{
				if(comp instanceof IGridHost)
					((IGridHost)comp).securityBreak();
			}
	}

}
