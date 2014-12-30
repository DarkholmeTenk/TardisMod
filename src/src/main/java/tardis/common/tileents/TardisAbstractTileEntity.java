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
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TardisAbstractTileEntity extends TileEntity
{
	public boolean init = false;
	public int tt = 0;
	private int lastUpdateTT = 0;
	private int updateCounter = 0;
	private static int updateInterval = 0;
	private static int updateCounterMax = 0;
	private static boolean updateQueued = false;
	public static Random rand = new Random();
	public SimpleCoordStore coords = null;
	
	@Override
	public Packet getDescriptionPacket()
	{
		TardisOutput.print("TATE","Compiling description packet @ "+xCoord + ","+yCoord+","+zCoord,TardisOutput.Priority.OLDDEBUG);
		NBTTagCompound tag = new NBTTagCompound();
		writeTransmittable(tag);
		writeTransmittableOnly(tag);
		Packet p = new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,3,tag);
		return p;
	}
	
	public void updateNeighbours()
	{
		Block b = worldObj.getBlock(xCoord, yCoord, zCoord);
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			Block d = worldObj.getBlock(xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ);
			if(d != null)
				d.onNeighborBlockChange(worldObj, xCoord+dir.offsetX,yCoord+dir.offsetY,zCoord+dir.offsetZ, b);
		}
	}
	
	private boolean canSendUpdate()
	{
		return lastUpdateTT + updateInterval <= tt && updateCounter < updateCounterMax;
	}
	
	public void sendUpdate()
	{
		if(!Helper.isServer())
			return;
		if(worldObj.playerEntities == null || worldObj.playerEntities.size() == 0)
			return;
		if(canSendUpdate())
		{
			TardisOutput.print("TATE", "Sending update " + getClass().getSimpleName());
			updateQueued = false;
			updateCounter++;
			lastUpdateTT = tt;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else
		{
			TardisOutput.print("TATE", "Update sending blocked to prevent spam " + getClass().getSimpleName());
			updateQueued = true;
		}
	}
	
	public void init() {}
	
	@Override
	public void updateEntity()
	{
		if(updateInterval == 0)
		{
			updateInterval = TardisMod.modConfig.getInt("updateInterval", 5);
			updateCounterMax = TardisMod.modConfig.getInt("updateCounterMax", 20);
		}
		
		if(coords == null)
			coords = new SimpleCoordStore(this);
		tt++;
		
		if(tt % 10 == 0 && updateCounter > 0)
			updateCounter--;
		
		if(updateQueued && canSendUpdate())
			sendUpdate();
		
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
