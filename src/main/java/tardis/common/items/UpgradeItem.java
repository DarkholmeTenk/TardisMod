package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;

public class UpgradeItem extends AbstractItem
{
	private final static String[] upgradeTypes = new String[]{"broken", "protExpl","protComb","protMiss","speed","shield","room","energy","energyRegen"};
	public UpgradeItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("UpgradeItem");
		setSubNames(upgradeTypes);
		setCreativeTab(TardisMod.cTab);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if (is != null)
		{
			NBTTagCompound nbt = is.stackTagCompound;
			if(nbt != null)
			{
				if(nbt.hasKey("health"))
					infoList.add("Health:" + nbt.getInteger("health"));
			}
		}
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

}
