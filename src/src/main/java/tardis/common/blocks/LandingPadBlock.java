package tardis.common.blocks;

import tardis.common.tileents.LandingPadTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class LandingPadBlock extends TardisAbstractBlockContainer
{

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new LandingPadTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("LandingPad");
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

}
