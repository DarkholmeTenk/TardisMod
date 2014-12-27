package tardis.common.network;

import tardis.common.network.packet.ParticlePacket;
import tardis.common.network.packet.TardisAbstractPacket.PacketType;
import tardis.common.network.packet.TardisControlPacket;
import tardis.common.network.packet.TardisDimRegPacket;
import tardis.common.network.packet.TardisSoundPacket;

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
			new TardisSoundPacket(p.payload()).play();
		else if(type == PacketType.DIMREG)
			new TardisDimRegPacket(p.payload()).registerDims();
		else if(type == PacketType.CONTROL)
			new TardisControlPacket(p.payload()).activate();
		else if(type == PacketType.PARTICLE)
			new ParticlePacket(p.payload()).spawn();
	}
}
