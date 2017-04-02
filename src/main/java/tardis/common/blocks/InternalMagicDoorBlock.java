package tardis.common.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import tardis.TardisMod;
import tardis.common.tileents.MagicDoorTileEntity;

public class InternalMagicDoorBlock extends AbstractScrewableBlockContainer
{

	public InternalMagicDoorBlock()
	{
		super(false, TardisMod.modName);
		setBlockName("MagicDoor");
	}

	@Override
	public void initData()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new MagicDoorTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return MagicDoorTileEntity.class;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return false;
	}

	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB aabbIn, List list, Entity ent)
	{
		/*int facing = w.getBlockMetadata(x, y, z) % 4;
		AxisAlignedBB aabb = null;
		switch(facing)
		{
			case 0: aabb = AxisAlignedBB.getBoundingBox(x, y - 1, z - 1, x +0.3, y + 2, z + 2); break;
			case 2: aabb = AxisAlignedBB.getBoundingBox(x + 0.7, y - 1, z - 1, x + 1, y + 2, z + 2); break;
			case 1: aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z, x + 2, y + 2, z + 0.3); break;
			case 3: aabb = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z + 0.7, x + 2, y + 2, z + 1); break;
		}
		if(aabb != null)
			list.add(aabb);*/
		return;
	}
}
