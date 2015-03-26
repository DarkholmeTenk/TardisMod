package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoreBlock extends AbstractBlockContainer
{
	public CoreBlock()
	{
		super(TardisMod.modName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new CoreTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("TardisCore");
		setBlockBounds(0,-1,0,1,4,1);
		setLightLevel(1.0F);
	}

	@Override
	public void initRecipes()
	{
		
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
		return CoreTileEntity.class;
	}

}
