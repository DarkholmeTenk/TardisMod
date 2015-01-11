package tardis.common.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOutput;

public class TardisTeleportHelper
{
	public static void transferEntityToDimension(Entity ent, int newDimension, double newX, double newY, double newZ)
	{
		if(!Helper.isServer())
			return;
		ServerConfigurationManager conf = Helper.getConfMan();
		int oldDimension = Helper.getWorldID(ent);
		WorldServer dest = Helper.getWorldServer(newDimension);
		WorldServer source = Helper.getWorldServer(oldDimension);
		if(ent instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)ent;
			conf.transferPlayerToDimension(pl, newDimension, TardisMod.teleporter);
			if(source.provider instanceof WorldProviderEnd)
				ent = Helper.getConfMan().respawnPlayer(pl, newDimension, true);
			pl.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(pl.experience, pl.experienceTotal, pl.experienceLevel));
			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(ent), dest);
			if(entity != null)
			{
				entity.copyDataFrom(ent, true);
				dest.spawnEntityInWorld(entity);
				ent.isDead = true;
	            source.resetUpdateEntityTick();
	            dest.resetUpdateEntityTick();
			}
		}
		else
		{
			ent.travelToDimension(newDimension);
			//conf.transferEntityToWorld(ent, newDimension, source, dest, TardisMod.teleporter);
		}
	}

	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z)
	{
		teleportEntity(ent,worldID,x,y,z,0);
	}

	public static void teleportEntity(Entity ent, int worldID, double x, double y, double z, double rot)
	{
		TardisOutput.print("TTH", "Teleport request: " + worldID +" > " + x+","+y+","+z);
		MinecraftServer serv = MinecraftServer.getServer();
		if(Helper.isServer() && serv != null && ent instanceof EntityLivingBase)
		{
			WorldServer nW = Helper.getWorldServer(worldID);
			if(nW.provider instanceof TardisWorldProvider && Helper.isServer())
			{
				Packet dP = TardisDimensionRegistry.getPacket();
				Helper.getConfMan().sendToAllNear(ent.posX, ent.posY, ent.posZ, 100, Helper.getWorldID(ent), dP);
			}
			
			if(Helper.getWorldID(ent.worldObj) != worldID)
			{
				transferEntityToDimension(ent,worldID,x,y,z);
			}
			((EntityLivingBase) ent).fallDistance = 0;
			((EntityLivingBase) ent).setPositionAndRotation(x, y, z, (float) rot, 0F);
			((EntityLivingBase) ent).setPositionAndUpdate(x, y, z);
		}
	}

	public static void teleportEntity(Entity ent, int worldID)
	{
		teleportEntity(ent,worldID,ent.posX,ent.posY,ent.posZ);
	}
}
