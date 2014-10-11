package tardis.common.dimension;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

public class TardisWorldProvider extends WorldProvider
{

	public TardisWorldProvider()
	{
	}

	@Override
	public String getDimensionName()
	{
		return "Tardis Interior";
	}
	
	@Override
	public String getSaveFolder()
	{
		return (dimensionId == 0 ? null : "tardis/DIM" + dimensionId);
	}
	
	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new TardisChunkProvider(worldObj);
	}	
	
	@Override
	public void updateWeather()
	{
		worldObj.rainingStrength = 0;
		worldObj.thunderingStrength = 0;
		worldObj.updateWeatherBody();
		if(worldObj.isRaining())
			worldObj.toggleRain();
	}
	
	@Override
	public ChunkCoordinates getSpawnPoint()
    {
		return new ChunkCoordinates(9,28,0);
    }
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
		return BiomeGenBase.plains;
    }
	
	@Override
	public long getWorldTime()
    {
		return 6000;
    }
}
