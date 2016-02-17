package tardis.common.dimension;

import io.darkcraft.darkcore.mod.helpers.MathHelper;
import javafx.scene.paint.Color;

import java.util.List;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.ConsoleTileEntity;

public class TardisWorldProvider extends WorldProvider
{
	public final ChunkCoordinates spawnPoint = new ChunkCoordinates(9,Helper.tardisCoreY,0);

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
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new TardisChunkManager(worldObj);
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

//	@Override
//	public BiomeGenBase getBiomeGenForCoords(int x, int z)
//    {
//		return BiomeGenBase.plains;
//    }

	@Override
	public float getSunBrightnessFactor(float par1)
	{
		return super.getSunBrightnessFactor(par1);
	}

	@Override
	public float calculateCelestialAngle(long par1, float par3)
    {
		return isDaytime() ? 0 : 0.5F;
    }

	@Override
	public boolean isDaytime()
	{
		if(dimensionId != 0)
		{
			ConsoleTileEntity con = getConsole();
			//ConsoleTileEntity con = Helper.getTardisConsole(worldObj);
			if(con != null)
				return con.getDaytimeSetting();
		}
		return true;
	}

	public ConsoleTileEntity getConsole()
	{
		if(worldObj == null)
			return null;
		List<Object> ents = worldObj.loadedTileEntityList;
		if(ents == null)
			return null;
		for(Object o : ents)
		{
			if(o instanceof ConsoleTileEntity)
				return ((ConsoleTileEntity)o);
		}
		return null;
	}

	public int getWorldVariance()
	{
		return MathHelper.round((Math.random() * 10) - 5);
	}

	@Override
	public long getWorldTime()
    {
		return getWorldVariance() + (isDaytime() ? 6000 : 18000);
    }
}
