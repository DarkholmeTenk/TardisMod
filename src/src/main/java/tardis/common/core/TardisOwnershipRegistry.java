package tardis.common.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import tardis.TardisMod;
import tardis.common.tileents.CoreTileEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldSavedData;

public class TardisOwnershipRegistry extends WorldSavedData
{
	public HashMap<Integer,String> ownedDimMapping = new HashMap<Integer,String>();
	
	public TardisOwnershipRegistry()
	{
		super("TModPReg");
	}
	
	public TardisOwnershipRegistry(String s)
	{
		super("TModPReg");
	}
	
	public static TardisOwnershipRegistry load()
	{
		TardisOutput.print("TPR","Attempting to load tardis player registry");
		try
		{
			WorldSavedData data = MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.loadData(TardisOwnershipRegistry.class, "TModPReg");
			TardisOutput.print("TPlReg", "Player registry " + data.toString());
			if(data != null && data instanceof TardisOwnershipRegistry)
				return (TardisOwnershipRegistry)data;
		}
		catch(Exception e)
		{
			TardisOutput.print("TPlReg", e.getMessage(),TardisOutput.Priority.ERROR);
			//e.printStackTrace();
		}
		return new TardisOwnershipRegistry();
	}

	public static void save()
	{
		if(Helper.isServer())
		{
			TardisOutput.print("TDR", "Saving",TardisOutput.Priority.DEBUG);
			if(TardisMod.plReg == null)
				TardisMod.plReg = load();
			MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.setData("TardPlayReg", TardisMod.plReg);
		}
	}
	
	public boolean addPlayer(String username, int dimension)
	{
		if(username == null || dimension == 0)
			return false;
		TardisOutput.print("TPlReg", "Mapping dim " + dimension + " to " + username);
		if(hasTardis(username))
			return false;
		ownedDimMapping.put(dimension,username);
		markDirty();
		return true;
	}
	
	public boolean removePlayer(String username)
	{
		if(username != null)
		{
			return ownedDimMapping.values().remove(username);
		}
		return false;
	}
	
	public Integer getDimension(String username)
	{
		Set<Integer> dims = ownedDimMapping.keySet();
		if(dims != null)
		{
			for(Integer i: dims)
			{
				if(ownedDimMapping.get(i) == null)
					dims.remove(i);
				else if(ownedDimMapping.get(i).equals(username))
					return i;
			}
		}
		return null;
	}
	
	public Integer getDimension(EntityPlayer player)
	{
		return getDimension(Helper.getUsername(player));
	}
	
	public CoreTileEntity getCore(EntityPlayer player)
	{
		Integer dimID = getDimension(player);
		if(dimID != null)
		{
			return Helper.getTardisCore(dimID);
		}
		return null;
	}
	
	public EntityPlayerMP getPlayer(int dimension)
	{
		if(ownedDimMapping.containsKey(dimension))
			return Helper.getPlayer(ownedDimMapping.get(dimension));
		return null;
	}
	
	public boolean hasTardis(String username)
	{
		Collection<String> values = ownedDimMapping.values();
		if(values != null)
			return values.contains(username);
		return false;
	}
	
	public void chatMapping(ICommandSender comsen)
	{
		comsen.addChatMessage(new ChatComponentText("Dimension mapping:"));
		for(Integer i : ownedDimMapping.keySet())
		{
			String owner = ownedDimMapping.get(i);
			if(i != null)
				comsen.addChatMessage(new ChatComponentText(owner + "->" + i));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		int i = 0;
		TardisOutput.print("TPlReg", "Reading from NBT");
		while(nbt.hasKey("store"+i))
		{
			NBTTagCompound tag = nbt.getCompoundTag("store"+i);
			String un = tag.getString("username");
			Integer dim = tag.getInteger("dimension");
			TardisOutput.print("TPlReg", "NBT Load: Mapping " + un +"->" +dim);
			addPlayer(un,dim);
			i++;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		TardisOutput.print("TPlReg", "Saving to NBT");
		int i = 0;
		Set<Integer> dims = ownedDimMapping.keySet();
		for(Integer j : dims)
		{
			NBTTagCompound tag = new NBTTagCompound();
			String s = ownedDimMapping.get(j);
			if(s != null)
			{
				tag.setString("username", s);
				tag.setInteger("dimension", j);
				nbt.setTag("store"+i, tag);
				i++;
			}
		}
	}

}
