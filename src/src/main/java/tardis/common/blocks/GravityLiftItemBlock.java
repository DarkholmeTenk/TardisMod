package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class GravityLiftItemBlock extends AbstractItemBlock
{

	public GravityLiftItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.gravityLift;
	}

}
