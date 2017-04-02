package tardis.common.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.SimulacrumBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.TardisMod;
import tardis.common.TMRegistry;
import tardis.common.tileents.extensions.CraftingComponentType;

public class CraftableSimBlock extends SimulacrumBlock
{

	public CraftableSimBlock(AbstractBlock simulating)
	{
		super(TardisMod.modName, simulating);
		setCreativeTab(TardisMod.cTab);
		setHardness(1.5F);
	}

	public static void initStaticRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.wallSimulacrumBlock,8, 15), false, "sss", "scs", "sss",
				's', "stone",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TMRegistry.wallSimulacrumBlock,1, 15),new ItemStack(TMRegistry.floorSimulacrumBlock,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TMRegistry.brickSimulacrumBlock,1, 15), new ItemStack(TMRegistry.wallSimulacrumBlock,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TMRegistry.plankSimulacrumBlock,1, 15), new ItemStack(TMRegistry.brickSimulacrumBlock,1,OreDictionary.WILDCARD_VALUE)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TMRegistry.floorSimulacrumBlock,1, 15), new ItemStack(TMRegistry.plankSimulacrumBlock,1,OreDictionary.WILDCARD_VALUE)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.glassSimulacrumBlock,8), false, "ggg","gcg","ggg",
				'g', "glass",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.decoSimulacrumBlock,8,1), false, "gbg","gcg","gbg",
				'g', "stone",
				'b', "dyeBrown",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.decoSimulacrumBlock,16,0), false, "iii","ici","iii",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TMRegistry.glassSimulacrumBlock,8,1), false, "gig","ici","gig",
				'g', "glass",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}
}
