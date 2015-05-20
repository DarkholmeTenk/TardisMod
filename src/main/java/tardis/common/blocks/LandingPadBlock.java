package tardis.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.LandingPadTileEntity;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

public class LandingPadBlock extends AbstractScrewableBlock
{
	public LandingPadBlock()
	{
		super(TardisMod.modName);
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new LandingPadTileEntity();
	}

	@Override
	public String[] getIconSuffix()
	{
		return new String[] {"side","top","bottom"};
	}

	@Override
	public void initData()
	{
		setBlockName("LandingPad");
		setLightLevel(1F);
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return true;
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,1),true, "crc","dgd","cdc",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1),
				'r', Blocks.redstone_block,
				'd', CraftingComponentType.DALEKANIUM.getIS(1),
				'g', Items.diamond));
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return LandingPadTileEntity.class;
	}

}
