package tardis.common.blocks;

import io.darkcraft.darkcore.mod.impl.DefaultItemBlock;
import net.minecraft.block.Block;

public class InternalDoorItemBlock extends DefaultItemBlock
{

	public InternalDoorItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	public int getMetadata(int damage)
	{
		return (damage % 8) < 4 ? 0 : 4;
	}

}
