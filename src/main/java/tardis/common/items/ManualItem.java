package tardis.common.items;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

public class ManualItem extends AbstractItem
{

	public ManualItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("ManualItem");
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1), false, " i ", "igi"," k ",
				'i', "ingotIron",
				'g', "blockGlass",
				'k', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}

}
