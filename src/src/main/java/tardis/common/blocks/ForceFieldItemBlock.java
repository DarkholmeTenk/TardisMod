package tardis.common.blocks;

import tardis.TardisMod;
import net.minecraft.block.Block;

public class ForceFieldItemBlock extends AbstractItemBlock
{

	public ForceFieldItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.forcefield;
	}

}
