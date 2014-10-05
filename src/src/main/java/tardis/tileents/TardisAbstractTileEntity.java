package tardis.tileents;

import java.util.Random;

import tardis.core.TardisOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public abstract class TardisAbstractTileEntity extends TileEntity
{
	public static Random rand = new Random();
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		Packet p = new Packet132TileEntityData(xCoord,yCoord,zCoord,3,tag);
		return p;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet)
	{
		readFromNBT(packet.data);
		super.onDataPacket(net, packet);
	}
}
