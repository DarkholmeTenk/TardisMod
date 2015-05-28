package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
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

	private static final double amountAboveToStop = 1.1;
	private static final double amountAboveToStart = 1.95;
	private static final double amountBelowToStart = 0.5;

	private List<Integer> distances = new ArrayList<Integer>();
	private int maxDist = 0;
	private HashMap<EntityPlayer,Boolean> goingUp = new HashMap<EntityPlayer,Boolean>();

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

	private int getBlock(double y, boolean start)
	{
		double p = yCoord;
		for(int i = 0; i < distances.size(); i++)
		{
			int d = distances.get(i);
			if((y > (p + amountAboveToStart)) && (y < (d + (start ? amountBelowToStart : amountAboveToStop))))
				return i;
			p = d;
		}
		return -1;
	}

	private double getStopPointAbove(double y)
	{
		int b = getBlock(y,false);
		if(b == -1)
			return -1;
		return distances.get(b) + amountAboveToStop;
	}

	private double getStopPointBelow(double y)
	{
		int b = getBlock(y,false);
		if(b == -1)
			return -1;
		if(b == 0)
			return yCoord + amountAboveToStop;
		return distances.get(b-1) + amountAboveToStop;
	}

	private void scanForCeiling()
	{
		maxDist = -1;
		distances.clear();
		for(int distance = 2; distance<(maxDistance+3);distance++)
		{
			if(!softBlock(worldObj,xCoord,yCoord+distance,zCoord))
			{
				if((yCoord + distance) > maxDist)
					maxDist = distance;
				if(worldObj.getBlock(xCoord, yCoord+distance, zCoord)==TardisMod.forcefield)
				{
					distances.add(yCoord + distance);
					distance += 3;
				}
				else
					break;
			}
		}
	}

	private double posY(EntityPlayer player)
	{
		if(ServerHelper.isClient()) return (player.posY - player.height) + player.eyeHeight;
		return player.posY;
	}

	private void scanForPlayers()
	{
		List<Object> baseList = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox((xCoord-1)+bevel, yCoord+1, (zCoord-1)+bevel, (xCoord+2)-bevel, yCoord+maxDist, (zCoord+2)-bevel));
		for(Object o : baseList)
		{
			if(!(o instanceof EntityPlayer))
				continue;
			EntityPlayer pl = (EntityPlayer)o;
			if(pl.capabilities.isFlying)
				continue;
			if(goingUp.keySet().contains(pl))
				continue;
			double prevDistance = yCoord;
			for(Integer d : distances)
			{
				if((posY(pl) > (prevDistance + amountAboveToStart)) && (posY(pl) < (d - amountBelowToStart)))
				{
					if(posY(pl) < (((d + prevDistance + amountAboveToStart) - amountBelowToStart) / 2))
						goingUp.put(pl, true);
					else
						goingUp.put(pl, false);
				}
				prevDistance = d;
			}
		}
	}

	//Remove player entities who are no longer valid
	private void validatePlayers()
	{
		Iterator<EntityPlayer> iter = goingUp.keySet().iterator();
		while(iter.hasNext())
		{
			EntityPlayer pl = iter.next();
			if(pl.isDead)
				iter.remove();
			else if(pl.worldObj != worldObj)
				iter.remove();
			else if(!worldObj.playerEntities.contains(pl))
				iter.remove();
			else if((pl.posX < ((xCoord - 1) + bevel)) || (pl.posX > ((xCoord + 2) - bevel)))
				iter.remove();
			else if((pl.posZ < ((zCoord - 1) + bevel)) || (pl.posZ > ((zCoord + 2) - bevel)))
				iter.remove();
			else if(pl.capabilities.isFlying)
				iter.remove();
			else if(getBlock(posY(pl),false) == -1)
				iter.remove();
		}
	}

	private void movePlayer(EntityPlayer pl, boolean up)
	{
		double dir = 0;
		if(up)
			dir = Math.min(movePerTick, getStopPointAbove(posY(pl)) - posY(pl));
		else
			dir = Math.max(-movePerTick, getStopPointBelow(posY(pl)) - posY(pl));
		pl.fallDistance = 0;
		pl.motionY = dir;
		pl.velocityChanged = true;
		pl.setPosition(pl.posX, pl.posY+dir, pl.posZ);
	}

	private void movePlayers()
	{
		Iterator<EntityPlayer> iter = goingUp.keySet().iterator();
		while(iter.hasNext())
		{
			EntityPlayer pl = iter.next();
			Boolean up = goingUp.get(pl);
			if(up)
			{

				if(posY(pl) > getStopPointAbove(posY(pl)))
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
				if(posY(pl) < (yCoord + 1.6))
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
