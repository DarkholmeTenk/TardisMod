package tardis.common.network;

import tardis.common.network.packet.ParticlePacket;
import tardis.common.network.packet.AbstractPacket.PacketType;
import tardis.common.network.packet.ControlPacket;
import tardis.common.network.packet.DimRegPacket;
import tardis.common.network.packet.SoundPacket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.CustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class TardisPacketHandler
{
	
	@SubscribeEvent
	public void handleCustomClientPacket(ClientCustomPacketEvent event)
	{
		handleCustomPacket(event);
	}
	
	@SubscribeEvent
	public void handleCustomServerPacket(ServerCustomPacketEvent event)
	{
		handleCustomPacket(event);
	}
	
	@SubscribeEvent
	public void handleCustomPacket(CustomPacketEvent event)
	{
		FMLProxyPacket p = event.packet;
		int discriminator = p.payload().getByte(0);
		p.payload().readerIndex(1);
		p.payload().discardReadBytes();
		PacketType type = PacketType.find(discriminator);
		if(type == PacketType.SOUND)
			new SoundPacket(p.payload()).play();
		else if(type == PacketType.DIMREG)
			new DimRegPacket(p.payload()).registerDims();
		else if(type == PacketType.CONTROL)
			new ControlPacket(p.payload()).activate();
		else if(type == PacketType.PARTICLE)
			new ParticlePacket(p.payload()).spawn();
	}
}
