package tardis.common.dimension.damage;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.datastore.Pair;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
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
	public static int			maxShields			= 1000;
	private static int			maxShieldsInc		= 500;
	public static int			maxHull;

	public static double		explosionDamageMult	= 100;
	public static double		missedDamageMult	= 30;
	public static double		combatDamageMult	= 30;
	public static int			hullToFly			= 30;
	public static int			hullToControl		= 50;
	private static Random		rand				= new Random();

	public static void refreshConfigs()
	{
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("damageSystem");
		maxHull = config.getInt("Max hull", 1000, "The maximum hull strength");
		maxShields = config.getInt("Max shields", 1000, "The base maximum amount of shielding");
		maxShieldsInc = config.getInt("Max shields increase", 500, "How much a level of max shields increases the amount of shielding");

		explosionDamageMult = config.getDouble("explosionDamageMult", 100, "Explosion damage is multiplied by this number before being applied to a TARDIS");
		missedDamageMult = config.getDouble("missedDamageMult", 30, "Damage from missing a blue control is multiplied by this number before being applied to a TARDIS");

		hullToFly = config.getInt("Hull To Fly", 300, "Minimum hull needed for the TARDIS to be in flight");
		hullToControl = config.getInt("Hull to Control", 500, "Hull needed for console controls to respond");
	}

	// *******************
	// ACTUAL OBJECT STUFF
	// *******************
	private final TardisDataStore			ds;
	private final int						dimID;

	private int								tt				= 0;
	private int								shields;
	private int								hull;
	private int								prevHull;
	public static final int					numBreakables	= 10;
	private boolean[]						breakables		= new boolean[numBreakables];
	private static Pair<Item, Integer>[]	repairComps;
	public static String[]					repairCompNames;

	private void fillInRepair()
	{
		repairComps = new Pair[numBreakables];
		repairCompNames = new String[numBreakables];

		Pair<Item, Integer> kontronPair = new Pair(TardisMod.craftingComponentItem, CraftingComponentType.KONTRON.ordinal());
		Pair<Item, Integer> dalek = new Pair(TardisMod.craftingComponentItem, CraftingComponentType.DALEKANIUM.ordinal());
		Pair<Item, Integer> chrono = new Pair(TardisMod.craftingComponentItem, CraftingComponentType.CHRONOSTEEL.ordinal());
		repairComps[0] = kontronPair;
		repairComps[1] = dalek;
		repairComps[2] = dalek;
		repairComps[4] = dalek;
		repairComps[5] = dalek;
		for(int i = 1; i< (numBreakables - 2); i++)
			if(repairComps[i] == null)
				repairComps[i] = chrono;
		for(int i = numBreakables-2; i < numBreakables; i++)
		{
			repairComps[i] = null;
			repairCompNames[i] = "nothing";
		}
		String kontronStr = "Kontron";
		String dalekStr = "Dalekanium";
		String chronoStr = "Chronosteel";
		for(int i = 0; i < numBreakables; i++)
		{
			if(repairComps[i] == kontronPair) repairCompNames[i] = kontronStr;
			else if(repairComps[i] == dalek) repairCompNames[i] = dalekStr;
			else if(repairComps[i] == chrono) repairCompNames[i] = chronoStr;
		}
	}

	public TardisDamageSystem(TardisDataStore parent)
	{
		//if(repairComps == null)
			fillInRepair();
		ds = parent;
		dimID = ds.getDimension();
		shields = getMaxShields();
		hull = getMaxHull();
		prevHull = hull;
		for (int i = 0; i < breakables.length; i++)
			breakables[i] = false;
	}

	private int handleDamageReductions(TardisDamageType damageType, int damage)
	{
		int damSoFar = damage;
		for (int i = 0; i < ds.upgrades.length; i++)
		{
			AbstractUpgrade up = ds.upgrades[i];
			if (up != null) damSoFar = up.takeDamage(damageType, damSoFar);
		}
		return damSoFar;
	}

	/**
	 * Damages the shields
	 *
	 * @param amount
	 *            the amount of damage to apply
	 * @return the amount of damage left over after damaging the shields
	 */
	private int damageShields(int amount)
	{
		synchronized (this)
		{
			if (shields >= amount)
			{
				shields -= amount;
				return 0;
			}
			int toRet = amount - shields;
			shields = 0;
			return toRet;
		}
	}

	private int getHullClass(int hull)
	{
		return hull / (getMaxHull() / numBreakables);
	}

	private void damageHull(int amount)
	{
		hull = MathHelper.clamp(hull - Math.abs(amount), 0, getMaxHull());
	}

	public void damage(TardisDamageType damageType, double damageAmount)
	{
		switch (damageType)
		{
			case EXPLOSION:
				damageAmount *= explosionDamageMult;
				break;
			case MISSEDCONTROL:
				damageAmount *= missedDamageMult;
				break;
			case COMBAT:
				damageAmount *= combatDamageMult;
				break;
			default:
				damageAmount = 0;
		}
		int amount = MathHelper.round(damageAmount);
		if (amount == 0) return;
		ds.markDirty();
		int damage = handleDamageReductions(damageType, amount);
		// System.out.println("[TDS] Damage amount: " + amount + ">" + damage);
		int hullDamage = damageShields(damage);
		// if (ServerHelper.isServer()) System.out.println("Newshields:" + shields);
		if (hullDamage == 0) return;
		damageHull(hullDamage);
	}

	private void playBreakSound()
	{
		SoundHelper.playSound(dimID, "tardismod:crack", 3, 1);
	}

	private void breakComponent()
	{
		int count = 0;
		while (count++ < 5)
		{
			int slot = rand.nextInt(numBreakables - 1) + 1;
			if (!breakables[slot])
			{
				breakables[slot] = true;
				playBreakSound();
				return;
			}
		}
		for (int i = 1; i <= numBreakables; i++)
			if (!breakables[i%numBreakables])
			{
				breakables[i%numBreakables] = true;
				playBreakSound();
				return;
			}
	}

	private boolean match(ItemStack is, int slot)
	{
		Pair<Item, Integer> pair = repairComps[slot];
		if(pair == null) return true;
		if(is == null) return false;
		if((is.getItem() == pair.a) && (is.getItemDamage() == pair.b)) return true;
		return false;
	}

	private boolean isHoldingRepairComponent(EntityPlayer pl, int component)
	{
		if ((component < 0) || (component >= numBreakables)) return false;
		if ((component != 0) && isComponentBroken(0))
		{
			ServerHelper.sendString(pl, "Engine", "The central damage unit must be repaired first");
			return false;
		}
		ItemStack is = pl.getHeldItem();
		if(match(is, component))
		{
			if((repairComps[component] != null) && !pl.capabilities.isCreativeMode) is.stackSize--;
			return true;
		}
		else
		{
			String m = repairCompNames[component];
			ServerHelper.sendString(pl, "Engine", "Repairing this damage unit will take " + m);
		}
		return false;
	}

	public boolean repairComponent(EntityPlayer pl, int component)
	{
		if ((component < 0) || (component >= numBreakables)) return false;
		if (!breakables[component]) return false;
		if (!isHoldingRepairComponent(pl, component)) return false;
		breakables[component] = false;
		hull = hull + (getMaxHull() / numBreakables);
		hull = MathHelper.clamp(hull, 0, getMaxHull());
		ds.sendUpdate();
		return true;
	}

	public boolean isComponentBroken(int component)
	{
		if ((component < 0) || (component >= numBreakables)) return false;
		return breakables[component];
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
		nbt.setInteger("prevHull", prevHull);
		nbt.setInteger("shields", shields);
		for (int i = 0; i < numBreakables; i++)
			nbt.setBoolean("b" + i, breakables[i]);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("shields"))
		{
			hull = nbt.getInteger("hull");
			prevHull = nbt.getInteger("prevHull");
			shields = nbt.getInteger("shields");
			for (int i = 0; i < numBreakables; i++)
				if (nbt.hasKey("b" + i)) breakables[i] = nbt.getBoolean("b" + i);
		}
	}

	private void regenShields()
	{
		synchronized (this)
		{
			double regen = getShieldRegenRate();
			if (regen >= 1)
			{
				shields = MathHelper.clamp(shields + MathHelper.round(regen), 0, getMaxShields());
				if ((tt % (20 * 60)) == 0) ds.markDirty();
			}
			else if (regen != 0)
			{
				int rate = MathHelper.round(1 / regen);
				if ((tt % rate) == 0)
				{
					shields = MathHelper.clamp(shields + 1, 0, getMaxShields());
					ds.markDirty();
				}
			}
		}
	}

	public synchronized void tick()
	{
		if (prevHull != hull)
		{
			int prevHullClass = getHullClass(prevHull);
			int currentClass = getHullClass(hull);
			while (currentClass < prevHullClass--)
				breakComponent();
			prevHull = hull;
			ds.sendUpdate();
		}
		if (ServerHelper.isIntegratedClient()) return;
		if (hull < hullToFly)
		{
			CoreTileEntity core = ds.getCore();
			if ((core != null) && core.inAbortableFlight())
			{
				core.attemptToLand();
				core.sendUpdate();
			}
		}
		tt = (tt + 1) % 1000000;
		regenShields();
	}

	private double getShieldRegenRate()
	{
		return 0.1;
	}

	public void setShields(int v)
	{
		shields = MathHelper.clamp(v, 0, getMaxShields());
	}

	public void setHull(int v)
	{
		hull = MathHelper.clamp(v, 0, getMaxHull());
	}
}
