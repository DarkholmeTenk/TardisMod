package tardis.common.blocks;

import tardis.TardisMod;
import net.minecraft.block.Block;

public class TardisDarkDecoItemBlock extends TardisAbstractItemBlock
{

	public TardisDarkDecoItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.darkDecoBlock;
	}

}
