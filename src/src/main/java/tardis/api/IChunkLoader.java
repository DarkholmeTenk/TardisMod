package tardis.api;

import net.minecraft.world.ChunkCoordIntPair;
import tardis.common.core.store.SimpleCoordStore;

public interface IChunkLoader
{
	public boolean shouldChunkload();
	
	public SimpleCoordStore coords();
	
	public ChunkCoordIntPair[] loadable();
}
