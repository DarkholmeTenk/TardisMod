package tardis.common;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.world.World;

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
		return WorldHelper.getWorldServer(id);
	}

	public void init()
	{
	}
}
