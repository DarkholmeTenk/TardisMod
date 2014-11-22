package tardis.common.blocks;

import net.minecraft.world.IBlockAccess;

public class TardisDecoBlock extends TardisAbstractBlock
{

	private static final String[] subs = {"Floor","Wall","Roundel","Corridor","CorridorRoundel","CorridorFloor","Glass", "WallPlain"};
	
	public TardisDecoBlock()
	{
		super();
	}

	@Override
	public void initData()
	{
		setBlockName("DecoBlock");
		setSubNames(subs);
		setLightLevel(1F);
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public void initRecipes()
	{
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		if(w.getBlock(mX, mY, mZ) == this && w.getBlockMetadata(mX, mY, mZ) == w.getBlockMetadata(x, y, z))
        	return false;
        return super.shouldSideBeRendered(w, s, x,y,z,mX, mY, mZ);
	}

}
