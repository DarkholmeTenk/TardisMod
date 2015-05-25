package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.IRotatableBlock;
import tardis.common.tileents.ManualTileEntity;

public class ManualHelperBlock extends AbstractBlock implements IRotatableBlock
{

	public ManualHelperBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("ManualHelper");
		setBlockBounds(0,0,0,1,1,1);
	}

	@Override
	public void initRecipes()
	{
	}

	public static boolean isRightSide(int s, int meta)
	{
		return ManualTileEntity.isRightSide(s, meta);
	}

	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float t, float j, float k)
	{
		int m = w.getBlockMetadata(x, y, z);
		if(!isRightSide(s,m)) return false;
		boolean dX = (m%2) == 0;
		for(int yO = -1; yO <= 1; yO++)
		{
			for (int oO = -2; oO <= 2; oO++)
			{
				TileEntity te = w.getTileEntity(x+(dX?oO:0), y+yO, z+(dX?0:oO));
				if(te instanceof ManualTileEntity)
				{
					((ManualTileEntity)te).activate(x,y,z,pl,s,t,j,k);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		return TardisMod.manualBlock.shouldSideBeRendered(w, s, x, y, z, mX, mY, mZ);
	}

	@Override
	public int rotatedMeta(int oldMeta, int oldFacing, int newFacing)
	{
		int diff = newFacing - oldFacing;
		int meta = oldMeta + diff;
		if(meta < 0) meta += 4;
		if(meta >= 3) meta -= 4;
		return meta;
	}

}
