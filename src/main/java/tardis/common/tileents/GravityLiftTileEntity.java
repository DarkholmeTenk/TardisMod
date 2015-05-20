package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;

public class GravityLiftTileEntity extends AbstractTileEntity implements IScrewable
{
	private static ConfigFile config = null;
	private static int maxDistance = 64;
	private static int scanCeilingInterval = 20;
	private static int scanPlayerInterval = 2;
	private static double movePerTick = 0.25;
	private static double bevel = 0.28;

	private int distance = -1;
	private HashMap<EntityPlayerMP,Boolean> goingUp = new HashMap<EntityPlayerMP,Boolean>();

	public static void refreshConfigs()
	{
		if(config == null)
		{
			config = TardisMod.configHandler.registerConfigNeeder("GravityLift");
			maxDistance = config.getInt("max distance", 64);
			scanCeilingInterval = config.getInt("interval for ceiling scan", 20);
			scanPlayerInterval = config.getInt("interval for player scan", 2);
			movePerTick = config.getDouble("move per tick", 0.25);
		}
	}

	private void scanForCeiling()
	{
		for(distance = 3; distance<(maxDistance+3);distance++)
		{
			if(!softBlock(worldObj,xCoord,yCoord+distance,zCoord))
			{
				if(worldObj.getBlock(xCoord, yCoord+distance, zCoord)==TardisMod.forcefield)
					distance +=3;
				break;
			}
		}
		distance -= 3;
	}

	private void scanForPlayers()
	{
		List<Object> baseList = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox((xCoord-1)+bevel, yCoord+1, (zCoord-1)+bevel, (xCoord+2)-bevel, yCoord+1+distance, (zCoord+2)-bevel));
		for(Object o : baseList)
		{
			if(!(o instanceof EntityPlayerMP))
				continue;
			EntityPlayerMP pl = (EntityPlayerMP)o;
			if(pl.capabilities.isFlying)
				continue;
			if(goingUp.keySet().contains(pl))
				continue;
			if((pl.posY > (yCoord + 1.5)) && (pl.posY < (yCoord + distance)))
			{
				boolean up = pl.posY < (yCoord + ((distance + 2) / 2));
				goingUp.put(pl, up);
			}
		}
	}

	//Remove player entities who are no longer valid
	private void validatePlayers()
	{
		Iterator<EntityPlayerMP> iter = goingUp.keySet().iterator();
		while(iter.hasNext())
		{
			EntityPlayerMP pl = iter.next();
			if(pl.isDead)
				iter.remove();
			else if(pl.worldObj != worldObj)
				iter.remove();
			else if(!worldObj.playerEntities.contains(pl))
				iter.remove();
			else if(pl.posY < (yCoord + 1.2))
				iter.remove();
			else if(pl.posY > (yCoord + 0.5 + distance))
				iter.remove();
			else if((pl.posX < ((xCoord - 1) + bevel)) || (pl.posX > ((xCoord + 2) - bevel)))
				iter.remove();
			else if((pl.posZ < ((zCoord - 1) + bevel)) || (pl.posZ > ((zCoord + 2) - bevel)))
				iter.remove();
			else if(pl.capabilities.isFlying)
				iter.remove();
		}
	}

	private void movePlayer(EntityPlayerMP pl, boolean up)
	{
		double dir = 0;
		if(up)
			dir = Math.min(movePerTick, (yCoord+distance+1.25) - pl.posY);
		else
			dir = Math.max(-movePerTick, (yCoord + 1.5) - pl.posY);
		pl.fallDistance = 0;
		pl.motionY = dir;
		pl.velocityChanged = true;
		pl.setPosition(pl.posX, pl.posY+dir, pl.posZ);
	}

	private void movePlayers()
	{
		Iterator<EntityPlayerMP> iter = goingUp.keySet().iterator();
		while(iter.hasNext())
		{
			EntityPlayerMP pl = iter.next();
			Boolean up = goingUp.get(pl);
			if(up)
			{

				if(pl.posY > (yCoord + 1.6 + distance))
				{
					iter.remove();
					continue;
				}
				else
				{
					//TardisOutput.print("GLTE", "moving up " + Helper.getUsername(pl));
					movePlayer(pl,true);
				}
			}
			else
			{
				//TardisOutput.print("GLTE", "moving down " + Helper.getUsername(pl));
				if(pl.posY < (yCoord + 1.6))
				{
					iter.remove();
					continue;
				}
				else
				{
					movePlayer(pl,false);
				}
			}
		}
	}

	@Override
	public void init()
	{
		if(config == null)
			refreshConfigs();
		scanForCeiling();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if((tt % scanCeilingInterval) == 0)
			scanForCeiling();
		if((tt % scanPlayerInterval) == 0)
			scanForPlayers();
		validatePlayers();
		movePlayers();
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
	}

	@Override
	public boolean screw(ScrewdriverMode mode, EntityPlayer player)
	{
		if(mode.equals(ScrewdriverMode.Dismantle))
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			if(meta == 1)
			{
				WorldHelper.giveItemStack(player, new ItemStack(TardisMod.gravityLift,1,1));
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				return true;
			}
		}
		return false;
	}

}
