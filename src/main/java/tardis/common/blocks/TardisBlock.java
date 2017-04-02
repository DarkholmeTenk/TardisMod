package tardis.common.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.TMRegistry;
import tardis.common.tileents.TardisTileEntity;

public class TardisBlock extends AbstractBlockContainer
{
	public TardisBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new TardisTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("Tardis");
	}

	@Override
	public void initRecipes()
	{

	}

	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB aabb, List l, Entity e)
    {
        TileEntity te = w.getTileEntity(x, y, z);
        if(te instanceof TardisTileEntity)
        	if(((TardisTileEntity)te).isLanding())
        		return;
        super.addCollisionBoxesToList(w, x, y, z, aabb, l, e);
    }

	//OTHER THINGS
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float j, float k, float l)
    {
    	super.onBlockActivated(world, x, y, z, player, i, j, k, l);
    	int myMeta = world.getBlockMetadata(x, y, z);
    	if(((myMeta == 0) && (i == 2)) || ((myMeta == 1) && (i == 5)) || ((myMeta == 2) && (i == 3)) || ((myMeta == 3) && (i == 4)))
    	{
    		TileEntity te = world.getTileEntity(x, y, z);
    		if(te instanceof TardisTileEntity)
    		{
    			TardisTileEntity tte = (TardisTileEntity) te;
    			tte.doorActivated(world,x,y,z,player);
    			return true;
    		}
    	}
        return false;
    }


	//PLACING AND DESTROYING THINGS

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		if(world.isAirBlock(x, y, z) && world.isAirBlock(x,y+1,z))
			return true;
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if (entity == null)
		{
			return;
		}

		int dir = MathHelper.floor_double(((entity.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
		world.setBlock(x, y+1, z, TMRegistry.tardisTopBlock, dir, 3);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
		if(world.getBlock(x, y+1,z) == TMRegistry.tardisTopBlock)
			world.setBlockToAir(x, y+1, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}

	@Override
	public boolean isNormalCube(IBlockAccess w, int x, int y, int z)
	{
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof TardisTileEntity)
		{
			if(((TardisTileEntity)te).isLanding())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return TardisTileEntity.class;
	}
}
