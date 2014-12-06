package tardis.common.core.store;

import tardis.common.core.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class SimpleCoordStore
{
	private final World worldObj;
	public final int world;
	public final int x;
	public final int y;
	public final int z;
	
	public SimpleCoordStore(TileEntity te)
	{
		world = te.getWorldObj().provider.dimensionId;
		worldObj = te.getWorldObj();
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
	}
	
	public SimpleCoordStore(int win, int xin, int yin, int zin)
	{
		world = win;
		x = xin;
		y = yin;
		z = zin;
		worldObj = Helper.getWorldServer(world);
	}
	
	public SimpleCoordStore(World w, int xin, int yin, int zin)
	{
		world = w.provider.dimensionId;
		worldObj = w;
		x = xin;
		y = yin;
		z = zin;
	}
	
	public SimpleCoordStore(EntityPlayer player)
	{
		world = Helper.getWorldID(player.worldObj);
		worldObj = player.worldObj;
		x = (int) Math.floor(player.posX);
		y = (int) Math.floor(player.posY);
		z = (int) Math.floor(player.posZ);
	}

	public World getWorldObj()
	{
		return worldObj;
	}
	
	@Override
	public String toString()
	{
		return "World "+world + ":" + x+ ","+ y+ ","+ z;
	}
	
	public String toSimpleString()
	{
		return x + "," + y + "," + z;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + world;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof SimpleCoordStore))
			return false;
		SimpleCoordStore other = (SimpleCoordStore) obj;
		if (world != other.world)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public NBTTagCompound writeToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return nbt;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("w", world);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
	}
	
	public static SimpleCoordStore readFromNBT(NBTTagCompound nbt)
	{
		if(!(nbt.hasKey("w") && nbt.hasKey("x") && nbt.hasKey("y") && nbt.hasKey("z")))
			return null;
		int w = nbt.getInteger("w");
		int x = nbt.getInteger("x");
		int y = nbt.getInteger("y");
		int z = nbt.getInteger("z");
		return new SimpleCoordStore(w,x,y,z);
	}

	public ChunkCoordIntPair toChunkCoords()
	{
		return new ChunkCoordIntPair(x>>4,z>>4);
	}
}
