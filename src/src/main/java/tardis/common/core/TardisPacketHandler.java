package tardis.common.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import tardis.TardisMod;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class TardisPacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		TardisOutput.print("PAC", "Packet handler handling shit");
		if(packet.channel.equals("TardisTrans"))
		{
			TardisOutput.print("PAC", "Packet handler handling trans");
			handleTardisTransPacket(packet);
		}
		else if(packet.channel.equals("TardisSn"))
		{
			TardisOutput.print("PAC", "Packet handler handling sound");
			handleTardisSoundPacket(packet);
		}
		else if(packet.channel.equals("TardisDR"))
		{
			TardisOutput.print("PAC", "Packet handler handling DR");
			handleTardisDRPacket(packet);
		}
	}
	
	public void handleTardisSoundPacket(Packet250CustomPayload packet)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
			return;
		
		DataInputStream inputStream = null;
		try
		{
			inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			NBTTagCompound nbt = (NBTTagCompound) NBTTagCompound.readNamedTag(inputStream);
			String sound = nbt.getString("sound");
			World w = Helper.getWorld(nbt.getInteger("world"));
			float vol = nbt.getFloat("vol");
			if(nbt.hasKey("x"))
			{
				int x = nbt.getInteger("x");
				int y = nbt.getInteger("y");
				int z = nbt.getInteger("z");
				if(w != null)
					w.playSound(x+0.5, y+0.5, z+0.5, sound, vol, 1, true);
			}
		}
		catch(IOException e)
		{
			TardisOutput.print("PAC", "TransPacketError:" + e.getMessage(),TardisOutput.Priority.ERROR);
		}
		finally
		{
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e) {	}
			}
		}
	}
	
	public void handleTardisDRPacket(Packet250CustomPayload packet)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().equals(Side.SERVER))
			return;
		DataInputStream inputStream = null;
		try
		{
			inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			NBTTagCompound nbt = (NBTTagCompound) NBTTagCompound.readNamedTag(inputStream);
			if(TardisMod.dimReg == null)
				TardisMod.dimReg = TardisDimensionRegistry.load();
			TardisMod.dimReg.readFromNBT(nbt);
			TardisMod.dimReg.registerDims();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e) {	}
			}
		}
	}
	
	public void handleTardisTransPacket(Packet250CustomPayload packet)
	{
		DataInputStream inputStream = null;
		try
		{
			inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			int worldID = inputStream.readInt();
			int x = inputStream.readInt();
			int y = inputStream.readInt();
			int z = inputStream.readInt();
			TardisOutput.print("PAC", "Packet handler " + worldID + "," + x +"," + y + "," + z);
			TardisMod.proxy.handleTardisTransparency(worldID, x, y, z);
		}
		catch(IOException e)
		{
			TardisOutput.print("PAC", "TransPacketError:" + e.getMessage(),TardisOutput.Priority.ERROR);
		}
		finally
		{
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e) {	}
			}
		}
	}

}
