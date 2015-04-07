package tardis.common.core;

import io.darkcraft.darkcore.mod.abstracts.AbstractWorldDataStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import tardis.TardisMod;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;

public class TardisOwnershipRegistry extends AbstractWorldDataStore
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
	
	public static void loadAll()
	{
		if(TardisMod.plReg == null)
			TardisMod.plReg = new TardisOwnershipRegistry();
		TardisMod.plReg.load();
	}

	public static void saveAll()
	{
		if(ServerHelper.isServer())
			TardisMod.plReg.save();
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
		return getDimension(ServerHelper.getUsername(player));
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
	
	public TardisDataStore getDataStore(EntityPlayer player)
	{
		Integer dimID = getDimension(player);
		if(dimID != null)
		{
			return Helper.getDataStore(dimID);
		}
		return null;
	}
	
	public EntityPlayerMP getPlayer(int dimension)
	{
		if(ownedDimMapping.containsKey(dimension))
			return ServerHelper.getPlayer(ownedDimMapping.get(dimension));
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
	public int getDimension()
	{
		return 0;
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
