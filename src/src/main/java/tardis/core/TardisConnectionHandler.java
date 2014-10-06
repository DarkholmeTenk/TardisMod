package tardis.core;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class TardisConnectionHandler implements IConnectionHandler
{

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		TardisOutput.print("TCH", "Sending TDR packet on login");
		Packet250CustomPayload p = TardisDimensionRegistry.getPacket();
		netHandler.handleCustomPayload(p);
		manager.addToSendQueue(p);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		TardisOutput.print("TCH", "Sending TDR packet");
		Packet250CustomPayload p = TardisDimensionRegistry.getPacket();
		netHandler.handleCustomPayload(p);
		manager.addToSendQueue(p);
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
	{
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
	{
	}

	@Override
	public void connectionClosed(INetworkManager manager)
	{

	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{

	}

}
