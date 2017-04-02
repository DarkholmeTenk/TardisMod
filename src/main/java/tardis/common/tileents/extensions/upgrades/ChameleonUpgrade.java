package tardis.common.tileents.extensions.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import tardis.TardisMod;
import tardis.common.TMRegistry;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;

public class ChameleonUpgrade extends AbstractUpgrade
{
	public final AbstractTardisChameleon chameleon;

	public ChameleonUpgrade(AbstractTardisChameleon c)
	{
		chameleon = c;
	}

	private static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/upgrades/cham.png");
	@Override
	public ResourceLocation getTexture()
	{
		return tex;
	}

	@Override
	public boolean isValid(AbstractUpgrade[] currentUpgrades)
	{
		for(AbstractUpgrade up : currentUpgrades)
			if(up instanceof ChameleonUpgrade)
				return false;
		return true;
	}

	@Override
	public ItemStack getIS()
	{
		return new ItemStack(TMRegistry.chameleonUpgradeItem, 1, TardisMod.tardisChameleonReg.getIndex(chameleon));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("id", "cham");
		chameleon.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	}

	private String[] info;
	@Override
	public String[] getExtraInfo()
	{
		if(info == null)
			info = new String[]{"Mode: " + StatCollector.translateToLocal(chameleon.getName())};
		return info;
	}

}
