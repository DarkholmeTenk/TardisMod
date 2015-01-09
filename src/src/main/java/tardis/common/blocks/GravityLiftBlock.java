package tardis.common.blocks;

import tardis.common.tileents.GravityLiftTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
