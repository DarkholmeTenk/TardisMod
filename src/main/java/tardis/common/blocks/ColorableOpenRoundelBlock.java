package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.ComponentTileEntity;

public class ColorableOpenRoundelBlock extends AbstractBlockContainer implements IColorableBlock
{

	public ColorableOpenRoundelBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableOpenRoundelItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableOpenRoundel");
		setLightLevel(1);
	}

	@Override
	public void initRecipes()
	{
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new ComponentTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ComponentTileEntity.class;
	}

}
