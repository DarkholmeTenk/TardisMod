package tardis.core;

import tardis.TardisMod;
import tardis.dimension.TardisWorldProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class Helper
{
	public static int clamp(int val, int min, int max)
	{
		return Math.min(max, Math.max(min,val));
	}
	
	public static void teleportEntityToSafety(Entity ent)
	{
		if((ent.worldObj.provider) instanceof TardisWorldProvider)
		{
			ent.setPosition(-10, 30, 0);
		}
	}
	
	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z)
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(ent instanceof EntityPlayerMP)
		{
			serv.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) ent, worldID, TardisMod.teleporter);
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
	
	public static void giveItemStack(EntityPlayerMP pl, ItemStack is)
	{
		InventoryPlayer inv = pl.inventory;
		if(!inv.addItemStackToInventory(is))
		{
			EntityItem ie = new EntityItem(pl.worldObj,pl.posX,pl.posY,pl.posZ,is);
			pl.worldObj.spawnEntityInWorld(ie);
		}
	}
}
