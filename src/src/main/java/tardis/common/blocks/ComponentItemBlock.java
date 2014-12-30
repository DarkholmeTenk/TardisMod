package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class ComponentItemBlock extends AbstractItemBlock
{

	public ComponentItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.componentBlock;
	}

}
