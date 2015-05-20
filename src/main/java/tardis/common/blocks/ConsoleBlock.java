package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.ConsoleTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConsoleBlock extends AbstractBlockContainer
{
	public ConsoleBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new ConsoleTileEntity(world);
	}

	@Override
	public void initData()
	{
		setBlockName("ConsoleBlock");
		setBlockBounds(0,0,0,1,1.5F,1);
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ConsoleTileEntity.class;
	}

}
