package tardis.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.tileents.ComponentTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ComponentBlock extends AbstractBlockContainer
{
	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new ComponentTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("Component");
		setSubNames("Roundel","CorridorRoundel");
		setLightLevel(1F);
	}

	@Override
	public void initRecipes()
	{

	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return true;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return true;
	}

}