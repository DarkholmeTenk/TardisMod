package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.ComponentTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ComponentBlock extends AbstractBlockContainer
{
	public ComponentBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new ComponentTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("Component");
		setSubNames("Roundel","CorridorRoundel");
		setLightLevel(1F);
	}

	@Override
	public void initRecipes()
	{

	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return true;
	}

	@Override
	public boolean isOpaqueCube()
	{
	   return true;
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ComponentTileEntity.class;
	}

}
