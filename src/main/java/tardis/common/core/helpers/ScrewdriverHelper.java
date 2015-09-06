package tardis.common.core.helpers;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.ScrewdriverMode;
import tardis.common.dimension.TardisDataStore;
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

	public String owner;

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
	}

	protected ScrewdriverHelper(ItemStack is, int _id)
	{
		this(is.stackTagCompound, _id);
		itemstack = is;
		markDirty();
	}

	private void markDirty()
	{
		if(itemstack != null)
		{
			if(itemstack.stackTagCompound == null)
				itemstack.stackTagCompound = new NBTTagCompound();
			writeToNBT(itemstack.stackTagCompound);
		}
	}

	public void clear()
	{
		itemstack = null;
	}

	protected void setItemStack(ItemStack is)
	{
		itemstack = is;
		markDirty();
	}

	public ItemStack getItemStack()
	{
		if(itemstack != null)
			return itemstack;
		itemstack = new ItemStack(TardisMod.screwItem, 1);
		itemstack.stackTagCompound = new NBTTagCompound();
		writeToNBT(itemstack.stackTagCompound);
		return itemstack;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		owner = nbt.getString("owner");
		mode = ScrewdriverMode.get(nbt.getInteger("scMo"));
		perms = nbt.getInteger("perm");
		schemaCat = nbt.getString("schemaCat");
		schema = nbt.getString("schemaName");
		if(nbt.hasKey("linkscs")) linkSCS = SimpleCoordStore.readFromNBT(nbt, "linkscs");
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
}
