package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import tardis.TardisMod;

public class ColorableRoundelBlock extends AbstractBlock implements IColorableBlock
{

	public ColorableRoundelBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableRoundelItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableRoundel");
		setLightLevel(1);
	}

	@Override
	public void initRecipes()
	{
	}

	/*
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new DummyRoundelTE();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return DummyRoundelTE.class;
	}
	*/
}
