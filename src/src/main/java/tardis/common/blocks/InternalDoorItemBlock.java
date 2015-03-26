package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import net.minecraft.block.Block;
import tardis.TardisMod;

public class InternalDoorItemBlock extends AbstractItemBlock
{

	public InternalDoorItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.internalDoorBlock;
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return (damage % 8) < 4 ? 0 : 4;
	}

}
