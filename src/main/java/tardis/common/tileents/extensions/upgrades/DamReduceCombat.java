package tardis.common.tileents.extensions.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;
import tardis.common.dimension.damage.TardisDamageType;

public class DamReduceCombat extends AbstractDamReduce
{
	public static final ResourceLocation tex = new ResourceLocation("tardismod","textures/models/upgrades/protComb.png");

	public DamReduceCombat(){}

	public DamReduceCombat(NBTTagCompound nbt)
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
		ItemStack is = new ItemStack(TardisMod.upgradeItem,1,2);
		is.stackTagCompound = new NBTTagCompound();
		writeToNBT(is.stackTagCompound);
		return is;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("id", "protComb");
	}

	@Override
	public TardisDamageType getDamageType()
	{
		return TardisDamageType.COMBAT;
	}

	@Override
	public boolean isValid(AbstractUpgrade[] currentUpgrades)
	{
		int c = 0;
		for(AbstractUpgrade up : currentUpgrades)
		{
			if(up == null) continue;
			if(up instanceof DamReduceCombat) c++;
			if(c >= 2) return false;
		}
		return true;
	}

	@Override
	public String getName()
	{
		return "Damage Protection Upgrade - Combat";
	}

}
