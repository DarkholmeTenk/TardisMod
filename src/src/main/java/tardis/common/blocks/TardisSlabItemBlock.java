package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class TardisSlabItemBlock extends TardisAbstractItemBlock
{

	public TardisSlabItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.slabBlock;
	}

}
