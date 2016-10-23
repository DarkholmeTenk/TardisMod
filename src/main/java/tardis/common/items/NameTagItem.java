package tardis.common.items;

import java.util.EnumSet;

import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tardis.TardisMod;
import tardis.common.recipes.LabRecipeRegistry;
import tardis.common.tileents.extensions.LabFlag;
import tardis.common.tileents.extensions.LabRecipe;

public class NameTagItem extends AbstractItem
{
	public NameTagItem()
	{
		super(TardisMod.modName);
		setUnlocalizedName("NameTag");
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public void initRecipes()
	{
		LabRecipeRegistry.addRecipe(new LabRecipe("tm.nametag",
				new ItemStack(Items.paper,1), new ItemStack(this,1), EnumSet.noneOf(LabFlag.class), 100));
	}

}
