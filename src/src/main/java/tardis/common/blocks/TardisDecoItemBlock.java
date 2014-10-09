package tardis.common.blocks;

import tardis.TardisMod;

public class TardisDecoItemBlock extends TardisAbstractItemBlock
{

	public TardisDecoItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.decoBlock;
	}

}
