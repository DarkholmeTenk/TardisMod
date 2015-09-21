package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.SimulacrumBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TardisMod.wallSimulacrumBlock,8,15), false, "sss", "scs", "sss",
				's', "stone",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TardisMod.wallSimulacrumBlock,1,15), TardisMod.floorSimulacrumBlock));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TardisMod.floorSimulacrumBlock,1,15), TardisMod.wallSimulacrumBlock));
	}
}
