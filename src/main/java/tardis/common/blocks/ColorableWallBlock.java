package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import tardis.TardisMod;

public class ColorableWallBlock extends AbstractBlock implements IColorableBlock
{

	public ColorableWallBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableWallItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableWall");
	}

	@Override
	public void initRecipes()
	{
	}

}
