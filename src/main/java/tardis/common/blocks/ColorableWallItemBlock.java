package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import net.minecraft.block.Block;
import tardis.TardisMod;

public class ColorableWallItemBlock extends AbstractItemBlock
{

	public ColorableWallItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.colorableWallBlock;
	}

}
