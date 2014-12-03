package tardis.common;

import cpw.mods.fml.common.event.FMLInterModComms;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
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
		return Helper.getWorldServer(id);
	}

	public void init()
	{
	}
}
