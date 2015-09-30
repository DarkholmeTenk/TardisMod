package tardis.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.MagicDoorTileEntity;

public class InternalMagicDoorBlock extends AbstractScrewableBlockContainer
{

	public InternalMagicDoorBlock()
	{
		super(TardisMod.modName);
		setBlockName("MagicDoor");
	}

	@Override
	public void initData()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new MagicDoorTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return MagicDoorTileEntity.class;
	}

}
