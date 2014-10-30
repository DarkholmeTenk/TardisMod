package tardis.common.blocks;

import tardis.TardisMod;

public class TardisComponentItemBlock extends TardisAbstractItemBlock
{

	public TardisComponentItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.componentBlock;
	}

}
