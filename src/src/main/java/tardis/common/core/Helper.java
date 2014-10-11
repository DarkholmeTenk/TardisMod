package tardis.common.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import tardis.TardisMod;
import tardis.common.core.schema.TardisPartBlueprint;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class Helper
{
	public static final int tardisCoreX = 0;
	public static final int tardisCoreY = 30;
	public static final int tardisCoreZ = 0;
	
	private static Boolean isServer = null;
	
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
	
	public static boolean isServer()
	{
		if(isServer == null)
			return (isServer=FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER));
		return isServer;
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
					core.enterTardis((EntityPlayer) ent);
				else
					teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
			}
			else
				teleportEntity(ent, 0, ent.posX, ent.posY, ent.posZ);
		}
	}
	
	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z)
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(ent instanceof EntityPlayerMP)
		{
			World nW = Helper.getWorld(worldID);
			if(nW.provider instanceof TardisWorldProvider && FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
			{
				Packet dP = TardisDimensionRegistry.getPacket();
				MinecraftServer.getServer().getConfigurationManager().sendToAllNear(ent.posX, ent.posY, ent.posZ, 100, ent.worldObj.provider.dimensionId, dP);
			}
			if(ent.worldObj.provider.dimensionId != worldID)
				serv.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) ent, worldID, TardisMod.teleporter);
			((EntityPlayerMP) ent).fallDistance = 0;
			((EntityPlayerMP) ent).setPositionAndUpdate(x, y, z);
		}
	}
	
	public static void teleportEntity(Entity ent, int worldID)
	{
		teleportEntity(ent,worldID,ent.posX,ent.posY,ent.posZ);
	}
	
	public static int toInt(String str, int def)
	{
		try
		{
			return Integer.parseInt(str);
		} catch(NumberFormatException e){}
		return def;
	}
	
	public static World getWorld(int dimensionID)
	{
		return TardisMod.proxy.getWorld(dimensionID);
	}
	
	public static void giveItemStack(EntityPlayerMP pl, ItemStack is)
	{
		InventoryPlayer inv = pl.inventory;
		if(!inv.addItemStackToInventory(is))
		{
			EntityItem ie = new EntityItem(pl.worldObj,pl.posX,pl.posY,pl.posZ,is);
			pl.worldObj.spawnEntityInWorld(ie);
		}
		else
		{
			inv.onInventoryChanged();
		}
	}
	
	public static void loadSchema(String name,World w, int x, int y, int z, int facing)
	{
		TardisPartBlueprint bp = new TardisPartBlueprint(TardisMod.configHandler.getSchemaFile(name));
		bp.reconstitute(w, x, y, z, facing);
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
	
	public static int generateTardisInterior(EntityPlayer player,TardisTileEntity exterior)
	{
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
			te.enterTardis(player);
			te.setExterior(exterior.worldObj, exterior.xCoord, exterior.yCoord, exterior.zCoord);
		}
		return dimID;
	}
}
