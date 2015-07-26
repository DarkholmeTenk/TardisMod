package tardis.common.dimension;

import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tardis.TardisMod;
import tardis.api.TardisFunction;
import tardis.api.TardisPermission;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.dimension.damage.TardisDamageSystem;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.TardisTileEntity;
import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;
import tardis.common.tileents.extensions.upgrades.factory.UpgradeFactory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TardisDataStore extends AbstractWorldDataStore
{
	public final int										dimID;
	public final TardisDamageSystem							damage;

	private int												pExtW			= 0;
	private int												pExtX			= 0;
	private int												pExtY			= 0;
	private int												pExtZ			= 0;

	public int												exteriorWorld;
	public int												exteriorX;
	public int												exteriorY;
	public int												exteriorZ;

	private volatile double									tardisXP		= 0;
	private volatile int									tardisLevel		= 0;
	private volatile HashMap<TardisUpgradeMode, Integer>	upgradeLevels	= new HashMap<TardisUpgradeMode, Integer>();
	private int												rfStored;
	private ItemStack[]										items			= new ItemStack[TardisMod.numInvs];
	private FluidStack[]									fluids			= new FluidStack[TardisMod.numTanks];
	private AspectList										aspectList		= new AspectList();
	public int												maxSuck			= 16;
	public Aspect											maxSuckT		= null;

	private HashMap<Integer, Integer>						permissionList	= new HashMap();
	public AbstractUpgrade[]								upgrades		= new AbstractUpgrade[8];
	private int tt = 0;

	public TardisDataStore(String n)
	{
		super(n);
		dimID = -1;
		damage = new TardisDamageSystem(this);
	}

	public TardisDataStore(int _dimID)
	{
		super("tardisIDS");
		dimID = _dimID;
		damage = new TardisDamageSystem(this);
	}

	public void markMaybeDirty()
	{
		if ((pExtX != exteriorX) || (pExtY != exteriorY) || (pExtZ != exteriorZ) || (pExtW != exteriorWorld)) markDirty();
	}

	@Override
	public void markDirty()
	{
		pExtW = exteriorWorld;
		pExtX = exteriorX;
		pExtY = exteriorY;
		pExtZ = exteriorZ;
		super.markDirty();
	}

	@Override
	public int getDimension()
	{
		return dimID;
	}

	public void setExterior(World w, int x, int y, int z)
	{
		exteriorWorld = w.provider.dimensionId;
		exteriorX = x;
		exteriorY = y;
		exteriorZ = z;
		markDirty();
	}

	public void linkToExterior(TardisTileEntity exterior)
	{
		setExterior(exterior.getWorldObj(), exterior.xCoord, exterior.yCoord, exterior.zCoord);
	}

	public TardisTileEntity getExterior()
	{
		World w = WorldHelper.getWorld(exteriorWorld);
		if (w != null)
		{
			TileEntity te = w.getTileEntity(exteriorX, exteriorY, exteriorZ);
			if (te instanceof TardisTileEntity) return (TardisTileEntity) te;
		}
		return null;
	}

	public boolean hasValidExterior()
	{
		World w = WorldHelper.getWorld(exteriorWorld);
		if (w != null)
		{
			if (w.getBlock(exteriorX, exteriorY, exteriorZ) == TardisMod.tardisBlock) return true;
		}
		return false;
	}

	public double getXP()
	{
		return tardisXP;
	}

	public double addXP(double a)
	{
		tardisXP += Math.abs(a);
		if (tardisXP >= getXPNeeded())
		{
			while (tardisXP >= getXPNeeded())
			{
				tardisXP -= getXPNeeded();
				tardisLevel++;
			}
			SoundHelper.playSound(dimID, Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, "tardismod:levelup", 1);
		}
		CoreTileEntity core = getCore();
		if (core != null) core.sendUpdate();
		markDirty();
		return tardisXP;
	}

	public double getXPNeeded()
	{
		if (tardisLevel <= 0) return 1;
		return TardisMod.xpBase + (tardisLevel * TardisMod.xpInc);
	}

	public int getLevel()
	{
		return tardisLevel;
	}

	public int getLevel(TardisUpgradeMode mode)
	{
		int level = 0;
		if (upgradeLevels.containsKey(mode)) level = upgradeLevels.get(mode);
		for(AbstractUpgrade up : upgrades)
		{
			if(up == null) continue;
			level += up.getUpgradeEffect(mode, this);
		}
		return level;
	}

	public void upgradeLevel(TardisUpgradeMode mode, int am)
	{
		if (unspentLevelPoints() >= am)
		{
			upgradeLevels.put(mode, am + getLevel(mode));
			markDirty();
		}
	}

	public int spentLevelPoints()
	{
		int spentPoints = 0;
		for (TardisUpgradeMode mode : upgradeLevels.keySet())
			spentPoints += upgradeLevels.get(mode);
		return spentPoints;
	}

	public int unspentLevelPoints()
	{
		return maxUnspentLevelPoints() - spentLevelPoints();
	}

	public int maxUnspentLevelPoints()
	{
		return tardisLevel / 2;
	}

	public int getMaxRF()
	{
		return TardisMod.rfBase + Math.max((tardisLevel - 1) * TardisMod.rfInc, 0);
	}

	public int getRF()
	{
		return rfStored;
	}

	public int addRF(int am, boolean sim)
	{
		int max = getMaxRF() - getRF();
		if (!sim)
		{
			rfStored += Math.min(am, max);
			markDirty();
		}
		return Math.min(am, max);
	}

	public int remRF(int am, boolean sim)
	{
		int max = getRF();
		if (!sim)
		{
			rfStored -= Math.min(am, max);
			markDirty();
		}
		return Math.min(am, max);
	}

	public ItemStack setIS(ItemStack is, int slot)
	{
		ItemStack ret = items[slot];
		if ((is != null) && (is.stackSize > 0))
			items[slot] = is;
		else
			items[slot] = null;
		markDirty();
		return ret;
	}

	public ItemStack getIS(int slot)
	{
		return items[slot];
	}

	public int getInvSize()
	{
		return items.length;
	}

	public FluidStack[] getTanks()
	{
		return fluids;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		exteriorWorld = nbt.getInteger("extW");
		exteriorX = nbt.getInteger("extX");
		exteriorY = nbt.getInteger("extY");
		exteriorZ = nbt.getInteger("extZ");
		pExtW = nbt.getInteger("pExtW");
		pExtX = nbt.getInteger("pExtX");
		pExtY = nbt.getInteger("pExtY");
		pExtZ = nbt.getInteger("pExtZ");
		rfStored = nbt.getInteger("rS");
		if (nbt.hasKey("invStore"))
		{
			NBTTagCompound invTag = nbt.getCompoundTag("invStore");
			for (int i = 0; i < items.length; i++)
			{
				if (invTag.hasKey("i" + i)) items[i] = ItemStack.loadItemStackFromNBT(invTag.getCompoundTag("i" + i));
			}
		}
		if (nbt.hasKey("fS"))
		{
			NBTTagCompound invTag = nbt.getCompoundTag("fS");
			for (int i = 0; i < fluids.length; i++)
			{
				if (invTag.hasKey("i" + i)) fluids[i] = FluidStack.loadFluidStackFromNBT(invTag.getCompoundTag("i" + i));
			}
		}
		readTransmittable(nbt);
	}

	public void readTransmittable(NBTTagCompound nbt)
	{
		if (TardisMod.tcInstalled) aspectList.readFromNBT(nbt, "aspectList");
		tardisLevel = nbt.getInteger("tL");
		tardisXP = nbt.getDouble("txp");
		{
			int i = 0;
			while (nbt.hasKey("permO" + i))
			{
				int h = nbt.getInteger("permO" + i);
				int d = nbt.getInteger("permD" + i);
				if (d != 0) permissionList.put(h, d);
				i++;
			}
		}
		for (TardisUpgradeMode mode : TardisUpgradeMode.values())
			if (nbt.hasKey("uG" + mode.ordinal()))
			{
				int am = nbt.getInteger("uG" + mode.ordinal());
				if (am > 0) upgradeLevels.put(mode, am);
			}

		NBTTagCompound damageNBT = nbt.getCompoundTag("damage");
		if (damageNBT != null) damage.readFromNBT(damageNBT);

		if(dimID == 8)
			System.out.println("");
		EngineTileEntity eng = getEngine();
		SimpleCoordStore engPos = null;
		if(eng != null)
			engPos = eng.coords;
		for (int i = 0; i < upgrades.length; i++)
		{
			if(nbt.hasKey("upgrade"+i))
			{
				if(upgrades[i] != null)
					upgrades[i].readFromNBT(nbt.getCompoundTag("upgrade"+i));
				else
					upgrades[i] = UpgradeFactory.createUpgrade(nbt.getCompoundTag("upgrade"+i));
			}
			else if(upgrades[i] != null)
			{
				upgrades[i] = null;
			}
			if((upgrades[i] != null) && (engPos != null))
				upgrades[i].setEnginePos(engPos);
		}
	}

	private void storeFlu(NBTTagCompound nbt)
	{
		int j = 0;
		NBTTagCompound invTag = new NBTTagCompound();
		for (int i = 0; i < fluids.length; i++)
		{
			if (fluids[i] != null)
			{
				NBTTagCompound iTag = new NBTTagCompound();
				invTag.setTag("i" + j, fluids[i].writeToNBT(iTag));
				j++;
			}
		}
		if (j > 0) nbt.setTag("fS", invTag);
	}

	private void storeInv(NBTTagCompound nbt)
	{
		int j = 0;
		NBTTagCompound invTag = new NBTTagCompound();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null)
			{
				NBTTagCompound iTag = new NBTTagCompound();
				invTag.setTag("i" + j, items[i].writeToNBT(iTag));
				j++;
			}
		}
		if (j > 0) nbt.setTag("invStore", invTag);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("extW", exteriorWorld);
		nbt.setInteger("extX", exteriorX);
		nbt.setInteger("extY", exteriorY);
		nbt.setInteger("extZ", exteriorZ);
		nbt.setInteger("pExtW", pExtW);
		nbt.setInteger("pExtX", pExtX);
		nbt.setInteger("pExtY", pExtY);
		nbt.setInteger("pExtZ", pExtZ);
		nbt.setInteger("rS", rfStored);
		storeInv(nbt);
		storeFlu(nbt);
		writeTransmittable(nbt);
	}

	public void writeTransmittable(NBTTagCompound nbt)
	{
		if (TardisMod.tcInstalled) aspectList.writeToNBT(nbt, "aspectList");
		nbt.setInteger("tL", tardisLevel);
		nbt.setDouble("txp", tardisXP);
		{
			int i = 0;
			for (Integer hash : permissionList.keySet())
			{
				nbt.setInteger("permO" + i, hash);
				nbt.setInteger("permD" + i, permissionList.get(hash));
				i++;
			}
		}

		if (upgradeLevels.size() > 0) for (TardisUpgradeMode mode : upgradeLevels.keySet())
		{
			int am = upgradeLevels.get(mode);
			if (am > 0) nbt.setInteger("uG" + mode.ordinal(), am);
		}
		NBTTagCompound damageNBT = new NBTTagCompound();
		damage.writeToNBT(damageNBT);
		nbt.setTag("damage", damageNBT);
		for(int i = 0; i < upgrades.length; i++)
		{
			if(upgrades[i] != null)
			{
				NBTTagCompound upgradeNBT = new NBTTagCompound();
				AbstractUpgrade upgrade = upgrades[i];
				upgrade.writeToNBT(upgradeNBT);
				nbt.setTag("upgrade"+i, upgradeNBT);
			}
		}
		if(dimID == 8)
			System.out.println("");
	}

	private ConsoleTileEntity getConsole()
	{
		return Helper.getTardisConsole(dimID);
	}

	private EngineTileEntity getEngine()
	{
		return Helper.getTardisEngine(dimID);
	}

	private CoreTileEntity getCore()
	{
		return Helper.getTardisCore(dimID);
	}

	public AspectList getAspectList()
	{
		return aspectList;
	}

	public void setAspectList(AspectList al)
	{
		aspectList = al;
		markDirty();
	}

	public boolean canHaveAspect(Aspect a, int am)
	{
		int cAm = aspectList.getAmount(a);
		if (cAm >= getMaxAspectStorage()) return false;
		return (aspectList.size() < TardisMod.numAspects) || ((cAm != 0) && ((cAm + am) <= getMaxAspectStorage()));
	}

	public int addAspect(Aspect a, int am)
	{
		int cAm = aspectList.getAmount(a);
		if ((cAm == 0) && (aspectList.size() >= TardisMod.numAspects)) return am;
		int toAdd = am;
		int toRet = 0;
		if ((am + cAm) > getMaxAspectStorage())
		{
			toAdd = Math.max(0, getMaxAspectStorage() - cAm);
			toRet = am - toAdd;
		}
		aspectList.add(a, toAdd);
		markDirty();
		return toRet;
	}

	public boolean removeAspect(Aspect a, int am)
	{
		int cAm = aspectList.getAmount(a);
		if (cAm < am) return false;
		aspectList.remove(a, am);
		markDirty();
		return true;
	}

	public int getMaxAspectStorage()
	{
		return ((getLevel() - 1) * TardisMod.maxEachAspectInc) + TardisMod.maxEachAspect;
	}

	public boolean hasFunction(TardisFunction fun)
	{
		switch (fun)
		{
			case LOCATE:
				return getLevel() >= 3;
			case SENSORS:
				return getLevel() >= 5;
			case STABILISE:
				return getLevel() >= 7;
			case TRANSMAT:
				return getLevel() >= 9;
			case RECALL:
				return getLevel() >= 11;
			case TRANQUILITY:
				return getLevel() >= 15;
			case CLARITY:
				return getLevel() >= 13;
			default:
				return false;
		}
	}

	public boolean hasPermission(Object ent, TardisPermission perm)
	{
		if (ent instanceof EntityPlayer) return hasPermission((EntityPlayer) ent, perm);
		return false;
	}

	public boolean hasPermission(EntityPlayer pl, TardisPermission perm)
	{
		if (pl.capabilities.isCreativeMode) return true;
		CoreTileEntity core = getCore();
		if ((core != null) && core.isOwner(pl)) return true;
		return hasPermission(ServerHelper.getUsername(pl), perm);
	}

	public boolean hasPermission(String pl, TardisPermission perm)
	{
		if ((pl == null) || (perm == null)) return false;
		CoreTileEntity core = getCore();
		if ((core != null) && core.isOwner(pl)) return true;
		int hash = pl.hashCode();
		synchronized (permissionList)
		{
			if (permissionList.containsKey(hash))
			{
				int data = permissionList.get(hash);
				return perm.isIn(data);
			}
		}
		return false;
	}

	public boolean togglePermission(EntityPlayer giver, EntityPlayer givee, TardisPermission perm)
	{
		return togglePermission(ServerHelper.getUsername(giver), ServerHelper.getUsername(givee), perm);
	}

	public boolean togglePermission(String giver, String givee, TardisPermission perm)
	{
		if (hasPermission(giver, TardisPermission.PERMISSIONS))
		{
			int hash = givee.hashCode();
			synchronized (permissionList)
			{
				int data = permissionList.containsKey(hash) ? permissionList.get(hash) : 0;
				data = perm.toggle(data);
				permissionList.put(hash, data);
			}
			return true;
		}
		return false;
	}

	public void tick()
	{
		damage.tick();
		if(!ServerHelper.isIntegratedClient())
			tt = (tt + 1) % 827026200;
		for(AbstractUpgrade up : upgrades)
		{
			if((up != null) && !ServerHelper.isIntegratedClient())
				up.tick(tt);
		}
	}

}
