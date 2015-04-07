package tardis.common.dimension;

import java.util.HashMap;

import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

public class TardisDataStore extends AbstractWorldDataStore
{
	private final int										dimID;

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

	public TardisDataStore(String n)
	{
		super(n);
		dimID = -1;
	}

	public TardisDataStore(int _dimID)
	{
		super("tardisIDS");
		dimID = _dimID;
	}
	
	public void markMaybeDirty()
	{
		if(pExtX!=exteriorX || pExtY != exteriorY || pExtZ!= exteriorZ || pExtW != exteriorWorld)
			markDirty();
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
		setExterior(exterior.getWorldObj(),exterior.xCoord,exterior.yCoord,exterior.zCoord);
	}
	
	public TardisTileEntity getExterior()
	{
		World w = WorldHelper.getWorld(exteriorWorld);
		if (w != null)
		{
			TileEntity te = w.getTileEntity(exteriorX, exteriorY, exteriorZ);
			if (te instanceof TardisTileEntity)
				return (TardisTileEntity) te;
		}
		return null;
	}
	
	public boolean hasValidExterior()
	{
		World w = WorldHelper.getWorld(exteriorWorld);
		if (w != null)
		{
			if (w.getBlock(exteriorX, exteriorY, exteriorZ) == TardisMod.tardisBlock)
				return true;
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
			SoundHelper.playSound(dimID, Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, "levelup", 1);
		}
		CoreTileEntity core = getCore();
		if (core != null)
			core.sendUpdate();
		markDirty();
		return tardisXP;
	}

	public double getXPNeeded()
	{
		if (tardisLevel <= 0)
			return 1;
		return TardisMod.xpBase + (tardisLevel * TardisMod.xpInc);
	}

	public int getLevel()
	{
		return tardisLevel;
	}

	public int getLevel(TardisUpgradeMode mode)
	{
		int level = 0;
		if (upgradeLevels.containsKey(mode))
			level = upgradeLevels.get(mode);
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
		items[slot] = is;
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
		rfStored = nbt.getInteger("rS");
		if (nbt.hasKey("invStore"))
		{
			NBTTagCompound invTag = nbt.getCompoundTag("invStore");
			for (int i = 0; i < items.length; i++)
			{
				if (invTag.hasKey("i" + i))
					items[i] = ItemStack.loadItemStackFromNBT(invTag.getCompoundTag("i" + i));
			}
		}
		if (nbt.hasKey("fS"))
		{
			NBTTagCompound invTag = nbt.getCompoundTag("fS");
			for (int i = 0; i < fluids.length; i++)
			{
				if (invTag.hasKey("i" + i))
					fluids[i] = FluidStack.loadFluidStackFromNBT(invTag.getCompoundTag("i" + i));
			}
		}
		readTransmittable(nbt);
	}

	public void readTransmittable(NBTTagCompound nbt)
	{
		tardisLevel = nbt.getInteger("tL");
		tardisXP = nbt.getDouble("txp");
		for (TardisUpgradeMode mode : TardisUpgradeMode.values())
			if (nbt.hasKey("uG" + mode.ordinal()))
			{
				int am = nbt.getInteger("uG" + mode.ordinal());
				if (am > 0)
					upgradeLevels.put(mode, am);
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
		if (j > 0)
			nbt.setTag("fS", invTag);
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
		if (j > 0)
			nbt.setTag("invStore", invTag);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		System.out.println("DSSAVE!");
		nbt.setInteger("extW", exteriorWorld);
		nbt.setInteger("extX", exteriorX);
		nbt.setInteger("extY", exteriorY);
		nbt.setInteger("extZ", exteriorZ);
		nbt.setInteger("rS", rfStored);
		storeInv(nbt);
		storeFlu(nbt);

		writeTransmittable(nbt);
	}

	public void writeTransmittable(NBTTagCompound nbt)
	{
		nbt.setInteger("tL", tardisLevel);
		nbt.setDouble("txp", tardisXP);
		if (upgradeLevels.size() > 0)
			for (TardisUpgradeMode mode : upgradeLevels.keySet())
			{
				int am = upgradeLevels.get(mode);
				if (am > 0)
					nbt.setInteger("uG" + mode.ordinal(), am);
			}
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

}
