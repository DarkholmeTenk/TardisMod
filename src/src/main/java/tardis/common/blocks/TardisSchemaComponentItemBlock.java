package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class TardisSchemaComponentItemBlock extends TardisAbstractItemBlock
{

	public TardisSchemaComponentItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.schemaComponentBlock;
	}

}
