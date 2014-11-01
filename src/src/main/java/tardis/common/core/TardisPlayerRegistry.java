package tardis.common.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import tardis.TardisMod;
import tardis.common.tileents.TardisCoreTileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.WorldSavedData;

public class TardisPlayerRegistry extends WorldSavedData
{
	public HashMap<Integer,String> ownedDimMapping = new HashMap<Integer,String>();
	
	public TardisPlayerRegistry()
	{
		super("TardPlayReg");
	}
	
	public TardisPlayerRegistry(String s)
	{
		super(s);
	}
	
	public static TardisPlayerRegistry load()
	{
		TardisOutput.print("TPR","Attempting to load tardis player registry");
		try
		{
			WorldSavedData data = MinecraftServer.getServer().worldServerForDimension(0).perWorldStorage.loadData(TardisPlayerRegistry.class, "TardPlayReg");
			if(data instanceof TardisPlayerRegistry)
				return (TardisPlayerRegistry)data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new TardisPlayerRegistry();
	}

	public static void save()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
		{
			TardisOutput.print("TDR", "Saving",TardisOutput.Priority.DEBUG);
			if(TardisMod.plReg == null)
				TardisMod.plReg = new TardisPlayerRegistry();
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
		return getDimension(player.username);
	}
	
	public TardisCoreTileEntity getCore(EntityPlayer player)
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
		comsen.sendChatToPlayer(new ChatMessageComponent().addText("Dimension mapping:"));
		for(Integer i : ownedDimMapping.keySet())
		{
			String owner = ownedDimMapping.get(i);
			if(i != null)
				comsen.sendChatToPlayer(new ChatMessageComponent().addText(owner + "->" + i));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		int i = 0;
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
				nbt.setCompoundTag("store"+i, tag);
				i++;
			}
		}
	}

}
