package tardis.common.tileents;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import tardis.TardisMod;
import tardis.api.IChunkLoader;
import tardis.client.TardisClientProxy;
import tardis.common.core.TardisOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;

public abstract class TardisAbstractTileEntity extends TileEntity
{
	public boolean init = false;
	public static Random rand = new Random();
	@Override
	public Packet getDescriptionPacket()
	{
		TardisOutput.print("TATE","Compiling description packet",TardisOutput.Priority.OLDDEBUG);
		NBTTagCompound tag = new NBTTagCompound();
		writeTransmittable(tag);
		Packet p = new Packet132TileEntityData(xCoord,yCoord,zCoord,3,tag);
		return p;
	}
	
	public void sendUpdate()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void updateEntity()
	{
		if(!init)
		{
			init = true;
			if(this instanceof IChunkLoader)
				TardisMod.chunkManager.loadMe((IChunkLoader)this);
		}
	}
	
	public void sendDataPacket()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
		{
			TardisOutput.print("TATE","Called sendDataPacket",TardisOutput.Priority.OLDDEBUG);
			Packet p = getDescriptionPacket();
			MinecraftServer serv = MinecraftServer.getServer();
			if(serv == null)
				return;
			ServerConfigurationManager conf = serv.getConfigurationManager();
			if(conf == null)
				return;
			conf.sendToAllNear(xCoord, yCoord, zCoord, 160, worldObj.provider.dimensionId, p);
		}
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet)
	{
		if(TardisMod.proxy instanceof TardisClientProxy)
			TardisClientProxy.cWorld = worldObj;
		TardisOutput.print("TATE","Receiving description packet",TardisOutput.Priority.OLDDEBUG);
		readTransmittable(packet.data);
		super.onDataPacket(net, packet);
	}
	
	public abstract void writeTransmittable(NBTTagCompound nbt);
	
	public abstract void readTransmittable(NBTTagCompound nbt);
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		readTransmittable(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		writeTransmittable(nbt);
	}
}
