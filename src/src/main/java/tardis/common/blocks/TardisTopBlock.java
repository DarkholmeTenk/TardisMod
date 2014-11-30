package tardis.common.blocks;

import tardis.TardisMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisTopBlock extends TardisAbstractBlock
{

	public TardisTopBlock()
	{
		super();
	}

	@Override
	public void initData()
	{
		setLightLevel(1.0F);
		setBlockName("TardisTop");
		setBlockBounds(0,-1,0,1,1,1);
	}

	@Override
	public void initRecipes()
	{
		
	}
	
	//Other things
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float j, float k, float l)
    {
    	if(world.getBlock(x, y-1, z) == TardisMod.tardisBlock)
    	{
    		return TardisMod.tardisBlock.onBlockActivated(world, x, y - 1, z, player, i, j, k, l);
    	}
    	return true;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		if(world.getBlock(x, y-1,z) == TardisMod.tardisBlock)
			world.setBlockToAir(x, y-1, z);
	}

}
