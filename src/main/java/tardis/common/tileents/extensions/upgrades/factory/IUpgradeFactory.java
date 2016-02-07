package tardis.common.tileents.extensions.upgrades.factory;

import tardis.common.tileents.extensions.upgrades.AbstractUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IUpgradeFactory
{
	public AbstractUpgrade create(NBTTagCompound nbt);

	public AbstractUpgrade create(ItemStack is);
}
