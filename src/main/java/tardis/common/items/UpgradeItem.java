package tardis.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;

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

	private ItemStack getIS(int meta)
	{
		return new ItemStack(this,1,meta);
	}

	@Override
	public void initRecipes()
	{
		ItemStack u = CraftingComponentType.UPGRADE.getIS(1);
		ItemStack c = CraftingComponentType.CHRONOSTEEL.getIS(1);
		ItemStack d = CraftingComponentType.DALEKANIUM.getIS(1);
		ItemStack r = new ItemStack(Items.redstone,1);

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(1), false, "kdk", "rur", "ydy",
				'k', "dyeRed",
				'd', d,
				'u', u,
				'r', r,
				'y', "dyeYellow")); //ProtExp

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(2), false, "kdk", "rur", "ydy",
				'k', "dyeGreen",
				'd', d,
				'u', u,
				'r', r,
				'y', "dyeBlack")); //ProtComb

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(3), false, "kdk", "rur", "ydy",
				'k', "dyeBlue",
				'd', d,
				'u', u,
				'r', r,
				'y', "dyeBlack")); //ProtMiss

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(4), false, "kck", "rur", "ycy",
				'k', "dyeBlue",
				'c', c,
				'u', u,
				'r', r,
				'y', "dyeCyan")); //Speed

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(5), false, "kck", "rur", "ycy",
				'k', "dyeBlue",
				'c', c,
				'u', u,
				'r', "ingotGold",
				'y', "dyeCyan")); //Shield

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(6), false, "kck", "rur", "ycy",
				'k', "dyeGreen",
				'c', c,
				'u', u,
				'r', "ingotGold",
				'y', "dyeYellow")); //Rooms

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(7), false, "kok", "rur", "ycy",
				'k', "dyePurple",
				'c', c,
				'u', u,
				'o', new ItemStack(Items.diamond,1),
				'r', "ingotGold",
				'y', "dyeBlack")); //Energy

		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(8), false, "kck", "rur", "ycy",
				'k', "dyeBlue",
				'c', c,
				'u', u,
				'r', "ingotGold",
				'y', "dyeBlack")); //Energy Regen
	}

}
