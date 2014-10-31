package tardis.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.tileents.TardisComponentTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
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
		setLightValue(1F);
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
