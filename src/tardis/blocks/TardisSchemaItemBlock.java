package tardis.blocks;

import tardis.TardisMod;

public class TardisSchemaItemBlock extends TardisAbstractItemBlock
{

	public TardisSchemaItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.schemaBlock;
	}

}
