package tardis.common.blocks;

import net.minecraft.block.Block;
import tardis.TardisMod;

public class TardisDecoDarkItemBlock extends TardisAbstractItemBlock
{

	public TardisDecoDarkItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.darkDecoBlock;
	}

}
