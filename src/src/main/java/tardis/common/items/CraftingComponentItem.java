package tardis.common.items;

import java.util.EnumSet;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class CraftingComponentItem extends AbstractItem
{
	
	private static String[] names;
	
	static
	{
		names = new String[CraftingComponentType.values().length];
		CraftingComponentType[] types = CraftingComponentType.values();
		for(int i = 0;i<names.length;i++)
			names[i] = types[i].name;
	}
	
	public CraftingComponentItem()
	{
		setUnlocalizedName("CraftingComponent");
		setSubNames(names);
	}

	@Override
	public void initRecipes()
	{
		LabRecipe currentRecipe = new LabRecipe(
				new ItemStack[] { new ItemStack(Items.iron_ingot)},
				new ItemStack[]{CraftingComponentType.CHRONOSTEEL.getIS(1)},
				EnumSet.of(LabFlag.INFLIGHT),
				100);
		LabTileEntity.addRecipe(currentRecipe);
		currentRecipe = new LabRecipe(
				new ItemStack[] { new ItemStack(Items.gold_ingot) },
				new ItemStack[] { CraftingComponentType.DALEKANIUM.getIS(1) },
				EnumSet.noneOf(LabFlag.class),
				100);
		LabTileEntity.addRecipe(currentRecipe);
		currentRecipe = new LabRecipe(
				new ItemStack[] { new ItemStack(Items.diamond), new ItemStack(Items.ender_pearl) },
				new ItemStack[] { CraftingComponentType.KONTRON.getIS(1) },
				EnumSet.noneOf(LabFlag.class),
				100);
		LabTileEntity.addRecipe(currentRecipe);
	}

}
