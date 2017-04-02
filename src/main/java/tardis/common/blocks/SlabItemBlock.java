package tardis.common.blocks;

import net.minecraft.block.Block;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;

import tardis.common.TMRegistry;

public class SlabItemBlock extends AbstractItemBlock
{

	public SlabItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TMRegistry.slabBlock;
	}

}
