package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class SchemaComponentItemBlock extends AbstractItemBlock
{

	public SchemaComponentItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.schemaComponentBlock;
	}

}
