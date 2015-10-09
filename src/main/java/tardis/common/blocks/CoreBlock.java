package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.Helper;
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
		return new CoreTileEntity(world);
	}

	@Override
	public void initData()
	{
		setBlockName("TardisCore");
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

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if(Helper.isTardisWorld(world) && (x == Helper.tardisCoreX) && (y == Helper.tardisCoreY) && (z == Helper.tardisCoreZ))
		{
			TileEntity te = world.getTileEntity(x, y, z);
			NBTTagCompound nbt = new NBTTagCompound();
			if(te != null)
				te.writeToNBT(nbt);
			world.setBlock(x, y, z, this);
			TileEntity newTE = world.getTileEntity(x, y, z);
			if((te != null) && (newTE != null))
				newTE.readFromNBT(nbt);
		}
	}

	@Override
    public boolean canPlaceBlockAt(World w, int x, int y, int z)
    {
		if(!Helper.isTardisWorld(w))
			return false;
		else
		{
			if((x != Helper.tardisCoreX) || (y != Helper.tardisCoreY) || (z != Helper.tardisCoreZ))
				return false;
			return true;
		}
    }
}
