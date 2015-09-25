package tardis.common.tileents.extensions.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;
import tardis.common.dimension.damage.TardisDamageType;

public class DamReduceMissedControl extends AbstractDamReduce
{
	public static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/upgrades/protMiss.png");

	public DamReduceMissedControl(){}

	public DamReduceMissedControl(NBTTagCompound nbt)
	{
		readFromNBT(nbt);
	}

	@Override
	public ResourceLocation getWorkingTexture()
	{
		return tex;
	}

	@Override
	public ItemStack getWorkingIS()
	{
		ItemStack is = new ItemStack(TardisMod.upgradeItem,1,3);
		is.stackTagCompound = new NBTTagCompound();
		writeToNBT(is.stackTagCompound);
		return is;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("id", "protMiss");
	}

	@Override
	public TardisDamageType getDamageType()
	{
		return TardisDamageType.MISSEDCONTROL;
	}

	@Override
	public String getName()
	{
		return "Damage Protection Upgrade - Calibration";
	}

}
