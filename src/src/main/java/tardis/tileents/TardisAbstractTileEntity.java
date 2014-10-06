package tardis.tileents;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import tardis.core.TardisOutput;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;

public abstract class TardisAbstractTileEntity extends TileEntity
{
	public static Random rand = new Random();
	@Override
	public Packet getDescriptionPacket()
	{
		TardisOutput.print("TATE","Compiling description packet",TardisOutput.Priority.DEBUG);
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		Packet p = new Packet132TileEntityData(xCoord,yCoord,zCoord,3,tag);
		return p;
	}
	
	public void sendDataPacket()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
		{
			TardisOutput.print("TATE","Called sendDataPacket",TardisOutput.Priority.DEBUG);
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
		TardisOutput.print("TATE","Receiving description packet",TardisOutput.Priority.DEBUG);
		readFromNBT(packet.data);
		super.onDataPacket(net, packet);
	}
}
