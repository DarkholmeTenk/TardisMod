package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IChunkLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import tardis.Configs;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class ComponentChunkLoader extends AbstractComponent implements IChunkLoader
{
	World w;
	protected ComponentChunkLoader()
	{

	}

	public ComponentChunkLoader(ComponentTileEntity parent)
	{
		w = parent.getWorldObj();
	}


	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentChunkLoader(parent);
	}

	@Override
	public boolean shouldChunkload()
	{
		if(Configs.loadWhenOffline)
			return true;
		if(Helper.isTardisWorld(w))
		{
			CoreTileEntity cte = Helper.getTardisCore(w);
			if(cte != null)
			{
				String o = cte.getOwner();
				EntityPlayer pl = ServerHelper.getPlayer(o);
				return pl != null;
			}
		}
		return true;
	}

	@Override
	public SimpleCoordStore coords()
	{
		if(parentObj != null)
			return parentObj.coords();
		return null;
	}

	@Override
	public ChunkCoordIntPair[] loadable()
	{
		if(parentObj != null)
		{
			ChunkCoordIntPair[] loadable = new ChunkCoordIntPair[9];
			loadable[0] = coords().toChunkCoords();
			int i = 1;
			for(int j = -1;j<=1;j++)
			{
				for(int k = -1;k<=1;k++)
				{
					if((j == 0) && (k == 0)) continue;
					loadable[i++] = new ChunkCoordIntPair(loadable[0].chunkXPos + j,loadable[0].chunkZPos + k);
				}
			}
			return loadable;
		}
		return null;
	}

}
