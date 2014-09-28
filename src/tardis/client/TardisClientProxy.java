package tardis.client;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import tardis.TardisProxy;
import tardis.tileents.TardisTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;

public class TardisClientProxy extends TardisProxy
{

	public TardisClientProxy()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TardisTileEntity.class, new TardisRenderer());
	}

	@Override
	public void handleTardisTransparency(int worldID,int x, int y, int z)
	{
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(worldID);
		world.markBlockForRenderUpdate(x, y, z);
	}
}
