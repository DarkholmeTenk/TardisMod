package tardis.common.tileents.extensions.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import tardis.common.TMRegistry;
import tardis.common.dimension.damage.TardisDamageType;

public class DamReduceExplosion extends AbstractDamReduce
{
	public static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/upgrades/protExpl.png");

	public DamReduceExplosion(){}

	public DamReduceExplosion(NBTTagCompound nbt)
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
		ItemStack is = new ItemStack(TMRegistry.upgradeItem,1,1);
		is.stackTagCompound = new NBTTagCompound();
		writeToNBT(is.stackTagCompound);
		return is;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("id", "protExpl");
	}

	@Override
	public TardisDamageType getDamageType()
	{
		return TardisDamageType.EXPLOSION;
	}

	@Override
	public String getName()
	{
		return "Damage Protection Upgrade - Explosion";
	}

}
