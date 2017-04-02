package tardis.common.tileents.extensions.upgrades.factory;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tardis.TardisMod;
import tardis.api.TardisUpgradeMode;
import tardis.common.TMRegistry;
import tardis.common.items.DimensionUpgradeItem;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;
import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;
import tardis.common.tileents.extensions.upgrades.ChameleonUpgrade;
import tardis.common.tileents.extensions.upgrades.DamReduceCombat;
import tardis.common.tileents.extensions.upgrades.DamReduceExplosion;
import tardis.common.tileents.extensions.upgrades.DamReduceMissedControl;
import tardis.common.tileents.extensions.upgrades.DimensionUpgrade;
import tardis.common.tileents.extensions.upgrades.LevelUpgrade;

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
			if(id.equals("levelUp"))
				return new LevelUpgrade(TardisUpgradeMode.getUpgradeMode(nbt.getInteger("tum")));
			if(id.equals("cham"))
				return new ChameleonUpgrade(TardisMod.tardisChameleonReg.get(nbt, AbstractTardisChameleon.nbtKey));
			if(id.equals("dim"))
				return new DimensionUpgrade(nbt.getInteger("dimID"));
		}
		return null;
	}

	@Override
	public AbstractUpgrade create(ItemStack is)
	{
		if(is == null) return null;
		Item i = is.getItem();
		if(i == TMRegistry.upgradeItem)
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
					case 4: return new LevelUpgrade(TardisUpgradeMode.SPEED);
					case 5: return new LevelUpgrade(TardisUpgradeMode.SHIELDS);
					case 6: return new LevelUpgrade(TardisUpgradeMode.ROOMS);
					case 7: return new LevelUpgrade(TardisUpgradeMode.ENERGY);
					case 8: return new LevelUpgrade(TardisUpgradeMode.REGEN);
				}
			}
		}
		if(i == TMRegistry.chameleonUpgradeItem)
		{
			int meta = is.getItemDamage();
			if((meta < 0) || (meta >= TMRegistry.chameleonUpgradeItem.types.length)) return null;
			return new ChameleonUpgrade(TMRegistry.chameleonUpgradeItem.types[meta]);
		}

		if(i instanceof DimensionUpgradeItem){
			for(int k : TMRegistry.dimensionUpgradeItems.keySet()){
				if(TMRegistry.dimensionUpgradeItems.get(k) == i){
					return new DimensionUpgrade(k);
				}
			}
		}
		return null;
	}

}
