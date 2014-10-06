package tardis;

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
		return DimensionManager.getWorld(id);
	}
}
