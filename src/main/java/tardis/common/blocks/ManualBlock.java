package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.IRotatableBlock;
import tardis.common.tileents.ManualTileEntity;

public class ManualBlock extends AbstractBlockContainer implements IRotatableBlock
{

	public ManualBlock()
	{
		super(true, TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("Manual");
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if (entity == null)
			return;

		int dir = MathHelper.floor_double(((entity.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}

	@Override
	public TileEntity createNewTileEntity(World w, int x)
	{
		return new ManualTileEntity(w);
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ManualTileEntity.class;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		int meta = w.getBlockMetadata(x, y, z);
		if((meta == 0) && (s == 2)) return false;
		if((meta == 1) && (s == 5)) return false;
		if((meta == 2) && (s == 3)) return false;
		if((meta == 3) && (s == 4)) return false;
		return super.shouldSideBeRendered(w, s, x, y, z, mX, mY, mZ);
	}

	@Override
	public int rotatedMeta(int oldMeta, int oldFacing, int newFacing)
	{
		int diff = newFacing - oldFacing;
		int meta = oldMeta + diff;
		if(meta < 0) meta += 4;
		if(meta >= 3) meta -= 4;
		return meta;
	}

	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float t, float j, float k)
	{
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof ManualTileEntity)
		{
			((ManualTileEntity)te).activate(x,y,z,pl,s,t,j,k);
			return true;
		}
		return false;
	}

}
