package tardis.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.SummonerTileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class SummonerBlock extends AbstractScrewableBlockContainer
{
	public SummonerBlock()
	{
		super(false,TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("Summoner");
		setCreativeTab(TardisMod.cTab);
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(getIS(1,0),false,"wiw","ibi","wiw",
				'w', "dyeWhite",
				'i', "ingotIron",
				'b', "dyeBlue"));
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new SummonerTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return SummonerTileEntity.class;
	}

}
