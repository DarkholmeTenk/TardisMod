package tardis.common.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.TMRegistry;
import tardis.common.tileents.TardisTileEntity;

public class TopBlock extends AbstractBlock
{
	public TopBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setLightLevel(1.0F);
		setBlockName("TardisTop");
	}

	@Override
	public void initRecipes()
	{

	}

	//Other things
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float j, float k, float l)
    {
    	if(world.getBlock(x, y-1, z) == TMRegistry.tardisBlock)
    	{
    		return TMRegistry.tardisBlock.onBlockActivated(world, x, y - 1, z, player, i, j, k, l);
    	}
    	else
    	{
    		world.setBlockToAir(x, y, z);
    		return true;
    	}
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
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB aabb, List l, Entity e)
    {
        TileEntity te = w.getTileEntity(x, y - 1, z);
        if(te instanceof TardisTileEntity)
        {
        	if(((TardisTileEntity)te).isLanding())
        	{
        		return;
        	}
        }
        super.addCollisionBoxesToList(w, x, y, z, aabb, l, e);
    }

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return false;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if(world.getBlock(x, y-1,z) == TMRegistry.tardisBlock)
			world.setBlockToAir(x, y-1, z);
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
	}

	@Override
	public boolean isNormalCube(IBlockAccess w, int x, int y, int z)
	{
		TileEntity te = w.getTileEntity(x, y-1, z);
		if(te instanceof TardisTileEntity)
		{
			if(((TardisTileEntity)te).isLanding())
				return false;
			return true;
		}
		else
			return false;
	}

}
