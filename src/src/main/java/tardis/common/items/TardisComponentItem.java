package tardis.common.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.components.TardisTEComponent;
import tardis.common.tileents.extensions.CraftingComponentType;

public class TardisComponentItem extends TardisAbstractItem
{

	public TardisComponentItem()
	{
		super();
		setUnlocalizedName("Component");
		setSubNames(TardisTEComponent.getStrings());
		setMaxStackSize(8);
	}
	
	public ItemStack getIS(TardisTEComponent comp)
	{
		return new ItemStack(this,1,comp.ordinal());
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.ENERGY), true, "grg","iii","grg",
				'g', Items.gold_nugget,
				'r', Items.redstone,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.FLUID), true, "grg","iii","grg",
				'g', Items.gold_nugget,
				'r', Items.bucket,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.INVENTORY), true, "grg","iii","grg",
				'g', Items.gold_nugget,
				'r', Blocks.chest,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.TRANSMAT), true, "grg","iii","grg",
				'g', Items.gold_nugget,
				'r', Items.ender_pearl,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.CHUNK), true, "grg","iii","gdg",
				'g', Items.gold_nugget,
				'r', Items.ender_pearl,
				'd', Items.diamond,
				'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.NANOGENE), true, "grg", "cac", "grg",
				'g', Items.gold_nugget,
				'r', Items.redstone,
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'a', Items.apple));
		
		if(TardisMod.aeAPI != null)
		{
			ItemStack i = TardisMod.aeAPI.materials().materialCertusQuartzCrystal.stack(1);
			if(i != null)
			{
				TardisOutput.print("TCI","Registering AE recipe");
				GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.GRID), true, "grg","iii","grg",
						'g', Items.gold_nugget,
						'r', i.copy(),
						'i', Items.iron_ingot));
			}
		}
	}

}
