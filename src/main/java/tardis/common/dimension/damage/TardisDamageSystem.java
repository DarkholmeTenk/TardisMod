package tardis.common.dimension.damage;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;

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

	private int handleDamageReductions(TardisDamageType damageType, int damage)
	{
		int damSoFar = damage;
		for(int i = 0; i < ds.upgrades.length; i++)
		{
			AbstractUpgrade up = ds.upgrades[i];
			if(up != null)
				damSoFar = up.takeDamage(damageType, damSoFar);
		}
		return damSoFar;
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
		if(amount == 0) return;
		ds.markDirty();
		int damage = handleDamageReductions(damageType, amount);
		System.out.println("[TDS] Damage amount: " + amount+">"+damage);
		int hullDamage = damageShields(damage);
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
				if((tt % (20 * 60)) == 0)
					ds.markDirty();
			}
			else if(regen != 0)
			{
				int rate = MathHelper.round(1/regen);
				if((tt  % rate) == 0)
				{
					shields = MathHelper.clamp(shields + 1, 0, getMaxShields());
					ds.markDirty();
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
