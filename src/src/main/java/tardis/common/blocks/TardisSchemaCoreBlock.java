package tardis.common.blocks;

import tardis.common.tileents.TardisSchemaCoreTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisSchemaCoreBlock extends TardisAbstractBlockContainer
{

	public TardisSchemaCoreBlock(int par1)
	{
		super(par1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TardisSchemaCoreTileEntity();
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("SchemaCore");
		
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}

}
