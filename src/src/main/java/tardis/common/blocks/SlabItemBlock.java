package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class SlabItemBlock extends AbstractItemBlock
{

	public SlabItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.slabBlock;
	}

}
