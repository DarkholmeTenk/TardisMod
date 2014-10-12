package tardis.common.blocks;

import tardis.TardisMod;

public class TardisSlabItemBlock extends TardisAbstractItemBlock
{

	public TardisSlabItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.slabBlock;
	}

}
