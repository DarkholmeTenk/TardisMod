package tardis.common.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashSet;

import tardis.TardisMod;
import tardis.common.network.packet.TardisDimRegPacket;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class TardisDimensionRegistry extends WorldSavedData
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
			TardisMod.networkChannel.sendToAll(getPacket());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		TardisOutput.print("TDR", "Reading from nbt");
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
		TardisOutput.print("TDR", "Writing to nbt:" + dimensionIDs.size(),TardisOutput.Priority.DEBUG);
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
}
