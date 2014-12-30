package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class DecoItemBlock extends AbstractItemBlock
{

	public DecoItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.decoBlock;
	}

}
