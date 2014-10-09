package tardis.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TardisProxy
{

	public TardisProxy()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void handleTardisTransparency(int worldID,int x, int y, int z)
	{
		
	}

	public void postAssignment()
	{
		
	}
	
	public World getWorld(int id)
	{
		MinecraftServer serv = MinecraftServer.getServer();
		if(serv != null)
			return serv.worldServerForDimension(id);
		return DimensionManager.getWorld(id);
	}
}
