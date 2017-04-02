package tardis.common.tileents.extensions.upgrades.factory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;

public interface IUpgradeFactory
{
	public AbstractUpgrade create(NBTTagCompound nbt);

	public AbstractUpgrade create(ItemStack is);
}
