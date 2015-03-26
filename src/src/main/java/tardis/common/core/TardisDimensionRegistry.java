package tardis.common.core;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.network.DataPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import tardis.TardisMod;
import tardis.common.network.TardisPacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class TardisDimensionRegistry extends AbstractWorldDataStore implements GenericFutureListener
{
	public TardisDimensionRegistry()
	{
		this("TDimReg");
	}
	
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
	
	public static void loadAll()
	{
		if(TardisMod.dimReg == null)
			TardisMod.dimReg = new TardisDimensionRegistry();
		TardisMod.dimReg.load();
	}

	public static void saveAll()
	{
		if(ServerHelper.isServer())
			TardisMod.dimReg.save();
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
			if(ServerHelper.isServer())
				DarkcoreMod.networkChannel.sendToAll(getPacket());
		}
	}
	
	public boolean hasDimension(int id)
	{
		return dimensionIDs.contains(id);
	}

	@Override
	public int getDimension()
	{
		return 0;
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
		saveAll();
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
	
	public static DataPacket getPacket()
	{
		NBTTagCompound t = new NBTTagCompound();
		TardisMod.dimReg.writeToNBT(t);
		return new DataPacket(t,TardisPacketHandler.dimRegFlag);
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
		if(pl instanceof EntityPlayerMP && DarkcoreMod.networkChannel != null)
			DarkcoreMod.networkChannel.sendTo(getPacket(),(EntityPlayerMP) pl);
	}

	@Override
	public void operationComplete(Future future) throws Exception
	{
	}
}
