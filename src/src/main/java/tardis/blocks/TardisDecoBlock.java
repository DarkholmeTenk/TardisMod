package tardis.blocks;

import net.minecraft.world.IBlockAccess;

public class TardisDecoBlock extends TardisAbstractBlock {

	public TardisDecoBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("DecoBlock");
		setSubNames("Floor","Wall","Roundel","Corridor","CorridorRoundel","CorridorFloor","Glass");
		setLightValue(0.8F);
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
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int s)
    {
        int mX = x;
        int mY = y;
        int mZ = z;
        switch(s)
		{
			case 0: y++;break;
			case 1: y--;break;
			case 2: z++;break;
			case 3: z--;break;
			case 4: x++;break;
			case 5: x--;break;
		}
        if(w.getBlockId(mX, mY, mZ) == blockID && w.getBlockMetadata(mX, mY, mZ) == 6 && w.getBlockMetadata(x, y, z) == 6)
        	return false;
        return super.shouldSideBeRendered(w, s, mX, mY, mZ);
    }

}
