package tardis.common.tileents.extensions.upgrades.factory;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;
import tardis.common.tileents.extensions.upgrades.DamReduceCombat;
import tardis.common.tileents.extensions.upgrades.DamReduceExplosion;
import tardis.common.tileents.extensions.upgrades.DamReduceMissedControl;

public class UpgradeFactory implements IUpgradeFactory
{
	private static ArrayList<IUpgradeFactory> upgradeFactories = new ArrayList<IUpgradeFactory>();
	static
	{
		UpgradeFactory newFactory = new UpgradeFactory();
		registerUpgradeFactory(newFactory);
	}

	public static void registerUpgradeFactory(IUpgradeFactory factory)
	{
		upgradeFactories.add(factory);
	}

	public static AbstractUpgrade createUpgrade(NBTTagCompound nbt)
	{
		for(IUpgradeFactory factory : upgradeFactories)
		{
			AbstractUpgrade upgrade = factory.create(nbt);
			if(upgrade != null)
				return upgrade;
		}
		return null;
	}

	public static AbstractUpgrade createUpgrade(ItemStack is)
	{
		for(IUpgradeFactory factory : upgradeFactories)
		{
			AbstractUpgrade upgrade = factory.create(is);
			if(upgrade != null)
				return upgrade;
		}
		return null;
	}

	@Override
	public AbstractUpgrade create(NBTTagCompound nbt)
	{
		if(nbt == null) return null;
		if(nbt.hasKey("id"))
		{
			String id = nbt.getString("id");
			if(id.equals("protExpl"))
				return new DamReduceExplosion(nbt);
			if(id.equals("protComb"))
				return new DamReduceCombat(nbt);
			if(id.equals("protMiss"))
				return new DamReduceMissedControl(nbt);
		}
		return null;
	}

	@Override
	public AbstractUpgrade create(ItemStack is)
	{
		if(is == null) return null;
		Item i = is.getItem();
		if(i == TardisMod.upgradeItem)
		{
			if(is.stackTagCompound != null)
			{
				NBTTagCompound nbt = is.stackTagCompound;
				return create(nbt);
			}
			else
			{
				int meta = is.getItemDamage();
				switch(meta)
				{
					case 1: return new DamReduceExplosion();
					case 2: return new DamReduceCombat();
					case 3: return new DamReduceMissedControl();
				}
			}
		}
		return null;
	}

}
