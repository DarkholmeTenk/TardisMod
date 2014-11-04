package tardis.common.items;

import appeng.api.Materials;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.components.TardisTEComponent;

public class TardisComponentItem extends TardisAbstractItem
{

	public TardisComponentItem(int par1)
	{
		super(par1);
		setUnlocalizedName("Component");
		setSubNames(TardisTEComponent.getStrings());
		setMaxStackSize(8);
	}
	
	public ItemStack getIS(TardisTEComponent comp)
	{
		return new ItemStack(itemID,1,comp.ordinal());
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.ENERGY), true, "grg","iii","grg",
				'g', Item.goldNugget,
				'r', Item.redstone,
				'i', Item.ingotIron));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.FLUID), true, "grg","iii","grg",
				'g', Item.goldNugget,
				'r', Item.bucketEmpty,
				'i', Item.ingotIron));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.INVENTORY), true, "grg","iii","grg",
				'g', Item.goldNugget,
				'r', Block.chest,
				'i', Item.ingotIron));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.TRANSMAT), true, "grg","iii","grg",
				'g', Item.goldNugget,
				'r', Item.enderPearl,
				'i', Item.ingotIron));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.CHUNK), true, "grg","iii","gdg",
				'g', Item.goldNugget,
				'r', Item.enderPearl,
				'd', Item.diamond,
				'i', Item.ingotIron));
		
		ItemStack i = Materials.matQuartz;
		if(i != null)
		{
			TardisOutput.print("TCI","Registering AE recipe");
			GameRegistry.addRecipe(new ShapedOreRecipe(getIS(TardisTEComponent.GRID), true, "grg","iii","grg",
					'g', Item.goldNugget,
					'r', i.copy(),
					'i', Item.ingotIron));
		}
	}

}
