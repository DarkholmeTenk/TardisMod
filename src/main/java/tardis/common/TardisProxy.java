package tardis.common;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TardisProxy
{
	public static final ResourceLocation defaultSkin = new ResourceLocation("tardismod","textures/models/Tardis.png");

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
