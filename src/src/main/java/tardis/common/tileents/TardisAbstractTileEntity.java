package tardis.common.tileents;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import tardis.TardisMod;
import tardis.api.IChunkLoader;
import tardis.client.TardisClientProxy;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;

public abstract class TardisAbstractTileEntity extends TileEntity
{
	public boolean init = false;
	public int tt = 0;
	public static Random rand = new Random();
	public SimpleCoordStore coords = null;
	
	@Override
	public Packet getDescriptionPacket()
	{
		TardisOutput.print("TATE","Compiling description packet",TardisOutput.Priority.OLDDEBUG);
		NBTTagCompound tag = new NBTTagCompound();
		writeTransmittable(tag);
		writeTransmittableOnly(tag);
		Packet p = new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,3,tag);
		return p;
	}
	
	public void sendUpdate()
	{
		if(worldObj.playerEntities == null || worldObj.playerEntities.size() == 0)
			return;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void updateEntity()
	{
		if(coords == null)
			coords = new SimpleCoordStore(this);
		tt++;
		if(!init)
		{
			init = true;
			if(Helper.isServer() && this instanceof IChunkLoader)
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
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
		if(TardisMod.proxy instanceof TardisClientProxy)
			TardisClientProxy.cWorld = worldObj;
		TardisOutput.print("TATE","Receiving description packet",TardisOutput.Priority.OLDDEBUG);
		NBTTagCompound nbt = packet.func_148857_g();
		readTransmittable(nbt);
		readTransmittableOnly(nbt);
		super.onDataPacket(net, packet);
	}
	
	public abstract void writeTransmittable(NBTTagCompound nbt);
	
	public abstract void readTransmittable(NBTTagCompound nbt);
	
	public void writeTransmittableOnly(NBTTagCompound nbt){}
	
	public void readTransmittableOnly(NBTTagCompound nbt){}
	
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
