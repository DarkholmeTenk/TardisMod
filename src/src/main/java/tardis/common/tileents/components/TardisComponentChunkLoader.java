package tardis.common.tileents.components;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import tardis.api.IChunkLoader;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;

public class TardisComponentChunkLoader extends TardisAbstractComponent implements IChunkLoader
{
	World w;
	protected TardisComponentChunkLoader()
	{
		
	}
	
	public TardisComponentChunkLoader(TardisComponentTileEntity parent)
	{
		w = parent.getWorldObj();
	}
	
	
	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentChunkLoader(parent);
	}

	@Override
	public boolean shouldChunkload()
	{
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
					if(j == 0 && k == 0)
						continue;
					ChunkCoordIntPair ccip = new ChunkCoordIntPair(loadable[0].chunkXPos + j,loadable[0].chunkZPos + k);
					loadable[i] = ccip;
					i++;
				}
			}
			return loadable;
		}
		return null;
	}

}
