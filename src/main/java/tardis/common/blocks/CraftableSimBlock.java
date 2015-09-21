package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.SimulacrumBlock;
import tardis.TardisMod;

public class CraftableSimBlock extends SimulacrumBlock
{

	public CraftableSimBlock(AbstractBlock simulating)
	{
		super(TardisMod.modName, simulating);
		setCreativeTab(TardisMod.cTab);
		setHardness(1.5F);
	}

}
