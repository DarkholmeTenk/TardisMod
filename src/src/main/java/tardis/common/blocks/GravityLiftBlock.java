package tardis.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import tardis.common.tileents.GravityLiftTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GravityLiftBlock extends AbstractBlockContainer
{
	private final String[] suffixes = new String[] { "top", "bottomsides" };

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new GravityLiftTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("GravityLift");
		setSubNames("Normal","Craftable");
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1,1), false, "ddd", "ici", "iii",
				'd', CraftingComponentType.DALEKANIUM.getIS(1),
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}
	
	@Override
	public String[] getIconSuffix()
	{
		return suffixes;
	}
	
	@Override
	public int getIconSuffixes()
	{
		return 2;
	}

}
