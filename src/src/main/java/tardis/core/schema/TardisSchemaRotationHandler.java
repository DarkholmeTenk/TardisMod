package tardis.core.schema;

import tardis.TardisMod;
import tardis.core.TardisOutput;
import net.minecraft.block.Block;

public class TardisSchemaRotationHandler
{
	
	private static boolean facingOpposite(int a, int b)
	{
		if((a == 0 && b == 2) || (b == 0 && a == 2))
			return true;
		if((a == 1 && b == 3) || (b == 1 && a == 3))
			return true;
		return false;
	}
	
	private static int getChestMetadata(int blockMeta, int oldFacing, int newFacing)
	{
		int newMeta = getBaseMetadata(blockMeta-2,oldFacing,newFacing)+2;
		TardisOutput.print("TSRH", "ChestRot:"+blockMeta+","+newMeta+" for facing:"+oldFacing+","+newFacing);
		return newMeta;
	}
	
	private static int getStairsMetadata(int blockMeta,int oldFacing, int newFacing)
	{
		int add   = (blockMeta / 4) * 4;
		int cMeta = blockMeta % 4;
		int newMeta = getBaseMetadata(cMeta,newFacing,oldFacing);
		TardisOutput.print("TSRH", "Stair:"+blockMeta+","+add+"->"+newMeta+"#"+oldFacing+","+newFacing);
		return newMeta+add;
	}
	
	private static int getBaseMetadata(int blockMeta,int oldFacing, int newFacing)
	{
		if(facingOpposite(oldFacing,newFacing))
		{
			switch(blockMeta)
			{
			case 0: return 1;
			case 1: return 0;
			case 2: return 3;
			case 3: return 2;
			}
		}
		else if((oldFacing + 1)%4 == newFacing)
		{
			switch(blockMeta)
			{
			case 0: return 3;
			case 1: return 2;
			case 2: return 0;
			case 3: return 1;
			}
		}
		else if((newFacing+1)%4 == oldFacing)
		{
			switch(blockMeta)
			{
			case 0: return 2;
			case 1: return 3;
			case 2: return 1;
			case 3: return 0;
			}
		}
		return blockMeta;
	}
	
	public static int getNewMetadata(int blockID, int blockMeta, int oldFacing, int newFacing)
	{
		if(oldFacing == newFacing)
			return blockMeta;
		Block[] stairs = {Block.stairsBrick,Block.stairsCobblestone,Block.stairsNetherBrick,Block.stairsNetherQuartz,Block.stairsSandStone,Block.stairsStoneBrick,Block.stairsWoodBirch,Block.stairsWoodJungle,Block.stairsWoodSpruce,Block.stairsWoodOak, TardisMod.stairBlock};
		if(blockID == Block.chest.blockID)
			return getChestMetadata(blockMeta,oldFacing,newFacing);
		for(Block b: stairs)
		{
			if(blockID == b.blockID)
				return getStairsMetadata(blockMeta, oldFacing, newFacing);
		}
		return blockMeta;
	}
}
