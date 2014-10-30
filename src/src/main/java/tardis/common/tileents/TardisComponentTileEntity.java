package tardis.common.tileents;

import java.util.HashMap;

import appeng.api.DimentionalCoord;
import appeng.api.WorldCoord;
import appeng.api.me.tiles.IGridTeleport;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import tardis.TardisMod;
import tardis.api.IActivatable;
import tardis.api.IScrewable;
import tardis.api.IWatching;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.items.TardisComponentItem;
import tardis.common.tileents.components.ITardisComponent;
import tardis.common.tileents.components.TardisComponentGrid;
import tardis.common.tileents.components.TardisTEComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

public class TardisComponentTileEntity extends TardisAbstractTileEntity implements IScrewable, IActivatable, IWatching, IGridTeleport, IGridTileEntity
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
		return comps.containsKey(comp.ordinal());
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
		if(!inited || compAdded)
		{
			compAdded = false;
			reviveComps();
			inited = true;
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
				player.addChatMessage("You do not have permission to modify this TARDIS");
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
								pl.inventory.setInventorySlotContents(pl.inventory.currentItem, null);
								//pl.inventory.mainInventory[pl.inventory.currentItem] = null;
								pl.inventory.onInventoryChanged();
							}
							ITardisComponent comp = getComponent(rel);
							//comp.revive(this);
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

	private void initialiseComponents()
	{
		if(worldObj != null)
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			TardisTEComponent[] possComps = TardisTEComponent.values();
			if(meta > 0 && meta < possComps.length)
			{
				if(!comps.containsKey(meta))
					comps.put(meta, possComps[meta].baseObj.create(this));
			}
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
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
					TardisOutput.print("TCompTE", "Writing " + relevantComp.componentName + " to nbt");
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
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
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

}
