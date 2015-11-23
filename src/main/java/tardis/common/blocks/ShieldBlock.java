package tardis.common.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.ShieldTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

public class ShieldBlock extends AbstractScrewableBlockContainer
{
	public ShieldBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new ShieldTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ShieldTileEntity.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ShieldBlock");
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1), "idi","crc","kkk",
				'k', "blockCoal",
				'i', "ingotIron",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'd', CraftingComponentType.DALEKANIUM.getIS(1),
				'r', "dustRedstone"));
	}

}
