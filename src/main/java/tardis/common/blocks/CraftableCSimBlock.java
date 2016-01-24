package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.interfaces.IBlockIteratorCondition;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;

public class CraftableCSimBlock extends CraftableSimBlock implements IColorableBlock
{

	public CraftableCSimBlock(AbstractBlock simulating)
	{
		super(simulating);
	}

	@Override
	public IBlockIteratorCondition getColoringIterator(SimpleCoordStore coord)
	{
		return ColorableBlock.getIterator(coord);
	}

}
