package tardis.common.blocks;

import tardis.common.tileents.TardisComponentTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisComponentBlock extends TardisAbstractBlockContainer
{

	public TardisComponentBlock(int par1)
	{
		super(par1);
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TardisComponentTileEntity();
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("Component");
		setSubNames("Roundel","CorridorRoundel");

	}

	@Override
	public void initRecipes()
	{

	}

}
