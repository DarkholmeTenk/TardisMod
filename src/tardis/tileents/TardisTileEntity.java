package tardis.tileents;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import tardis.core.TardisOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisTileEntity extends TileEntity
{
	private int tickTimer = 0;
	private int fadeTimer = 0;
	private double speed = 4 * 20;
	
	private boolean landed = false;
	private boolean takingOff = false;
	private boolean landing   = false;
	
	int linkedDimension;
	TardisCoreTileEntity linkedCore = null;

	public TardisTileEntity()
	{
		landed = false;
	}
	
	private void sendTransPacket()
	{
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
		
	}
	
	public void updateEntity()
	{
		if(isTakingOff() || isLanding())
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if(++fadeTimer > (speed * 5.5))
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
			//sendTransPacket();
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
	
	private void setSpeed()
	{
		if(linkedCore == null)
			speed = 4 * 20;
		else
			speed = linkedCore.getSpeed();
	}
	
	private void takeoff()
	{
		setSpeed();
		fadeTimer = 0;
		takingOff = true;
	}
	
	private void land()
	{
		setSpeed();
		fadeTimer = 0;
		landing = true;
		TardisOutput.print("TTE", "LANDING!!!!");
	}
	
	public float getTransparency()
	{
		double multiplier = 1;
		if(!landed && !isLanding())
			return 0.0F;
		else if(isTakingOff() || isLanding())
		{
			if(isLanding())
				multiplier = (fadeTimer / (speed * 5.5));
			else if(isTakingOff())
				multiplier = 1 - (fadeTimer / (speed * 5.5));
			
			double remainder;
			double transVal;
			if(isLanding())
			{
				remainder = ((fadeTimer - (speed/2)) % speed);
				transVal = multiplier * (Math.abs(1-((2*remainder)/speed)));
			}
			else
			{
				remainder = (fadeTimer % speed);
				transVal = multiplier * (Math.abs(1-((2*remainder)/speed)));
			}
			//TardisOutput.print("TTE", "trans:" + remainder + "," + transVal + "," + multiplier);
			return (float) transVal;
		}
		else
		{
			return 1.0F;
		}
	}
	
	public void doorActivated(World world, int x, int y, int z, EntityPlayer player)
	{
		if(landed)
		{
			takeoff();
		}
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if(!landed)
			land();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		landed = tag.getBoolean("landed");
		fadeTimer = tag.getInteger("fadeTimer");
		linkedDimension = tag.getInteger("linkedDimension");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setBoolean("landed", landed);
		tag.setInteger("fageTimer", fadeTimer);
		tag.setInteger("linkedDimension", linkedDimension);
	}

}
