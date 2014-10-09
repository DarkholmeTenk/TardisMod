package tardis.common.dimension;

import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class TardisChunkProvider implements IChunkProvider
{
	World worldObj;

	public TardisChunkProvider(World par1World)
	{
		worldObj = par1World;
	}

	@Override
	public boolean chunkExists(int i, int j)
	{
		return true;
	}

	@Override
	public Chunk provideChunk(int i, int j)
	{
		byte[] abyte = new byte[32768];
		Chunk c = new Chunk(worldObj, abyte, i, j);
		c.generateSkylightMap();
		return c;
	}

	@Override
	public Chunk loadChunk(int i, int j)
	{
		return provideChunk(i,j);
	}

	@Override
	public void populate(IChunkProvider ichunkprovider, int i, int j)
	{
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate)
	{
		return true;
	}

	@Override
	public boolean unloadQueuedChunks()
	{
		return true;
	}

	@Override
	public boolean canSave()
	{
		return true;
	}

	@Override
	public String makeString()
	{
		return "stringThing";
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType enumcreaturetype, int i, int j, int k)
	{
		return null;
	}

	@Override
	public ChunkPosition findClosestStructure(World world, String s, int i, int j, int k)
	{
		return null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(int i, int j)
	{
	}

	@Override
	public void saveExtraData()
	{
	}

}
