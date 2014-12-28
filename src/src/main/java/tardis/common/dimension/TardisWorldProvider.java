package tardis.common.dimension;

import java.util.List;

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
		worldChunkMgr = new TardisChunkManager();
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
		worldObj.prevRainingStrength = 0;
		worldObj.rainingStrength = 0;
		worldObj.prevRainingStrength = 0;
		worldObj.thunderingStrength = 0;
		worldObj.updateWeatherBody();
		if(worldObj.isRaining())
			worldObj.setRainStrength(0);
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
	public float getSunBrightnessFactor(float par1)
	{
		return super.getSunBrightnessFactor(getWorldTime());
		//return isDaytime() ? 1.0f : 0.0f;
	}
	
	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
    {
        return super.calculateCelestialAngle(getWorldTime(), 0);
    }
	
	@Override
	public boolean isDaytime()
	{
		if(dimensionId != 0)
		{
			TardisConsoleTileEntity con = getConsole();
			//TardisConsoleTileEntity con = Helper.getTardisConsole(worldObj);
			if(con != null)
				return con.getDaytimeSetting();
		}
		return true;
	}
	
	public TardisConsoleTileEntity getConsole()
	{
		if(worldObj == null)
			return null;
		List<Object> ents = worldObj.loadedTileEntityList;
		if(ents == null)
			return null;
		for(Object o : ents)
		{
			if(o instanceof TardisConsoleTileEntity)
				return ((TardisConsoleTileEntity)o);
		}
		return null;
	}
	
	@Override
	public long getWorldTime()
    {
		return isDaytime() ? 6000 : 18000;
    }
}
