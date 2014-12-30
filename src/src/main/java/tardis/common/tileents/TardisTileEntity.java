package tardis.common.tileents;

import java.util.ArrayList;
import java.util.List;

import tardis.TardisMod;
import tardis.api.IChunkLoader;
import tardis.api.IWatching;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class TardisTileEntity extends AbstractTileEntity implements IChunkLoader, IWatching
{
	private int fadeTimer = 0;
	
	private boolean landed = false;
	private boolean landFast = true;
	private boolean takingOff = false;
	private boolean landing   = true;
	
	private boolean takingOffSoundPlayed = false;
	private boolean landingSoundPlayed = false;
	
	public String owner;
	public static String baseURL = null;
	
	Integer linkedDimension = null;
	
	public void updateEntity()
	{
		super.updateEntity();
		if(baseURL == null)
			baseURL = TardisMod.modConfig.getString("Skin URL", "http://skins.darkcraft.io/tardis/");
		if(Helper.isServer())
		{
			CoreTileEntity linkedCore = getCore();
			if(linkedCore != null && owner == null)
			{
				owner = linkedCore.getOwner();
				sendUpdate();
			}
		}
		if(linkedDimension != null && tt % 20 == 0)
		{
			CoreTileEntity core = Helper.getTardisCore(linkedDimension);
			if(core != null)
			{
				TardisTileEntity ext = core.getExterior();
				if(ext != null)
				{
					if(!equals(ext))
					{
						worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
						worldObj.setBlockToAir(xCoord, yCoord, zCoord);
						return;
					}
				}
			}
		}
		if(inFlight())
		{
			if(isLanding() && !landingSoundPlayed)
				playLandSound();
			else if(isTakingOff() && !takingOffSoundPlayed)
				playTakeoffSound();
			sendUpdate();
			if(++fadeTimer >( !landFast && isLanding() ? 22 * 20 : 5.5 * 20))
			{
				if(isLanding())
					landed = true;
				else if(isTakingOff())
				{
					worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
					worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				}
				landing = takingOff = false;
			}
		}
		else
		{
			if(!landed)
				land();
		}
	}
	
	public boolean isTakingOff()
	{
		return takingOff;
	}
	
	public boolean isLanding()
	{
		return landing;
	}
	
	public boolean inFlight()
	{
		return isTakingOff() || isLanding();
	}
	
	private void playTakeoffSound()
	{
		Helper.playSound(this, "takeoff", 1);
		takingOffSoundPlayed = true;
	}
	
	public void takeoff()
	{
		fadeTimer = 0;
		takingOff = true;
		playTakeoffSound();
		sendUpdate();
	}
	
	public void forceLand()
	{
		landing = false;
		landed = true;
	}
	
	private void playLandSound()
	{
		if(!landFast)
			Helper.playSound(this, "landing", 1);
		else
			Helper.playSound(this, "landingInt", 1);
		landingSoundPlayed = true;
	}
	
	public void land(boolean fast)
	{
		fadeTimer = 0;
		landing = true;
		landFast = fast;
		playLandSound();
		TardisOutput.print("TTE", "LANDING!!!! " + (fast ? "FAST " : "SLOW ") + (worldObj.isRemote?"REM":"SER") + ":" + (landed?"LAN":"UNL"));
		sendUpdate();
	}
	
	public void land()
	{
		land(false);
	}
	
	public void linkToDimension(int dimID)
	{
		linkedDimension = dimID;
		CoreTileEntity te = Helper.getTardisCore(dimID);
		if(te != null)
		{
			te.linkToExterior(this);
		}
	}
	
	public float getTransparency()
	{
		double multiplier = 1;
		if(!landed && !isLanding())
			return 0.0F;
		else if(isTakingOff() || isLanding())
		{
			if(isLanding())
				multiplier = (fadeTimer / (80 * 5.5));
			else if(isTakingOff())
				multiplier = 1 - (fadeTimer / (80 * 5.5));
			
			double remainder;
			double transVal;
			if(isLanding())
			{
				int value = landFast ? 40 : 80;
				remainder = ((fadeTimer - (value/2)) % value);
				transVal = multiplier * (Math.abs(1-((2*remainder)/value)));
			}
			else
			{
				remainder = (fadeTimer % 80);
				transVal = multiplier * (Math.abs(1-((2*remainder)/80)));
			}
			return (float) transVal;
		}
		else
		{
			return 1.0F;
		}
	}
	
	public void doorActivated(World world, int x, int y, int z, EntityPlayer player)
	{
		if(!Helper.isServer())
			return;
		if(!inFlight())
		{
			if(linkedDimension == null)
			{
				if(!TardisMod.plReg.hasTardis(player.getCommandSenderName()))
					linkedDimension = Helper.generateTardisInterior(player,this);
				else
					player.addChatMessage(new ChatComponentText("You already own a TARDIS"));
			}
			else
			{
				CoreTileEntity te = Helper.getTardisCore(linkedDimension);
				if(te != null)
				{
					te.linkToExterior(this);
					if(!te.changeLock(player,false))
						te.enterTardis(player,false);
				}
			}
		}
	}
	
	public CoreTileEntity getCore()
	{
		if(linkedDimension != null && linkedDimension != 0)
			return Helper.getTardisCore(linkedDimension);
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
	}

	@Override
	public void writeTransmittable(NBTTagCompound tag)
	{
		tag.setBoolean("takingOff",takingOff);
		tag.setBoolean("landing",landing);
		tag.setBoolean("landFast", landFast);
		tag.setBoolean("landed", landed);
		tag.setInteger("fadeTimer", fadeTimer);
		if(linkedDimension != null)
			tag.setInteger("linkedDimension", linkedDimension);
		if(owner != null)
			tag.setString("owner", owner);
	}

	@Override
	public void readTransmittable(NBTTagCompound tag)
	{
		takingOff = tag.getBoolean("takingOff");
		landing = tag.getBoolean("landing");
		landFast = tag.getBoolean("landFast");
		landed = tag.getBoolean("landed");
		fadeTimer = tag.getInteger("fadeTimer");
		if(tag.hasKey("linkedDimension"))
			linkedDimension = tag.getInteger("linkedDimension");
		if(tag.hasKey("owner"))
			owner = tag.getString("owner");
	}

	public List<Entity> getEntitiesInside()
	{
		ArrayList<Entity> list = new ArrayList<Entity>();
		List<Object> genList = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+2, zCoord+1));
		if(genList != null)
		{
			for(Object o: genList)
			{
				if(o instanceof EntityLivingBase)
					list.add((EntityLivingBase) o);
			}
		}
		return list;
	}

	@Override
	public boolean shouldChunkload()
	{
		return true;
	}

	@Override
	public SimpleCoordStore coords()
	{
		if(coords == null)
			coords = new SimpleCoordStore(this);
		return coords;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		ChunkCoordIntPair[] loadable = new ChunkCoordIntPair[1];
		loadable[0] = coords().toChunkCoords();
		return loadable;
	}

	@Override
	public void neighbourUpdated(Block neighbourBlockID)
	{
		if((!takingOff) && worldObj.getBlock(xCoord, yCoord+1, zCoord) != TardisMod.tardisTopBlock)
			worldObj.setBlock(xCoord, yCoord, zCoord, TardisMod.tardisTopBlock, worldObj.getBlockMetadata(xCoord, yCoord, zCoord),3);
	}
}
