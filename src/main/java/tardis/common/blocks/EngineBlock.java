package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.EngineTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EngineBlock extends AbstractBlockContainer
{
	public EngineBlock()
	{
		super(false,TardisMod.modName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new EngineTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("TardisEngineBlock");
		setLightLevel(1F);
		setBlockBounds(0,-1,0,1,3,1);
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
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float i, float j, float k)
    {
		boolean superEffect = super.onBlockActivated(w, x, y, z, pl, s, i, j, k);
		TileEntity te = w.getTileEntity(x,y,z);
		if(te instanceof EngineTileEntity)
		{
			superEffect = ((EngineTileEntity)te).activate(pl, s, y, i, j, k);;
		}
		return superEffect;
    }

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return EngineTileEntity.class;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if(Helper.isTardisWorld(world) && x == Helper.tardisCoreX && y == Helper.tardisCoreY - 5 && z == Helper.tardisCoreZ)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			NBTTagCompound nbt = new NBTTagCompound();
			if(te != null)
				te.writeToNBT(nbt);
			world.setBlock(x, y, z, this);
			TileEntity newTE = world.getTileEntity(x, y, z);
			if(te != null && newTE != null)
				newTE.readFromNBT(nbt);
		}
	}
}
