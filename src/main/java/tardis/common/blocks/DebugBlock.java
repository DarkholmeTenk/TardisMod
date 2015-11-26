package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import tardis.TardisMod;

public class DebugBlock extends AbstractBlock
{

	public DebugBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("DebugBlock");
	}

	@Override
	public void initRecipes()
	{

	}

}
