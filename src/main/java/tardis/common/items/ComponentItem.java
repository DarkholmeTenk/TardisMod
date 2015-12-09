package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import tardis.common.integration.ae.AEHelper;
import tardis.common.tileents.components.TardisTEComponent;
import tardis.common.tileents.extensions.CraftingComponentType;
import thaumcraft.api.ItemApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class ComponentItem extends AbstractItem
{

	public ComponentItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("Component");
		setSubNames(TardisTEComponent.getStrings());
		setMaxStackSize(8);
		setCreativeTab(TardisMod.cTab);
	}

	public ItemStack getIS(TardisTEComponent comp)
	{
		return new ItemStack(this,1,comp.ordinal());
	}

	@Override
	public void initRecipes()
	{
		if(Loader.isModLoaded("CoFHCore"))
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

		if(AEHelper.aeAPI != null)
		{
			ItemStack i = AEHelper.aeAPI.materials().materialCertusQuartzCrystal.stack(1);
			if(i != null)
			{
				TardisOutput.print("TCI","Registering AE recipe");
				GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.GRID), true, "grg","iii","grg",
						'g', Items.gold_nugget,
						'r', i.copy(),
						'i', Items.iron_ingot));
			}
		}

		ItemStack shard = ItemApi.getItem("itemResource", 0);
		if(shard != null)
		{
			TardisOutput.print("TCI","Registering TC recipe");
			GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.THAUMCRAFT), true, "grg","iii","grg",
					'g', Items.gold_nugget,
					'r', shard,
					'i', "ingotThaumium"));
		}
	}

}
