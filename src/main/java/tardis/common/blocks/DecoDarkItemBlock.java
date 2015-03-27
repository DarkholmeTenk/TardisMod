package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
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
