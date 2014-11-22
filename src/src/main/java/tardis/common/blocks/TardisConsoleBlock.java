package tardis.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.tileents.TardisConsoleTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TardisConsoleBlock extends TardisAbstractBlockContainer
{

	public TardisConsoleBlock()
	{
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new TardisConsoleTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("ConsoleBlock");

	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}

}
