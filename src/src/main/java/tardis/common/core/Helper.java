package tardis.common.core;

import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tardis.TardisMod;
import tardis.common.core.exception.schema.UnmatchingSchemaException;
import tardis.common.core.schema.PartBlueprint;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.dimension.TardisTeleportHelper;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.entities.particles.ParticleType;
import tardis.common.network.packet.ParticlePacket;
import tardis.common.network.packet.ControlPacket;
import tardis.common.network.packet.SoundPacket;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class Helper
{
	public static final int tardisCoreX = 0;
	public static final int tardisCoreY = 30;
	public static final int tardisCoreZ = 0;
	
	public static int cycle(int val, int min, int max)
	{
		if(val < min)
			return max;
		if(val > max)
			return min;
		return val;
	}
	
	public static int clamp(int val, int min, int max)
	{
		return Math.min(max, Math.max(min,val));
	}
	
	public static double clamp(double val,double min, double max)
	{
		return Math.min(max, Math.max(min,val));
	}
	
	public static int toInt(String str, int def)
	{
		try
		{
			return Integer.parseInt(str);
		} catch(NumberFormatException e){}
		return def;
	}
	
	public static double toDouble(String str, double def)
	{
		try
		{
			return Double.parseDouble(str);
		} catch(NumberFormatException e){}
		return def;
	}
	
	///////////////////////////////////////////////////
	///////////////TELEPORT STUFF//////////////////////
	///////////////////////////////////////////////////
	
	public static void teleportEntityToSafety(Entity ent)
	{
		if((ent.worldObj.provider) instanceof TardisWorldProvider)
		{
			if(ent instanceof EntityPlayer)
			{
				CoreTileEntity core = getTardisCore(ent.worldObj);
				if(core != null)
					core.enterTardis((EntityPlayer) ent,true);
				else
					TardisTeleportHelper.teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
			}
			else
				TardisTeleportHelper.teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
		}
	}
	
	public static boolean sameItem(ItemStack a, ItemStack b)
	{
		if(a == null ^ b == null)
			return false;
		if(a.getItem() != null)
			return a.getItem().equals(b.getItem()) && a.getItemDamage() == b.getItemDamage();
		return false;
	}
	
	public static ItemStack transferItemStack(ItemStack is, IInventory dest)
	{
		int size = dest.getSizeInventory();
		ItemStack remaining = is.copy();
		for(int i = 0;i<size&&remaining.stackSize>0;i++)
		{
			if(dest.isItemValidForSlot(i, remaining))
			{
				ItemStack inSlot = dest.getStackInSlot(i);
				if(inSlot == null)
				{
					dest.setInventorySlotContents(i, remaining);
					return null;
				}
				else if(sameItem(remaining,inSlot))
				{
					int am = Math.min(remaining.stackSize, inSlot.getMaxStackSize()-inSlot.stackSize);
					inSlot.stackSize+=am;
					remaining.stackSize -= am;
				}
			}
		}
		if(remaining.stackSize > 0)
			return remaining;
		return null;
	}
	
	public static void giveItemStack(EntityPlayer pl, ItemStack is)
	{
		EntityItem ie = new EntityItem(pl.worldObj,pl.posX,pl.posY,pl.posZ,is);
		ie.delayBeforeCanPickup = 0;
		pl.worldObj.spawnEntityInWorld(ie);
	}
	
	public static void generateTardisInterior(int dimID, String ownerName, TardisTileEntity exterior)
	{
		World tardisWorld = Helper.getWorldServer(dimID);
		try
		{
			loadSchema("tardisConsoleMain",tardisWorld,tardisCoreX,tardisCoreY-10,tardisCoreZ,0);
		}
		catch(Exception e)
		{
			TardisOutput.print("TH", "Generating tardis error: " + e.getMessage());
			e.printStackTrace();
		}
		if(tardisWorld.getBlock(tardisCoreX, tardisCoreY, tardisCoreZ) != TardisMod.tardisCoreBlock)
		{
			tardisWorld.setBlock(tardisCoreX, tardisCoreY, tardisCoreZ, TardisMod.tardisCoreBlock);
			tardisWorld.setBlock(tardisCoreX, tardisCoreY-2, tardisCoreZ, TardisMod.tardisConsoleBlock);
			tardisWorld.setBlock(tardisCoreX, tardisCoreY-5, tardisCoreZ, TardisMod.tardisEngineBlock);
		}
		CoreTileEntity te = getTardisCore(dimID);
		if(te != null)
		{
			te.setOwner(ownerName);
			if(exterior != null)
				te.setExterior(exterior.getWorldObj(), exterior.xCoord, exterior.yCoord, exterior.zCoord);
		}
	}
	
	public static int generateTardisInterior(String ownerName,TardisTileEntity exterior)
	{
		if(exterior.getWorldObj().isRemote)
			return 0;
		int dimID = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimID, TardisMod.providerID);
		TardisMod.dimReg.addDimension(dimID);
		TardisDimensionRegistry.save();
		generateTardisInterior(dimID,ownerName,exterior);
		return dimID;
	}
	
	public static int generateTardisInterior(EntityPlayer player,TardisTileEntity exterior)
	{
		int dimID = generateTardisInterior(player.getCommandSenderName(),exterior);
		CoreTileEntity te = getTardisCore(dimID);
		if(te != null)
			te.enterTardis(player, true);
		return dimID;
	}
	
	public static void summonNewTardis(EntityPlayer player)
	{
		if(TardisMod.plReg.hasTardis(player.getCommandSenderName()))
			return;
		
		TardisTileEntity te = summonTardis(player);
		if(te != null)
		{
			int dimID = generateTardisInterior(player.getCommandSenderName(),te);
			te.linkToDimension(dimID);
			ConsoleTileEntity con = getTardisConsole(dimID);
			if(con != null)
				con.setControls(te, true);
		}
	}
	
	public static void summonOldTardis(EntityPlayer player)
	{
		Integer dim = TardisMod.plReg.getDimension(player);
		if(dim == null)
			return;
		
		TardisTileEntity te = summonTardis(player);
		if(te != null)
		{
			te.linkToDimension(dim);
			ConsoleTileEntity con = getTardisConsole(dim);
			if(con != null)
				con.setControls(te, true);
		}
		else
			TardisOutput.print("Helper", "No exterior :(");
	}
	
	private static TardisTileEntity summonTardis(EntityPlayer player)
	{
		World w = player.worldObj;
		if(w.isRemote)
			return null;
		
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		int[] validSpotRanges = {0, -1, 1, -2, 2, -3, 3};
		
		SimpleCoordStore place = null;
		for(int yOf = 0;yOf<7;yOf++)
		{
			if(place != null)
				break;
			
			int yO = yOf > 3?3-yOf:yOf;
			for(int xO : validSpotRanges)
			{
				if(place != null)
					break;
				
				for(int zO : validSpotRanges)
				{
					if(y > 1 && y < 253)
					{
						if(w.isAirBlock(x+xO, y+yO, z+zO) && w.isAirBlock(x+xO, y+yO+1, z+zO))
						{
							place = new SimpleCoordStore(w,x+xO,y+yO,z+zO);
							break;
						}
					}
				}
			}
		}
		
		if(place != null)
		{
			w.setBlock(place.x, place.y, place.z, TardisMod.tardisBlock, 0, 3);
			w.setBlock(place.x, place.y+1,place.z, TardisMod.tardisTopBlock, 0, 3);
			TileEntity te = w.getTileEntity(place.x,place.y,place.z);
			if(te instanceof TardisTileEntity)
				return (TardisTileEntity)te;
		}
		return null;
	}
	
	public static boolean isBlockRemovable(Block blockID)
	{
		if(blockID == TardisMod.tardisCoreBlock)
			return false;
		else if(blockID == TardisMod.tardisConsoleBlock)
			return false;
		else if(blockID == TardisMod.tardisEngineBlock);
		return true;
	}
	
	public static void loadSchema(File schemaFile, World w, int x, int y, int z, int facing)
	{
		PartBlueprint pb = new PartBlueprint(schemaFile);
		pb.reconstitute(w, x, y, z, facing);
	}

	public static void loadSchema(String name, World w, int x, int y, int z, int facing)
	{
		File schemaFile = TardisMod.configHandler.getSchemaFile(name);
		loadSchema(schemaFile, w, x, y, z, facing);
	}
	
	public static void loadSchemaDiff(String fromName, String toName, World worldObj, int xCoord, int yCoord, int zCoord, int facing)
	{
		if(fromName.equals(toName))
			return;
		
		long mstime = System.currentTimeMillis();
		File schemaDiff = TardisMod.configHandler.getSchemaFile(toName+"."+fromName+".diff");
		PartBlueprint diff = null;
		if(schemaDiff.exists())
		{
			TardisOutput.print("TH", "Loading diff from file");
			diff = new PartBlueprint(schemaDiff);
		}
		else
		{
			File fromFile	= TardisMod.configHandler.getSchemaFile(fromName);
			File toFile		= TardisMod.configHandler.getSchemaFile(toName);
			PartBlueprint fromPB	= new PartBlueprint(fromFile);
			PartBlueprint toPB	= new PartBlueprint(toFile);
			try
			{
				diff = new PartBlueprint(toPB,fromPB);
				diff.saveTo(schemaDiff);
			}
			catch (UnmatchingSchemaException e)
			{
				TardisOutput.print("TH", e.getMessage());
				e.printStackTrace();
			}
		}
		
		if(diff != null)
		{
			diff.reconstitute(worldObj, xCoord, yCoord, zCoord, facing);
		}
		TardisOutput.print("TH", "TimeTaken (ms):"+(System.currentTimeMillis() - mstime));
	}

	public static boolean isServer()
	{
		return !FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
	}
	
	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z)
	{
		spawnParticle(type,dim,x,y,z,1,false);
	}
	
	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, int c)
	{
		spawnParticle(type,dim,x,y,z,c,false);
	}
	
	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, boolean b)
	{
		spawnParticle(type,dim,x,y,z,1,b);
	}
	
	public static void spawnParticle(ParticleType type, int dim, double x, double y, double z, int count, boolean rand)
	{
		if(!Helper.isServer())
			return;
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("dim",dim);
		data.setDouble("x", x);
		data.setDouble("y", y);
		data.setDouble("z", z);
		data.setInteger("c", count);
		data.setBoolean("r", rand);
		data.setInteger("type",type.ordinal());
		ParticlePacket packet = new ParticlePacket(Unpooled.buffer(),data);
		TardisMod.networkChannel.sendToDimension(packet, dim);
	}
	
	public static void playSound(TileEntity te, String sound, float vol)
	{
		playSound(getWorldID(te.getWorldObj()), te.xCoord, te.yCoord, te.zCoord, sound, vol);
	}
	
	public static void playSound(TileEntity te, String sound, float vol, float speed)
	{
		playSound(getWorldID(te.getWorldObj()), te.xCoord, te.yCoord, te.zCoord, sound, vol,speed);
	}
	
	public static void playSound(World w, int x, int y, int z, String sound, float vol)
	{
		int dim = getWorldID(w);
		playSound(dim,x,y,z,sound,vol);
	}
	
	public static void playSound(World w, int x, int y, int z, String sound, float vol,float speed)
	{
		int dim = getWorldID(w);
		playSound(dim,x,y,z,sound,vol,speed);
	}
	
	public static void playSound(int dim, int x, int y, int z, String sound, float vol)
	{
		playSound(dim,x,y,z,sound,vol,1);
	}
	
	public static void playSound(int dim, int x, int y, int z, String sound, float vol, float speed)
	{
		if(!Helper.isServer())
			return;
		NBTTagCompound data = new NBTTagCompound();
		World w = Helper.getWorld(dim);
		//No point playing a sound to a world with no entities in
		if(w != null)
			if(w.playerEntities != null && w.playerEntities.size() == 0)
				return;
		if(!sound.contains(":"))
			sound = "tardismod:" + sound;
		data.setString("sound", sound);
		data.setInteger("world", dim);
		data.setInteger("x",x);
		data.setInteger("y",y);
		data.setInteger("z",z);
		data.setFloat("vol",vol);
		if(speed != 1)
			data.setFloat("spe", speed);
		SoundPacket packet = new SoundPacket(Unpooled.buffer(),data);
		TardisMod.networkChannel.sendToDimension(packet, dim);
		//MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, dim);
	}
	
	public static void activateControl(TileEntity te, EntityPlayer player, int control)
	{
		if(Helper.isServer())
			return;
		NBTTagCompound data = new NBTTagCompound();
		data.setInteger("cID", control);
		data.setString("pl", getUsername(player));
		data.setInteger("dim", getWorldID(te));
		data.setInteger("x", te.xCoord);
		data.setInteger("y", te.yCoord);
		data.setInteger("z", te.zCoord);
		ControlPacket p = new ControlPacket(data);
		TardisMod.networkChannel.sendToServer(p);
	}
	
	public static String getUsername(EntityPlayer player)
	{
		return player.getCommandSenderName();
	}
	
	public static ServerConfigurationManager getConfMan()
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(serv != null)
			return serv.getConfigurationManager();
		return null;
	}

	public static World getWorld(int dimensionID)
	{
		if(!Helper.isServer())
			return TardisMod.proxy.getWorld(dimensionID);
		else
			return getWorldServer(dimensionID);
	}

	public static WorldServer getWorldServer(int d)
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(serv != null)
			return serv.worldServerForDimension(d);
		return null;
	}
	
	public static int getWorldID(World w)
	{
		return w.provider.dimensionId;
	}
	
	public static int getWorldID(TileEntity te)
	{
		World w = te.getWorldObj();
		if(w != null)
			return getWorldID(w);
		return 0;
	}
	
	public static int getWorldID(Entity ent)
	{
		World w = ent.worldObj;
		if(w != null)
			return getWorldID(w);
		return 0;
	}

	public static EntityPlayerMP getPlayer(String username)
	{
		List playerEnts = getConfMan().playerEntityList;
		for(Object o : playerEnts)
		{
			if(o instanceof EntityPlayerMP)
			{
				if(((EntityPlayerMP)o).getCommandSenderName().equals(username))
					return (EntityPlayerMP)o;
			}
		}
		return null;
	}

	public static CoreTileEntity getTardisCore(int dimensionID)
	{
		if(dimensionID == 0)
			return null;
		World tardisWorld = getWorld(dimensionID);
		if(tardisWorld != null)
			return getTardisCore(tardisWorld);
		else
			TardisOutput.print("TH","No world passed",TardisOutput.Priority.DEBUG);
		return null;
	}
	
	public static CoreTileEntity getTardisCore(IBlockAccess tardisWorld)
	{
		if(tardisWorld != null)
		{
			TileEntity te = tardisWorld.getTileEntity(tardisCoreX, tardisCoreY, tardisCoreZ);
			if(te instanceof CoreTileEntity)
			{
				return (CoreTileEntity)te;
			}
		}
		else
		{
			TardisOutput.print("TH","No world passed",TardisOutput.Priority.DEBUG);
		}
		return null;
	}
	
	public static CoreTileEntity getTardisCore(TileEntity te)
	{
		if(te != null)
		{
			World w = te.getWorldObj();
			if(w != null)
				return getTardisCore(w);
		}
		return null;
	}
	
	public static EngineTileEntity getTardisEngine(int dim)
	{
		return getTardisEngine(Helper.getWorld(dim));
	}
	
	public static EngineTileEntity getTardisEngine(IBlockAccess w)
	{
		CoreTileEntity core = getTardisCore(w);
		if(core != null)
			return core.getEngine();
		return null;
	}
	
	public static ConsoleTileEntity getTardisConsole(int dimID)
	{
		World w = getWorldServer(dimID);
		if(w != null)
			return getTardisConsole(w);
		return null;
	}
	
	public static ConsoleTileEntity getTardisConsole(IBlockAccess tardisWorld)
	{
		CoreTileEntity core = getTardisCore(tardisWorld);
		if(core != null)
			return core.getConsole();
		return null;
	}
	
	public static NBTTagCompound readNBT(InputStream in)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(in);
			return nbt;
		}
		catch(ZipException e)
		{
			try
			{
				if(in instanceof DataInputStream)
				{
					NBTTagCompound nbt = CompressedStreamTools.read((DataInputStream)in);
					return nbt;
				}
				return null;
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		catch(IOException e)
		{
			TardisOutput.print("TH", "Error writing NBT: "+ e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeNBT(NBTTagCompound nbt, OutputStream out)
	{
		try
		{
			CompressedStreamTools.writeCompressed(nbt, out);
		}
		catch(IOException e)
		{
			TardisOutput.print("TH", "Error writing NBT: "+ e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
	}

	public static String getDimensionName(int worldID)
	{
		World w = getWorld(worldID);
		return getDimensionName(w);
	}
	
	public static String getDimensionName(World w)
	{
		if(w != null)
		{
			return w.provider.getDimensionName();
		}
		return "Unknown";
	}

	public static void sendString(EntityPlayer pl, String source,String s)
	{
		sendString(pl, new ChatComponentText("["+source+"] " + s));
	}

	public static void sendString(EntityPlayer pl, ChatComponentText message)
	{
		pl.addChatMessage(message);
	}

	public static boolean isTardisWorld(World worldObj)
	{
		if(worldObj != null)
		{
			return worldObj.provider instanceof TardisWorldProvider;
		}
		return false;
	}

	public static void sendString(EntityPlayer pl, String string)
	{
		sendString(pl, new ChatComponentText(string));
	}
}
