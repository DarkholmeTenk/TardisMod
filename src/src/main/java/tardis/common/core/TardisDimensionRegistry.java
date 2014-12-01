package tardis.common.core;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

import tardis.TardisMod;
import tardis.common.network.packet.TardisDimRegPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class TardisDimensionRegistry extends WorldSavedData implements GenericFutureListener
{
	public TardisDimensionRegistry(String par1Str)
	{
		super(par1Str);
	}

	private static HashSet<Integer> dimensionIDs = new HashSet<Integer>();
	
	public void addDimension(int id)
	{
		if(dimensionIDs.add(id))
		{
			TardisOutput.print("TDR", "Adding dimension:" + id,TardisOutput.Priority.DEBUG);
			markDirty();
		}
	}
	
	public static TardisDimensionRegistry load()
	{
		TardisOutput.print("TDR","Attempting to load tardis dimension registry");
		try
		{
			WorldSavedData data = MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.loadData(TardisDimensionRegistry.class, "TModDimReg");
			if(data instanceof TardisDimensionRegistry)
				return (TardisDimensionRegistry)data;
		}
		catch(Exception e)
		{
			
		}
		return new TardisDimensionRegistry("TModDimReg");
	}

	public static void save()
	{
		if(Helper.isServer())
		{
			TardisOutput.print("TDR", "Saving",TardisOutput.Priority.DEBUG);
			if(TardisMod.dimReg == null)
				TardisMod.dimReg = new TardisDimensionRegistry("TModDimReg");
			MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.setData("TModDimReg", TardisMod.dimReg);
		}
	}
	
	public void registerDims()
	{
		for(Integer i:dimensionIDs)
			registerDim(i);
	}
	
	private void registerDim(int id)
	{
		if(!DimensionManager.isDimensionRegistered(id))
		{
			TardisOutput.print("TDR", "Registering dim " + id,TardisOutput.Priority.DEBUG);
			DimensionManager.registerDimension(id, TardisMod.providerID);
			if(Helper.isServer())
				TardisMod.networkChannel.sendToAll(getPacket());
		}
	}
	
	public boolean hasDimension(int id)
	{
		return dimensionIDs.contains(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		//TardisOutput.print("TDR", "Reading from nbt");
		int[] dims = nbt.getIntArray("registeredDimensions");
		if(dims != null)
		{
			for(int curr: dims)
			{
				addDimension(curr);
			}
		}
		save();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		//TardisOutput.print("TDR", "Writing to nbt:" + dimensionIDs.size(),TardisOutput.Priority.DEBUG);
		int[] dims = new int[dimensionIDs.size()];
		int i=0;
		for(Integer curr:dimensionIDs)
		{
			dims[i] = curr;
			i++;
		}
		nbt.setIntArray("registeredDimensions", dims);
	}
	
	public static TardisDimRegPacket getPacket()
	{
		NBTTagCompound t = new NBTTagCompound();
		TardisMod.dimReg.writeToNBT(t);
		return new TardisDimRegPacket(Unpooled.buffer(),t);
	}
	
	@SubscribeEvent
	public void sendPacket(PlayerLoggedInEvent event)
	{
		sendPacket((PlayerEvent)event);
	}
	
	@SubscribeEvent
	public void sendPacket(PlayerChangedDimensionEvent event)
	{
		sendPacket((PlayerEvent)event);
	}
	
	@SubscribeEvent
	public void sendPacket(ServerConnectionFromClientEvent event)
	{
		event.manager.scheduleOutboundPacket(getPacket(), this);
	}
	
	public void sendPacket(PlayerEvent event)
	{
		EntityPlayer pl = event.player;
		if(pl instanceof EntityPlayerMP && TardisMod.networkChannel != null)
			TardisMod.networkChannel.sendTo(getPacket(),(EntityPlayerMP) pl);
	}

	@Override
	public void operationComplete(Future future) throws Exception
	{
	}
}
