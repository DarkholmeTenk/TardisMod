package tardis.blocks;

import tardis.TardisMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisTopBlock extends TardisAbstractBlock
{

	public TardisTopBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("TardisTop");
	}

	@Override
	public void initRecipes()
	{
		
	}
	
	//Other things
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float j, float k, float l)
    {
    	if(world.getBlockId(x, y-1, z) == TardisMod.tardisBlock.blockID)
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
		if(world.getBlockId(x, y-1,z) == TardisMod.tardisBlock.blockID)
			world.setBlockToAir(x, y-1, z);
	}

}
