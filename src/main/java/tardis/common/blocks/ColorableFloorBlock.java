package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import tardis.TardisMod;

public class ColorableFloorBlock extends AbstractBlock implements IColorableBlock
{

	public ColorableFloorBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableFloorItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableFloor");
	}

	@Override
	public void initRecipes()
	{
	}

}
