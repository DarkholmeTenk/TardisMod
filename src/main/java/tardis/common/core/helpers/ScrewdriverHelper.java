package tardis.common.core.helpers;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.network.DataPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tardis.Configs;
import tardis.TardisMod;
import tardis.api.ILinkable;
import tardis.api.ScrewdriverMode;
import tardis.common.dimension.TardisDataStore;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.items.extensions.ScrewTypeRegister;
import tardis.common.items.extensions.screwtypes.AbstractScrewdriverType;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.CoreTileEntity;

public class ScrewdriverHelper
{
	public final int id;
	protected ItemStack itemstack;
	private int perms;
	private ScrewdriverMode mode = ScrewdriverMode.Reconfigure;
	private SimpleCoordStore linkSCS;
	private String schemaCat;
	private String schema;
	private AbstractScrewdriverType type = TardisMod.defaultType;
	private String displayName;

	public String owner;

	private boolean dirty		= false;

	private static boolean isVoid(String s)
	{
		return (s == null) || s.isEmpty();
	}

	{
		resetPermissions(true);
	}

	protected ScrewdriverHelper(NBTTagCompound nbt, int _id)
	{
		id = _id;
		readFromNBT(nbt);
	}

	protected ScrewdriverHelper(ItemStack is, int _id)
	{
		this(is.stackTagCompound, _id);
		itemstack = is;
		if(is.hasDisplayName())
			displayName = is.getDisplayName();
	}

	private void markDirty()
	{
		if(itemstack != null)
		{
			if(itemstack.stackTagCompound == null)
				itemstack.stackTagCompound = new NBTTagCompound();
			writeToNBT(itemstack.stackTagCompound);
			if(ServerHelper.isServer())
			{
				DataPacket packet = new DataPacket(itemstack.stackTagCompound,TardisPacketHandler.screwFlag);
				DarkcoreMod.networkChannel.sendToAll(packet);
			}
		}
		dirty = true;
	}

	public boolean isDirty()
	{
		boolean d = dirty;
		dirty = false;
		return d;
	}

	public void clear()
	{
		itemstack = null;
	}

	protected void setItemStack(ItemStack is)
	{
		itemstack = is;
		if((is != null) && (is.stackTagCompound != null))
			readFromNBT(is.stackTagCompound);
		markDirty();
	}

	public ItemStack getItemStack()
	{
		if(itemstack != null)
			return itemstack;
		itemstack = new ItemStack(TardisMod.screwItem, 1);
		itemstack.stackTagCompound = new NBTTagCompound();
		writeToNBT(itemstack.stackTagCompound);
		if(displayName != null)
			itemstack.setStackDisplayName(displayName);
		return itemstack;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt == null) return;
		owner = nbt.getString("owner");
		mode = ScrewdriverMode.get(nbt.getInteger("scMo"));
		perms = nbt.getInteger("perm");
		schemaCat = nbt.hasKey("schemaCat") ? nbt.getString("schemaCat") : null;
		schema = nbt.hasKey("schemaName") ? nbt.getString("schemaName") : null;
		linkSCS = nbt.hasKey("linkscs") ? SimpleCoordStore.readFromNBT(nbt, "linkscs") : null;
		displayName = nbt.hasKey("dispName") ? nbt.getString("dispName") : null;
		type = ScrewTypeRegister.get(nbt);
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("uuid", id);
		nbt.setInteger("scMo", mode.ordinal());
		nbt.setInteger("perm", perms);
		if(owner != null) nbt.setString("owner", owner);
		if(linkSCS != null)
			linkSCS.writeToNBT(nbt, "linkscs");
		else if(nbt.hasKey("linkscs"))
			nbt.removeTag("linkscs");
		type.writeToNBT(nbt);
		if(schema != null)
		{
			nbt.setString("schemaName", schema);
			if(schemaCat != null)
				nbt.setString("schemaCat", schemaCat);
		}
		if(displayName != null)
			nbt.setString("dispName", displayName);
	}

	public void writeToNBT(NBTTagCompound nbt, String name)
	{
		NBTTagCompound subNBT = new NBTTagCompound();
		writeToNBT(subNBT);
		nbt.setTag(name, subNBT);
	}

	public static final int	maxPerms	= 0xFF;
	public static final int	minPerms	= 0xCD;

	public boolean hasPermission(ScrewdriverMode testMode)
	{
		int toCheck = (int) Math.pow(2, testMode.ordinal());
		return (perms & toCheck) == toCheck;
	}

	public void setPermission(ScrewdriverMode setMode, boolean newValue)
	{
		int value = (int) Math.pow(2,setMode.ordinal());
		if(newValue)
			perms = perms | value;
		else
			perms = perms & ~value;
		markDirty();
	}

	public void togglePermission(ScrewdriverMode togMode)
	{
		int value = (int) Math.pow(2,togMode.ordinal());
		perms = perms ^ value;
		markDirty();
	}

	/**
	 * Resets permissions to factory default or no permissions
	 * @param factory if true, permissions are reset to factory default, else all permissions are removed
	 */
	private void resetPermissions(boolean factory)
	{
		if(factory)
			perms = maxPerms;
		else
			perms = minPerms;
	}

	public String getOwner()
	{
		if(isVoid(owner)) return "Unknown";
		return owner;
	}

	public void setOwner(String newOwner)
	{
		if(isVoid(owner))
			resetPermissions(true);
		else if(!owner.equals(newOwner))
			resetPermissions(false);
		owner = newOwner;
		markDirty();
	}

	public Integer getLinkedDimID()
	{
		if(isVoid(owner)) return null;
		return TardisMod.plReg.getDimension(owner);
	}

	public CoreTileEntity getLinkedCore()
	{
		Integer dim = getLinkedDimID();
		if(dim != null)
			return Helper.getTardisCore(dim);
		return null;
	}

	public TardisDataStore getLinkedDS()
	{
		Integer dim = getLinkedDimID();
		if(dim != null)
			return Helper.getDataStore(dim);
		return null;
	}

	private ILinkable getILinkable(SimpleCoordStore scs)
	{
		if(scs != null)
		{
			TileEntity te = scs.getTileEntity();
			Block b = scs.getBlock();
			if(te instanceof ILinkable)
				return (ILinkable) te;
			if(b instanceof ILinkable)
				return (ILinkable) b;
		}
		return null;
	}

	/**
	 * @return the ILinkable associated with the getLinkSCS coords
	 */
	public ILinkable getLinkILinkable()
	{
		SimpleCoordStore scs = getLinkSCS();
		return getILinkable(scs);
	}

	public SimpleCoordStore getLinkSCS()
	{
		return linkSCS;
	}

	public void setLinkSCS(SimpleCoordStore scs)
	{
		linkSCS = scs;
		markDirty();
	}

	public double[] getColors()
	{
		return mode.c;
	}

	public String getSchemaCat()
	{
		if(isVoid(schemaCat)) return null;
		return schemaCat;
	}

	public String getSchemaName()
	{
		if(isVoid(schemaCat)) return null;
		return schema;
	}

	public String getSchemaDisplay()
	{
		if(isVoid(schema)) return "--None--";
		if(isVoid(schemaCat)) return schema;
		return schemaCat + " - " + schema;
	}

	public void setSchema(String cat, String name)
	{
		schemaCat = cat;
		schema = name;
		markDirty();
	}

	public ScrewdriverMode getMode()
	{
		return mode;
	}

	public boolean isModeValid(EntityPlayer pl, ScrewdriverMode mode)
	{
		if(!hasPermission(mode)) return false;
		if((mode == ScrewdriverMode.Link) && !Configs.enableLinking) return false;
		if(mode.requiredFunction != null)
		{
			TardisDataStore ds = getLinkedDS();
			if((ds == null) || !ds.hasFunction(mode.requiredFunction)) return false;
		}
		{
			boolean isTardisWorld = Helper.isTardisWorld(pl.worldObj);
			switch(mode)
			{
				case Locate: case Recall: return !isTardisWorld;
				case Schematic: return isTardisWorld;
				default: return true;
			}
		}
	}

	public void switchMode(EntityPlayer pl)
	{
		int startMode = mode.ordinal();
		ScrewdriverMode[] modes = ScrewdriverMode.values();
		int modeLength = modes.length;
		int newMode = (startMode + 1) % modeLength;
		while(!isModeValid(pl, modes[newMode]) && (newMode != startMode)) newMode = (newMode + 1) % modeLength;
		mode = modes[newMode];
		markDirty();
	}

	public void render()
	{
		type.render(this);
	}

	public AbstractScrewdriverType getType()
	{
		return type;
	}

	public void setScrewdriverType(AbstractScrewdriverType newType)
	{
		type = newType;
		markDirty();
	}

	public void cycleScrewdriverType()
	{
		int length = ScrewTypeRegister.size();
		int index = ScrewTypeRegister.getIndex(type);
		index = (index + 1) % length;
		type = ScrewTypeRegister.get(index);
		markDirty();
	}

	private boolean clearLinkSCS(EntityPlayer pl, boolean mess)
	{
		if(mess && ServerHelper.isServer())
			ServerHelper.sendString(pl, SonicScrewdriverItem.screwName, "Link target has been cleared (no linkable object)");
		setLinkSCS(null);
		return true;
	}

	public boolean linkUsed(EntityPlayer pl, SimpleCoordStore usedPos)
	{
		ILinkable usedOn = getILinkable(usedPos);
		SimpleCoordStore linkSCS = getLinkSCS();
		ILinkable linkLinkable = getLinkILinkable();
		if((linkSCS != null) && (linkLinkable == null)) //Linkable pos no longer an instance of ILinkable
			return clearLinkSCS(pl,true);
		if((linkSCS != null) && linkSCS.equals(usedPos)) //Trying to link the same place as before
		{
			if(usedOn != null)
				if(usedOn.unlink(pl, usedPos))
					if(ServerHelper.isServer())
						System.out.println("Removed links");
			return clearLinkSCS(pl,true);
		}

		if((usedOn == null) || !usedOn.isLinkable(usedPos)) //Used on something that isn't ILinkable, so clear the link
		{
			if(linkSCS != null)
				return clearLinkSCS(pl,true);
			return false;
		}

		if(linkLinkable == null) //Used on something good but we have no link currently
		{
			setLinkSCS(usedPos);
			if(ServerHelper.isServer())
				ServerHelper.sendString(pl, SonicScrewdriverItem.screwName, "Link target set to " + usedPos);
			return true;
		}

		if((usedOn != null) && (linkLinkable != null))
		{
			if(usedOn.link(pl, usedPos, linkSCS))
			{
				if(linkLinkable.link(pl, linkSCS, usedPos))
				{
					clearLinkSCS(pl, false);
					if(ServerHelper.isServer())
						ServerHelper.sendString(pl, SonicScrewdriverItem.screwName, "Linked " + linkSCS + " to " + usedPos);
					return true;
				}
				else
					usedOn.unlink(pl, usedPos);
			}
		}

		return false;
	}
}
