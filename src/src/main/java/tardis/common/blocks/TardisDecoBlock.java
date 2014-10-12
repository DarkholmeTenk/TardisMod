package tardis.common.blocks;

import net.minecraft.world.IBlockAccess;

public class TardisDecoBlock extends TardisAbstractBlock
{

	private static final String[] subs = {"Floor","Wall","Roundel","Corridor","CorridorRoundel","CorridorFloor","Glass", "WallPlain"};
	
	public TardisDecoBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("DecoBlock");
		setSubNames(subs);
		setLightValue(1F);
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
		if(w.getBlockId(mX, mY, mZ) == blockID && w.getBlockMetadata(mX, mY, mZ) == 7 && w.getBlockMetadata(x, y, z) == 7)
        	return false;
        return super.shouldSideBeRendered(w, s, x,y,z,mX, mY, mZ);
	}

}
