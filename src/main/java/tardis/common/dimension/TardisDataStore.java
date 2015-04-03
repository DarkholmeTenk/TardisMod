package tardis.common.dimension;

import java.util.HashMap;

import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.core.Helper;
import tardis.common.tileents.CoreTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

public class TardisDataStore extends AbstractWorldDataStore
{
	private final int										dimID;

	public int												exteriorWorld;
	public int												exteriorX;
	public int												exteriorY;
	public int												exteriorZ;

	public volatile double									tardisXP		= 0;
	public volatile int										tardisLevel		= 0;
	private volatile HashMap<TardisUpgradeMode, Integer>	upgradeLevels	= new HashMap<TardisUpgradeMode, Integer>();

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

	@Override
	public int getDimension()
	{
		return dimID;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		exteriorWorld = nbt.getInteger("extW");
		exteriorX = nbt.getInteger("extX");
		exteriorY = nbt.getInteger("extY");
		exteriorZ = nbt.getInteger("extZ");
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

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		System.out.println("DSSAVE!");
		nbt.setInteger("extW", exteriorWorld);
		nbt.setInteger("extX", exteriorX);
		nbt.setInteger("extY", exteriorY);
		nbt.setInteger("extZ", exteriorZ);
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

	public double getXP()
	{
		return tardisXP;
	}

	public double addXP(double a)
	{
		World w = WorldHelper.getWorld(dimID);
		tardisXP += Math.abs(a);
		if (tardisXP >= getXPNeeded())
		{
			while (tardisXP >= getXPNeeded())
			{
				tardisXP -= getXPNeeded();
				tardisLevel++;
			}
			SoundHelper.playSound(w, Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, "levelup", 1);
		}
		CoreTileEntity core = Helper.getTardisCore(w);
		if (core != null)
			core.sendUpdate();
		this.markDirty();
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
			upgradeLevels.put(mode, am + getLevel(mode));
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

}
