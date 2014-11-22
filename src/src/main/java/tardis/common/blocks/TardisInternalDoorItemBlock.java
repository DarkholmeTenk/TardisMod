package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class TardisInternalDoorItemBlock extends TardisAbstractItemBlock
{

	public TardisInternalDoorItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.internalDoorBlock;
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return (damage % 8) < 4 ? 0 : 4;
	}

}
