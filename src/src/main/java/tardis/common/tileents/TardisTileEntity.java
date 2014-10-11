package tardis.common.tileents;

import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TardisTileEntity extends TardisAbstractTileEntity
{
	private int fadeTimer = 0;
	
	private boolean landed = false;
	private boolean takingOff = false;
	private boolean landing   = false;
	
	private boolean takingOffSoundPlayed = false;
	private boolean landingSoundPlayed = false;
	
	Integer linkedDimension = null;
	TardisCoreTileEntity linkedCore = null;
	
/*	private void sendTransPacket()
	{
		if(worldObj.isRemote)
			return;
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "TardisTrans";
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		try
		{
			stream.writeInt(worldObj.provider.dimensionId);
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(zCoord);
			stream.writeFloat(getTransparency());
			packet.data = byteStream.toByteArray();
			packet.length = byteStream.size();
			MinecraftServer.getServer().getConfigurationManager().sendToAllNear(xCoord, yCoord, zCoord, 160, worldObj.provider.dimensionId, packet);
		}
		catch(Exception e)
		{
			TardisOutput.print("TTE", "PacketError:" + e.getMessage(),TardisOutput.Priority.ERROR);
		}
		
	}*/
	
	public void updateEntity()
	{
		
		if(inFlight())
		{
			if(isLanding() && !landingSoundPlayed)
			{
				worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:landing", 0.75F, 1, true);
				landingSoundPlayed = true;
			}
			else if(isTakingOff() && !takingOffSoundPlayed)
			{
				worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:takeoff", 0.75F, 1, true);
				takingOffSoundPlayed = true;
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if(++fadeTimer >( isLanding() ? 22 * 20 : 11 * 20))
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
	
	public void takeoff()
	{
		fadeTimer = 0;
		takingOff = true;
		worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:takeoff", 0.75F, 1, true);
		takingOffSoundPlayed = true;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void forceLand()
	{
		landing = false;
		landed = true;
	}
	
	public void land()
	{
		fadeTimer = 0;
		landing = true;
		worldObj.playSound(xCoord, yCoord, zCoord, "tardismod:landing", 0.75F, 1, true);
		landingSoundPlayed = true;
		TardisOutput.print("TTE", "LANDING!!!! " + (worldObj.isRemote?"REM":"SER") + ":" + (landed?"LAN":"UNL"));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void linkToDimension(int dimID)
	{
		linkedDimension = dimID;
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
				remainder = ((fadeTimer - (80/2)) % 80);
				transVal = multiplier * (Math.abs(1-((2*remainder)/80)));
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
		if(!inFlight())
		{
			if(linkedDimension == null)
			{
				linkedDimension = Helper.generateTardisInterior(player,this);
			}
			else
			{
				TardisCoreTileEntity te = Helper.getTardisCore(linkedDimension);
				if(te != null)
					te.enterTardis(player);
			}
		}
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
		tag.setBoolean("landed", landed);
		tag.setInteger("fadeTimer", fadeTimer);
		if(linkedDimension != null)
			tag.setInteger("linkedDimension", linkedDimension);
	}

	@Override
	public void readTransmittable(NBTTagCompound tag)
	{
		takingOff = tag.getBoolean("takingOff");
		landing = tag.getBoolean("landing");
		landed = tag.getBoolean("landed");
		fadeTimer = tag.getInteger("fadeTimer");
		if(tag.hasKey("linkedDimension"))
			linkedDimension = tag.getInteger("linkedDimension");
	}

}
