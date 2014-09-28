package tardis.blocks;

import tardis.TardisMod;

public class TardisSchemaComponentItemBlock extends TardisAbstractItemBlock
{

	public TardisSchemaComponentItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.schemaComponentBlock;
	}

}
