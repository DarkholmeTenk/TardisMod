package tardis.common.tileents.extensions.upgrades;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import tardis.api.TardisUpgradeMode;
import tardis.common.TMRegistry;
import tardis.common.dimension.TardisDataStore;

public class LevelUpgrade extends AbstractUpgrade
{
	private static HashMap<TardisUpgradeMode,ResourceLocation> resMap = new HashMap();
	private final TardisUpgradeMode tum;
	public LevelUpgrade(TardisUpgradeMode mode)
	{
		tum = mode;
	}

	@Override
	public ResourceLocation getTexture()
	{
		if(resMap.containsKey(tum))
			return resMap.get(tum);
		resMap.put(tum, new ResourceLocation("tardismod","textures/models/upgrades/up"+tum.toString()+".png"));
		return resMap.get(tum);
	}

	@Override
	public boolean isValid(AbstractUpgrade[] currentUpgrades)
	{
		return true;
	}

	@Override
	public ItemStack getIS()
	{
		int o = 0;
		switch(tum)
		{
			case SPEED: o = 4; break;
			case SHIELDS: o = 5; break;
			case ROOMS: o = 6; break;
			case ENERGY: o = 7; break;
			case REGEN: o = 8; break;
		}
		return new ItemStack(TMRegistry.upgradeItem,1,o);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("id", "levelUp");
		nbt.setInteger("tum", tum.ordinal());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getExtraInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUpgradeEffect(TardisUpgradeMode mode, TardisDataStore ds)
	{
		if(mode == tum)
			return 1;
		return 0;
	}

}
