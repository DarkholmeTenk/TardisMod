package tardis.common.tileents.extensions.upgrades;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.SoundHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tardis.TardisMod;
import tardis.common.dimension.damage.TardisDamageType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractDamReduce extends AbstractUpgrade
{
	protected int	health	= 1000;
	protected boolean takeDamage(int dam)
	{
		if(health == 0) return false;
		health = health - dam;
		if(health <= 0)
		{
			health = 0;
			if(enginePos != null)
				SoundHelper.playSound(enginePos, "tardismod:crack", 2.5f);
		}
		return true;
	}

	@Override
	public int takeDamage(TardisDamageType dam, int amount)
	{
		if(dam == getDamageType())
		{
			int blockAmount = MathHelper.ceil(amount / 2.0);
			int acceptAmount = MathHelper.floor(amount / 2.0);
			if(takeDamage(blockAmount))
				return acceptAmount;
		}
		return amount;
	}

	public abstract TardisDamageType getDamageType();

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture()
	{
		if(health > 0)
			return getWorkingTexture();
		return AbstractUpgrade.brokenTexture;
	}

	@Override
	public ItemStack getIS()
	{
		if(health <= 0)
		{
			ItemStack is = new ItemStack(TardisMod.upgradeItem,1,0);
			return is;
		}
		else
			return getWorkingIS();
	}

	public abstract ItemStack getWorkingIS();

	@Override
	public boolean isValid(AbstractUpgrade[] currentUpgrades)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getWorkingTexture();

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("health", health);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		health = nbt.getInteger("health");
	}

	@Override
	public void tick(int tt)
	{
		if((tt % 20) == 0)
			if(health > 0)
				health = MathHelper.clamp(health+1, 0, 1000);
	}

	public abstract String getName();

	private int lastHealth;
	private String[] extraInfo;
	@Override
	public String[] getExtraInfo()
	{
		if((extraInfo == null) || (lastHealth != health))
		{
			extraInfo = new String[]{getName(),"Health: " + health + "/1000"};
			lastHealth = health;
		}
		return extraInfo;
	}
}
