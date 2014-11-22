package tardis.common.network;

import tardis.common.core.TardisOutput;
import tardis.common.network.packet.TardisAbstractPacket.PacketType;
import tardis.common.network.packet.TardisDimRegPacket;
import tardis.common.network.packet.TardisSoundPacket;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.CustomNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class TardisPacketHandler
{
/*
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
					w.playSound(x+0.5, y+0.5, z+0.5, sound, vol * TardisMod.tardisVol, 1, true);
			}
		}
		catch(Exception e)
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
	}*/
	
	@SubscribeEvent
	public void handleCustomPacket(ClientCustomPacketEvent event)
	{
		FMLProxyPacket p = event.packet;
		Class clazz =event.getClass();
		TardisOutput.print("TPH","Received packet with channel:" + p.channel() +", " + clazz.getName());
		int discriminator = p.payload().getByte(0);
		p.payload().readerIndex(1);
		p.payload().discardReadBytes();
		PacketType type = PacketType.find(discriminator);
		if(type == PacketType.SOUND)
			new TardisSoundPacket(p.payload()).play();
		else if(type == PacketType.DIMREG)
			new TardisDimRegPacket(p.payload()).registerDims();
	}
	@SubscribeEvent
	public void handlePacket(Event event)
	{
		TardisOutput.print("TPH", event.getClass().getName());
		if(event instanceof CustomNetworkEvent)
		{
			Object wrappedEvent = ((CustomNetworkEvent)event).wrappedEvent;
			if(wrappedEvent != null)
				TardisOutput.print("TPH","w:"+wrappedEvent.getClass().getName());
		}
	}
}
