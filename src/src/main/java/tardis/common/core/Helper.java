package tardis.common.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tardis.TardisMod;
import tardis.common.core.schema.TardisPartBlueprint;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
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
				TardisCoreTileEntity core = getTardisCore(ent.worldObj);
				if(core != null)
					core.enterTardis((EntityPlayer) ent,true);
				else
					teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
			}
			else
				teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
		}
	}
	
	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z)
	{
		teleportEntity(ent,worldID,x,y,z,0);
	}
	
	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z, double rot)
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(Helper.isServer() && serv != null && ent instanceof EntityPlayer)
		{
			WorldServer nW = Helper.getWorldServer(worldID);
//			WorldServer oW = Helper.getWorldServer(ent.worldObj.provider.dimensionId);
			if(nW.provider instanceof TardisWorldProvider && FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
			{
				Packet dP = TardisDimensionRegistry.getPacket();
				MinecraftServer.getServer().getConfigurationManager().sendToAllNear(ent.posX, ent.posY, ent.posZ, 100, ent.worldObj.provider.dimensionId, dP);
			}
			
			if(ent.worldObj.provider.dimensionId != worldID)
				serv.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) ent, worldID, TardisMod.teleporter);
			((EntityPlayer) ent).fallDistance = 0;
			((EntityPlayer) ent).setPositionAndRotation(x, y, z, (float) rot, 0F);
			((EntityPlayer) ent).setPositionAndUpdate(x, y, z);
		}
	}
	
	public static void teleportEntity(Entity ent, int worldID)
	{
		teleportEntity(ent,worldID,ent.posX,ent.posY,ent.posZ);
	}
	
	public static Packet250CustomPayload nbtPacket(String channel,NBTTagCompound nbt)
	{
		Packet250CustomPayload p = new Packet250CustomPayload();
		p.channel = channel;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(bos);
			NBTTagCompound.writeNamedTag(nbt, stream);
			p.data = bos.toByteArray();
			p.length = p.data.length;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return p;
	}
	
	public static void giveItemStack(EntityPlayer pl, ItemStack is)
	{
		/*InventoryPlayer inv = pl.inventory;
		if(!inv.addItemStackToInventory(is))
		{
			EntityItem ie = new EntityItem(pl.worldObj,pl.posX,pl.posY,pl.posZ,is);
			pl.worldObj.spawnEntityInWorld(ie);
		}
		else
		{
			inv.onInventoryChanged();
		}*/
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
		if(tardisWorld.getBlockId(tardisCoreX, tardisCoreY, tardisCoreZ) != TardisMod.tardisCoreBlock.blockID)
			tardisWorld.setBlock(tardisCoreX, tardisCoreY, tardisCoreZ, TardisMod.tardisCoreBlock.blockID);
		TardisCoreTileEntity te = getTardisCore(dimID);
		if(te != null)
		{
			te.setOwner(ownerName);
			if(exterior != null)
				te.setExterior(exterior.worldObj, exterior.xCoord, exterior.yCoord, exterior.zCoord);
		}
	}
	
	public static int generateTardisInterior(String ownerName,TardisTileEntity exterior)
	{
		if(exterior.worldObj.isRemote)
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
		int dimID = generateTardisInterior(player.username,exterior);
		TardisCoreTileEntity te = getTardisCore(dimID);
		if(te != null)
			te.enterTardis(player, true);
		return dimID;
		/*
		if(player.worldObj.isRemote)
			return 0;
		int dimID = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimID, TardisMod.providerID);
		TardisMod.dimReg.addDimension(dimID);
		TardisDimensionRegistry.save();
		World tardisWorld = Helper.getWorld(dimID);
		try
		{
			loadSchema("tardisConsoleMain",tardisWorld,tardisCoreX,tardisCoreY-10,tardisCoreZ,0);
		}
		catch(Exception e)
		{
			TardisOutput.print("TH", "Generating tardis error: " + e.getMessage());
			e.printStackTrace();
		}
		tardisWorld.setBlock(tardisCoreX, tardisCoreY, tardisCoreZ, TardisMod.tardisCoreBlock.blockID);
		TardisCoreTileEntity te = getTardisCore(dimID);
		if(te != null)
		{
			te.setOwner(player.username);
			te.enterTardis(player,true);
			te.setExterior(exterior.worldObj, exterior.xCoord, exterior.yCoord, exterior.zCoord);
		}
		return dimID;
		*/
	}
	
	public static void summonNewTardis(EntityPlayer player)
	{
		if(TardisMod.plReg.hasTardis(player.username))
			return;
		
		TardisTileEntity te = summonTardis(player);
		if(te != null)
		{
			int dimID = generateTardisInterior(player.username,te);
			te.linkToDimension(dimID);
			TardisConsoleTileEntity con = getTardisConsole(dimID);
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
			TardisConsoleTileEntity con = getTardisConsole(dim);
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
			w.setBlock(place.x, place.y, place.z, TardisMod.tardisBlock.blockID, 0, 3);
			w.setBlock(place.x, place.y+1,place.z, TardisMod.tardisTopBlock.blockID, 0, 3);
			TileEntity te = w.getBlockTileEntity(place.x,place.y,place.z);
			if(te instanceof TardisTileEntity)
				return (TardisTileEntity)te;
		}
		return null;
	}
	
	public static boolean isBlockRemovable(int blockID)
	{
		if(blockID == TardisMod.tardisCoreBlock.blockID)
			return false;
		else if(blockID == TardisMod.tardisConsoleBlock.blockID)
			return false;
		return true;
	}

	public static void loadSchema(String name,World w, int x, int y, int z, int facing)
	{
		TardisPartBlueprint bp = new TardisPartBlueprint(TardisMod.configHandler.getSchemaFile(name));
		bp.reconstitute(w, x, y, z, facing);
	}
	
	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER);
	}
	
	public static void playSound(World w, int x, int y, int z, String sound, float vol)
	{
		int dim = getWorldID(w);
		playSound(dim,x,y,z,sound,vol);
	}
	
	public static void playSound(int dim, int x, int y, int z, String sound, float vol)
	{
		if(!Helper.isServer())
			return;
		NBTTagCompound data = new NBTTagCompound();
		data.setString("sound", sound);
		data.setInteger("world", dim);
		data.setInteger("x",x);
		data.setInteger("y",y);
		data.setInteger("z",z);
		data.setFloat("vol",vol);
		Packet250CustomPayload packet = nbtPacket("TardisSn",data);
		MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(packet, dim);
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
		return MinecraftServer.getServer().worldServerForDimension(d);
	}
	
	public static int getWorldID(World w)
	{
		return w.provider.dimensionId;
	}
	
	public static Block getBlock(World w, int x, int y, int z)
	{
		int blockID = w.getBlockId(x, y, z);
		if(blockID != 0)
		{
			try
			{
				return Block.blocksList[blockID];
			}
			catch(IndexOutOfBoundsException e)
			{
			}
		}
		return null;
	}

	public static EntityPlayerMP getPlayer(String username)
	{
		return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);
	}

	public static TardisCoreTileEntity getTardisCore(int dimensionID)
	{
		World tardisWorld = Helper.getWorld(dimensionID);
		if(tardisWorld != null)
			return getTardisCore(tardisWorld);
		else
			TardisOutput.print("TH","No world passed",TardisOutput.Priority.DEBUG);
		return null;
	}
	
	public static TardisCoreTileEntity getTardisCore(World tardisWorld)
	{
		if(tardisWorld != null)
		{
			TileEntity te = tardisWorld.getBlockTileEntity(tardisCoreX, tardisCoreY, tardisCoreZ);
			if(te != null && te instanceof TardisCoreTileEntity)
			{
				return (TardisCoreTileEntity)te;
			}
		}
		else
		{
			TardisOutput.print("TH","No world passed",TardisOutput.Priority.DEBUG);
		}
		return null;
	}
	
	public static TardisConsoleTileEntity getTardisConsole(int dimID)
	{
		World w = getWorldServer(dimID);
		if(w != null)
			return getTardisConsole(w);
		return null;
	}
	
	public static TardisConsoleTileEntity getTardisConsole(World tardisWorld)
	{
		TardisCoreTileEntity core = getTardisCore(tardisWorld);
		if(core != null)
			return core.getConsole();
		return null;
	}
}
