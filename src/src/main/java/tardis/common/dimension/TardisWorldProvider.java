package tardis.common.dimension;

import tardis.common.core.Helper;
import tardis.common.tileents.TardisConsoleTileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

public class TardisWorldProvider extends WorldProvider
{
	public final ChunkCoordinates spawnPoint = new ChunkCoordinates(9,28,0);

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
	public boolean canRespawnHere()
	{
		return true;
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
		return spawnPoint;
    }
	
	@Override
	public ChunkCoordinates getRandomizedSpawnPoint()
    {
		return getSpawnPoint();
    }
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
		return BiomeGenBase.plains;
    }
	
	@Override
	public boolean isDaytime()
	{
		if(dimensionId != 0)
		{
			TardisConsoleTileEntity con = Helper.getTardisConsole(worldObj);
			if(con != null)
				return con.getDaytimeSetting();
		}
		return true;
	}
	
	@Override
	public long getWorldTime()
    {
		return isDaytime() ? 6000 : 18000;
    }
}
