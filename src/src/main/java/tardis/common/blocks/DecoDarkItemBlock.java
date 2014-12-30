package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class DecoDarkItemBlock extends AbstractItemBlock
{

	public DecoDarkItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.darkDecoBlock;
	}

}
