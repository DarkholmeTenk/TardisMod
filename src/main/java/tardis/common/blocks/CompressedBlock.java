package tardis.common.blocks;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.Configs;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;

public class CompressedBlock extends AbstractBlock
{
	
	public CompressedBlock()
	{
		super(TardisMod.modName);
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public void initData()
	{
		setBlockName("Block");
		setSubNames("Dalekanium", "Chronosteel");
		setLightLevel(Configs.lightBlocks ? 1 : 0);
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1,0), false, "ddd", "ddd", "ddd",
				'd', CraftingComponentType.DALEKANIUM.getIS(1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1,1), false, "ccc", "ccc", "ccc",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}

}
