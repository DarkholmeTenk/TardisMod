package tardis.common.tileents;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import io.darkcraft.darkcore.mod.interfaces.IActivatablePrecise;
import io.darkcraft.darkcore.mod.interfaces.IBlockUpdateDetector;
import io.darkcraft.darkcore.mod.interfaces.IChunkLoader;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;
import tardis.common.dimension.TardisDataStore;
import tardis.common.integration.ae.AEHelper;
import tardis.common.integration.other.CofHCore;
import tardis.common.integration.other.IC2;
import tardis.common.integration.other.Thaumcraft;
import tardis.common.items.ComponentItem;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.tileents.components.ComponentAspect;
import tardis.common.tileents.components.ITardisComponent;
import tardis.common.tileents.components.TardisTEComponent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value={
		@Optional.Interface(iface="cofh.api.energy.IEnergyHandler",modid=CofHCore.modname),
		@Optional.Interface(iface="appeng.api.networking.IGridHost",modid=AEHelper.modname),
		@Optional.Interface(iface="thaumcraft.api.aspects.IEssentiaTransport",modid=Thaumcraft.modname),
		@Optional.Interface(iface="ic2.api.energy.tile.IEnergySource",modid=IC2.modname),
		@Optional.Interface(iface="ic2.api.energy.tile.IEnergySink",modid=IC2.modname)
})
public class ComponentTileEntity extends AbstractTileEntity implements IActivatablePrecise, IBlockUpdateDetector,
		IGridHost, IEnergyHandler, IInventory, IFluidHandler, IChunkLoader, IEssentiaTransport, IEnergySource, IEnergySink
{
	private HashMap<Integer, ITardisComponent>	comps			= new HashMap<Integer, ITardisComponent>();
	private boolean								valid			= false;
	private boolean								compAdded		= false;
	private Boolean								inside			= null;


	public boolean isComponentValid(TardisTEComponent comp)
	{
		if (inside == null)
			inside = Helper.isTardisWorld(worldObj);
		return comp.isValid(inside);
	}

	public boolean addComponent(TardisTEComponent comp)
	{
		if (!hasComponent(comp) && (getNumComponents() < Configs.maxComponents))
		{
			if (isComponentValid(comp) || !valid)
			{
				compAdded = true;
				comps.put(comp.ordinal(), comp.baseObj.create(this));
				return true;
			}
		}
		return false;

	}

	public int getNumComponents()
	{
		int c = 0;
		for (TardisTEComponent comp : TardisTEComponent.values())
			c += hasComponent(comp) ? 1 : 0;
		return c;
	}

	public boolean hasComponent(TardisTEComponent comp)
	{
		if (comps != null)
			return comps.containsKey(comp.ordinal());
		return false;
	}

	public ITardisComponent getComponent(TardisTEComponent comp)
	{
		if (hasComponent(comp))
			return comps.get(comp.ordinal());
		return null;
	}

	public ItemStack[] getComponentItems()
	{
		ItemStack[] retIS = new ItemStack[comps.size()];
		if (retIS.length > 0)
		{
			int i = 0;
			for (Integer compVal : comps.keySet())
			{
				ItemStack is = new ItemStack(TardisMod.componentItem, 1, compVal);
				retIS[i] = is;
				i++;
			}
		}
		return retIS;
	}

	protected void reviveComps()
	{
		if (comps == null)
			return;
		if (comps.size() > 0)
		{
			for (ITardisComponent comp : comps.values())
			{
				if (comp != null)
					comp.revive(this);
			}
		}
	}

	protected void killComps()
	{
		if (comps.size() > 0)
		{
			for (ITardisComponent comp : comps.values())
			{
				if (comp != null)
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
	public void init()
	{
		inside = Helper.isTardisWorld(worldObj);
		compAdded = false;
		reviveComps();
		if (ServerHelper.isServer() && valid)
		{
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
			sendUpdate();
		}

	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (compAdded)
			init();

		if (comps.size() > 0)
		{
			for (ITardisComponent comp : comps.values())
				comp.updateTick();
		}
	}

	protected void dismantle(EntityPlayer pl)
	{
		if(ServerHelper.isClient())
			return;
		Block b = worldObj.getBlock(xCoord, yCoord, zCoord);
		ItemStack[] contained = getComponentItems();
		if (contained.length > 0)
		{
			for (ItemStack is : contained)
				WorldHelper.giveItemStack(pl, is);
		}
		if(b == TardisMod.componentBlock)
		{
			int d = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.decoBlock, d == 0 ? 2 : 4, 3);
		}
		else if(b == TardisMod.colorableOpenRoundelBlock)
		{
			int d = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.colorableRoundelBlock, d, 3);
		}
	}


	public boolean screw(ScrewdriverMode mode, EntityPlayer player)
	{
		if(ServerHelper.isClient())
			return true;
		if (mode == ScrewdriverMode.Dismantle)
		{
			TardisDataStore ds = getDS();
			if ((ds == null) || ds.hasPermission(player, TardisPermission.ROUNDEL))
			{
				dismantle(player);
				return true;
			}
			else
				player.addChatMessage(CoreTileEntity.cannotModifyMessage);
		}
		if (mode == ScrewdriverMode.Link)
		{

		}

		/*
		boolean screwed = false;
		if (comps.size() > 0)
		{
			for (ITardisComponent te : comps.values())
			{
				if (te instanceof IScrewable)
					screwed = ((IScrewable) te).screw(mode, player) || screwed;
			}
		}
		return screwed;*/
		return false;
	}

	private double getAngle(int side, float x, float y, float z)
	{
		float i = 0;
		float j = 0;
		switch(side)
		{
			case 0: case 1: i = 1 - x; j = 1 - z; break;
			case 2: i = y; j = 1 - x; break;
			case 3: i = y; j = x; break;
			case 4: i = y; j = z; break;
			case 5: i = y; j = 1 - z; break;
		}
		i -= 0.5f;
		j -= 0.5f;
		return Math.atan2(j,i);
	}

	private int getSlot(int n, double angle)
	{
		double range = (2*Math.PI)/n;
		angle += (range / 2);
		if(angle < 0)
			angle += (Math.PI*2);
		return MathHelper.floor(angle/range);
	}

	private ITardisComponent getComponent(int slot)
	{
		{
			int i = 0;
			for(ITardisComponent c : comps.values())
				if(i++ == slot)
					return c;
		}
		return null;
	}

	@Override
	public boolean activate(EntityPlayer pl, int s, float x, float y, float z)
	{
		if(ServerHelper.isClient()) return true;
		ItemStack is = pl.getHeldItem();
		if (is != null)
		{
			Item i = is.getItem();
			if (i instanceof ComponentItem)
			{
				TardisDataStore ds = getDS();
				if ((ds == null) || ds.hasPermission(pl, TardisPermission.ROUNDEL))
				{
					int dam = is.getItemDamage();
					TardisTEComponent[] possComps = TardisTEComponent.values();
					if ((dam >= 0) && (dam < possComps.length))
					{
						TardisTEComponent rel = possComps[dam];
						if(isComponentValid(rel))
						{
							if (addComponent(rel))
							{
								if (!pl.capabilities.isCreativeMode)
								{
									pl.inventory.decrStackSize(pl.inventory.currentItem, 1);
									pl.inventory.markDirty();
								}
								return true;
							}
							else
								ServerHelper.sendString(pl, "That component has already been fitted");
						}
						else
							ServerHelper.sendString(pl, "That component is not valid " + (inside ? "inside" : "outside of") + " a TARDIS");
					}
				}
				else
					ServerHelper.sendString(pl, "You do not have permission to modify this");
			}
		}
		if(SonicScrewdriverItem.isScrewdriver(is))
		{
			ScrewdriverMode mode = SonicScrewdriverItem.getMode(is);
			if(mode == ScrewdriverMode.Dismantle)
			{
				TardisDataStore ds = getDS();
				if((ds == null) || ds.hasPermission(pl, TardisPermission.ROUNDEL))
				{
					dismantle(pl);
					return true;
				}
				else
				{
					ServerHelper.sendString(pl, "You do not have permission to modify this");
					return false;
				}
			}
		}
		int n = comps.size();
		ITardisComponent o = getComponent(getSlot(n,getAngle(s, x,y,z)));
		if(o != null)
		{
			if(is != null)
			{
				if(SonicScrewdriverItem.isScrewdriver(is) && (o instanceof IScrewable))
				{
					IScrewable screwable = (IScrewable)o;
					ScrewdriverMode mode = SonicScrewdriverItem.getMode(is);
					if(screwable.screw(ScrewdriverHelperFactory.get(is), mode, pl)) return true;
				}
			}
			if(o instanceof IActivatable)
			{
				return ((IActivatable)o).activate(pl, s);
			}
		}
		return false;
	}

	@Override
	public void blockUpdated(Block neighbourBlock)
	{
		if (comps.size() > 0)
		{
			for (ITardisComponent te : comps.values())
			{
				if (te instanceof IBlockUpdateDetector)
					((IBlockUpdateDetector) te).blockUpdated(neighbourBlock);
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
		if (comps.size() > 0)
		{
			for (ITardisComponent comp : comps.values())
			{
				if (comp != null)
					comp.die();
			}
		}
		comps = null;
		valid = false;
	}

	@Override
	public void onChunkUnload()
	{
		if (comps.size() > 0)
		{
			for (ITardisComponent comp : comps.values())
			{
				if (comp != null)
					comp.die();
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		TardisTEComponent[] possibleComps = TardisTEComponent.values();
		for (Integer key : comps.keySet())
		{
			if ((key >= 0) && (key < possibleComps.length))
			{
				TardisTEComponent relevantComp = possibleComps[key];
				NBTTagCompound compNBT = new NBTTagCompound();
				ITardisComponent te = comps.get(key);
				if (te != null)
				{
					// TardisOutput.print("TCompTE", "Writing " +
					// relevantComp.componentName + " to nbt");
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
		for (TardisTEComponent comp : possibleComps)
		{
			if (nbt.hasKey(comp.componentName))
			{
				addComponent(comp);
				ITardisComponent te = getComponent(comp);
				if (te != null)
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
		for (Integer i : comps.keySet())
			c += i + ",";
		nbt.setString("c", c);
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
		String c = nbt.getString("c");
		String[] d = c.split(",");
		if (d.length > 0)
		{
			TardisTEComponent[] vals = TardisTEComponent.values();
			for (String e : d)
			{
				int i = MathHelper.toInt(e, -1);
				if (i != -1)
				{
					addComponent(vals[i]);
				}
			}
		}
	}

	public CoreTileEntity getCore()
	{
		CoreTileEntity core = Helper.getTardisCore(this);
		return core;
	}

	public TardisDataStore getDS()
	{
		return Helper.getDataStore(this);
	}

	public IArtronEnergyProvider getArtronEnergyProvider()
	{
		CoreTileEntity core = getCore();
		if (core != null)
			return core;
		return null;
	}

	public boolean isValid()
	{
		return valid && !compAdded;
	}

	@Override
	@Optional.Method(modid=CofHCore.modname)
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
	{
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IEnergyHandler)
				return ((IEnergyHandler) comp).receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}

	@Override
	@Optional.Method(modid=CofHCore.modname)
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IEnergyHandler)
				return ((IEnergyHandler) comp).extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}

	@Override
	@Optional.Method(modid=CofHCore.modname)
	public boolean canConnectEnergy(ForgeDirection from)
	{
		if (!valid || compAdded)
			return true;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IEnergyHandler)
				return ((IEnergyHandler) comp).canConnectEnergy(from);
		}
		return false;
	}

	@Override
	@Optional.Method(modid=CofHCore.modname)
	public int getEnergyStored(ForgeDirection from)
	{
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IEnergyHandler)
				return ((IEnergyHandler) comp).getEnergyStored(from);
		}
		return 0;
	}

	@Override
	@Optional.Method(modid=CofHCore.modname)
	public int getMaxEnergyStored(ForgeDirection from)
	{
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IEnergyHandler)
				return ((IEnergyHandler) comp).getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getSizeInventory()
	{
		if (!valid || compAdded)
			return 0;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				return ((IInventory) comp).getSizeInventory();
		}
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		if (!valid || compAdded)
			return null;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				return ((IInventory) comp).getStackInSlot(i);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (!valid || compAdded)
			return null;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				return ((IInventory) comp).decrStackSize(i, j);
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
		if (!valid || compAdded)
			return;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				((IInventory) comp).setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInventoryName()
	{
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				return ((IInventory) comp).getInventoryName();
		}
		return null;
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
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if (!valid || compAdded)
			return false;
		for (ITardisComponent comp : comps.values())
		{
			if (comp instanceof IInventory)
				return ((IInventory) comp).isItemValidForSlot(i, itemstack);
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).fill(from, resource, doFill);
			}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).drain(from, resource, doDrain);
			}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).drain(from, maxDrain, doDrain);
			}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).canFill(from, fluid);
			}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).canDrain(from, fluid);
			}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IFluidHandler)
					return ((IFluidHandler) comp).getTankInfo(from);
			}
		return null;
	}

	@Override
	public boolean shouldChunkload()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IChunkLoader)
					return ((IChunkLoader) comp).shouldChunkload();
			}
		return false;
	}

	@Override
	public SimpleCoordStore coords()
	{
		if (coords == null)
			coords = new SimpleCoordStore(this);
		return coords;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IChunkLoader)
					return ((IChunkLoader) comp).loadable();
			}
		return null;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	@Optional.Method(modid=AEHelper.modname)
	public IGridNode getGridNode(ForgeDirection dir)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IGridHost)
					return ((IGridHost) comp).getGridNode(dir);
			}
		return null;
	}

	@Override
	@Optional.Method(modid=AEHelper.modname)
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IGridHost)
					return ((IGridHost) comp).getCableConnectionType(dir);
			}
		return AECableType.NONE;
	}

	@Override
	@Optional.Method(modid=AEHelper.modname)
	public void securityBreak()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IGridHost)
					((IGridHost) comp).securityBreak();
			}
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public AspectList getAspects()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).getAspects();
			}
		return null;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public void setAspects(AspectList aspects)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					((IAspectSource) comp).setAspects(aspects);
			}
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public boolean doesContainerAccept(Aspect tag)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).doesContainerAccept(tag);
			}
		return false;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public int addToContainer(Aspect tag, int amount)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).addToContainer(tag,amount);
			}
		return amount;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public boolean takeFromContainer(Aspect tag, int amount)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).takeFromContainer(tag,amount);
			}
		return false;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public boolean takeFromContainer(AspectList ot)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).takeFromContainer(ot);
			}
		return false;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public boolean doesContainerContainAmount(Aspect tag, int amount)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).doesContainerContainAmount(tag,amount);
			}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Optional.Method(modid=Thaumcraft.modname)
	public boolean doesContainerContain(AspectList ot)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).doesContainerContain(ot);
			}
		return false;
	}

	@Optional.Method(modid=Thaumcraft.modname)
	public int containerContains(Aspect tag)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource) comp).containerContains(tag);
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public boolean isConnectable(ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return true;
			}
		return false;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public boolean canInputFrom(ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return true;
			}
		return false;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public boolean canOutputTo(ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return true;
			}
		return false;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public void setSuction(Aspect aspect, int amount)
	{
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public Aspect getSuctionType(ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof ComponentAspect)
					return ((ComponentAspect)comp).getSuctionAspect();
			}
		return null;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public int getSuctionAmount(ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof ComponentAspect)
					return ((ComponentAspect)comp).getSuction();
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return ((IAspectSource)comp).takeFromContainer(aspect, amount) ? amount : 0;
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IAspectSource)
					return amount - ((IAspectSource)comp).addToContainer(aspect, amount);
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public Aspect getEssentiaType(ForgeDirection face)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public int getEssentiaAmount(ForgeDirection face)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public int getMinimumSuction()
	{
		return 0;
	}

	@Override
	@Optional.Method(modid=Thaumcraft.modname)
	public boolean renderExtendedTube()
	{
		return false;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySink)
					return ((IEnergySink)comp).acceptsEnergyFrom(emitter, direction);
			}
		return false;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public double getDemandedEnergy()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySink)
					return ((IEnergySink)comp).getDemandedEnergy();
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public int getSinkTier()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySink)
					return ((IEnergySink)comp).getSinkTier();
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySink)
					return ((IEnergySink)comp).injectEnergy(directionFrom, amount, voltage);
			}
		return amount;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySource)
					return ((IEnergySource)comp).emitsEnergyTo(receiver, direction);
			}
		return false;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public double getOfferedEnergy()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySource)
					return ((IEnergySource)comp).getOfferedEnergy();
			}
		return 0;
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public void drawEnergy(double amount)
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySource)
					((IEnergySource)comp).drawEnergy(amount);
			}
	}

	@Override
	@Optional.Method(modid=IC2.modname)
	public int getSourceTier()
	{
		if (valid && !compAdded)
			for (ITardisComponent comp : comps.values())
			{
				if (comp instanceof IEnergySource)
					return ((IEnergySource)comp).getSourceTier();
			}
		return 0;
	}

}
