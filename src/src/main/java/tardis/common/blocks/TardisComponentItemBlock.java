package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class TardisComponentItemBlock extends TardisAbstractItemBlock
{

	public TardisComponentItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.componentBlock;
	}

}
