package tardis.common.integration.waila;

import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DummyWailaAccessor implements IWailaDataAccessor
{
	private MovingObjectPosition	mop		= null;
	private Block					b		= null;
	private int						meta	= -1;

	@Override
	public World getWorld()
	{
		return getPlayer().worldObj;
	}

	@Override
	public EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public Block getBlock()
	{
		return b;
	}

	@Override
	public int getBlockID()
	{
		return 0;
	}

	@Override
	public int getMetadata()
	{
		return meta;
	}

	@Override
	public TileEntity getTileEntity()
	{
		if(mop != null)
			return getWorld().getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		return null;
	}

	@Override
	public MovingObjectPosition getPosition()
	{
		return mop;
	}

	@Override
	public Vec3 getRenderingPosition()
	{
		return null;
	}

	@Override
	public NBTTagCompound getNBTData()
	{
		TileEntity te = getTileEntity();
		NBTTagCompound nbt = new NBTTagCompound();
		if(te != null)
			te.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public int getNBTInteger(NBTTagCompound tag, String keyname)
	{
		return 0;
	}

	@Override
	public double getPartialFrame()
	{
		return 0;
	}

	@Override
	public ForgeDirection getSide()
	{
		return null;
	}

	public void update(EntityPlayer pl, MovingObjectPosition _mop, Block _b, int _meta)
	{
		mop = _mop;
		b = _b;
		meta = _meta;
	}

}
