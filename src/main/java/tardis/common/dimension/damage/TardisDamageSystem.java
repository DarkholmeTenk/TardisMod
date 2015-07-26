package tardis.common.dimension.damage;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.dimension.TardisDataStore;

/**
 * A subsystem of the TARDIS Data Store which handles all of the damage
 *
 * @author dark
 */
public class TardisDamageSystem
{
	// THESE ARE CLASS WIDE
	private static ConfigFile	config				= null;
	private static int			maxShields			= 1000;
	private static int			maxShieldsInc		= 500;
	private static int			maxHull;

	public static double		explosionDamageMult	= 50;

	public static void refreshConfigs()
	{
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("DamageSystem");
		maxHull = config.getInt("Max hull", 1000, "The maximum hull strength");
		maxShields = config.getInt("Max shields", 1000, "The base maximum amount of shielding");
		maxShieldsInc = config.getInt("Max shields increase", 500, "How much a level of max shields increases the amount of shielding");
	}

	// *******************
	// ACTUAL OBJECT STUFF
	// *******************
	private final TardisDataStore	ds;
	private final int				dimID;

	private int						tt	= 0;
	private int						shields;
	private int						hull;

	public TardisDamageSystem(TardisDataStore parent)
	{
		ds = parent;
		dimID = ds.dimID;
		shields = getMaxShields();
		hull = getMaxHull();
	}

	/**
	 * Damages the shields
	 * @param amount the amount of damage to apply
	 * @return the amount of damage left over after damaging the shields
	 */
	private int damageShields(int amount)
	{
		synchronized(this)
		{
			if(shields >= amount)
			{
				shields -= amount;
				return 0;
			}
			int toRet = amount - shields;
			shields = 0;
			return toRet;
		}
	}

	public void damage(TardisDamageType damageType, int amount)
	{
		System.out.println("[TDS] Damage amount: " + amount);
		int hullDamage = damageShields(amount);
		if(ServerHelper.isServer())
			System.out.println("Newshields:" + shields);
		if(hullDamage == 0) return;
	}

	public int getShields()
	{
		shields = MathHelper.clamp(shields, 0, getMaxShields());
		return shields;
	}

	public int getMaxShields()
	{
		return maxShields + (ds.getLevel(TardisUpgradeMode.SHIELDS) * maxShieldsInc);
	}

	public int getHull()
	{
		hull = MathHelper.clamp(hull, 0, getMaxHull());
		return hull;
	}

	public int getMaxHull()
	{
		return maxHull;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("hull", hull);
		nbt.setInteger("shields", shields);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("shields"))
		{
			hull = nbt.getInteger("hull");
			shields = nbt.getInteger("shields");
		}
	}

	private void regenShields()
	{
		synchronized(this)
		{
			double regen = getShieldRegenRate();
			if(regen >= 1)
			{
				shields = MathHelper.clamp(shields + MathHelper.round(regen), 0, getMaxShields());
			}
			else if(regen != 0)
			{
				int rate = MathHelper.round(1/regen);
				if((tt  % rate) == 0)
				{
					shields = MathHelper.clamp(shields + 1, 0, getMaxShields());
					if(shields < getMaxShields())
					System.out.println("Regshields:" + shields);
				}
			}
		}
	}

	public synchronized void tick()
	{
		if(ServerHelper.isIntegratedClient()) return;
		tt = (tt + 1) % 1000000;
		regenShields();
	}

	private double getShieldRegenRate()
	{
		return 0.1;
	}
}
