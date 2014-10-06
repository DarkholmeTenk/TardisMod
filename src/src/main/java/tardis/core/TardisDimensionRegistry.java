package tardis.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;

import tardis.TardisMod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
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
		TardisOutput.print("TDR", "Adding dimension:" + id,TardisOutput.Priority.DEBUG);
		if(dimensionIDs.add(id))
			markDirty();
			
	}
	
	public static TardisDimensionRegistry load()
	{
		TardisOutput.print("TDR","Attempting to load tardis dimension registry");
		WorldSavedData data = MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.loadData(TardisDimensionRegistry.class, "TModDimReg");
		if(data instanceof TardisDimensionRegistry)
			return (TardisDimensionRegistry)data;
		return new TardisDimensionRegistry("TModDimReg");
	}

	public static void save()
	{
		TardisOutput.print("TDR", "Saving",TardisOutput.Priority.DEBUG);
		if(TardisMod.dimReg == null)
			TardisMod.dimReg = new TardisDimensionRegistry("TModDimReg");
		MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.setData("TModDimReg", TardisMod.dimReg);
	}
	
	public void registerDims()
	{
		for(Integer i:dimensionIDs)
			registerDim(i);
	}
	
	private void registerDim(int id)
	{
		TardisOutput.print("TDR", "Registering dim " + id,TardisOutput.Priority.DEBUG);
		if(!DimensionManager.isDimensionRegistered(id))
			DimensionManager.registerDimension(id, TardisMod.providerID);
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
	
	public static Packet250CustomPayload getPacket()
	{
		Packet250CustomPayload p = new Packet250CustomPayload();
		p.channel = "TardisDR";
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(bos);
			NBTTagCompound t = new NBTTagCompound();
			TardisMod.dimReg.writeToNBT(t);
			NBTTagCompound.writeNamedTag(t, stream);
			p.data = bos.toByteArray();
			p.length = p.data.length;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return p;
	}
}
