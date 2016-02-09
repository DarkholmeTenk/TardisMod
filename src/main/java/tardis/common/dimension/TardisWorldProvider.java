package tardis.common.dimension;

import io.darkcraft.darkcore.mod.helpers.MathHelper;

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

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
		return BiomeGenBase.plains;
    }

	@Override
	public float getSunBrightnessFactor(float par1)
	{
		return super.getSunBrightnessFactor(par1);
	}

	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
    {
		if(dimensionId != 0)
		{
			ConsoleTileEntity con = Helper.getTardisConsole(worldObj);
			int position = 0;
			position = con.getDaytimeSetting();
			switch(position)
			{
				case 0:
					return 0f;
			
				case 1:
					int j = (int)(p_76563_1_ % 24000L);
					float f1 = ((float)j + p_76563_3_) / 24000.0F - 0.25F;

					if (f1 < 0.0F)
					{
						++f1;
					}

					if (f1 > 1.0F)
					{
						--f1;
					}

					float f2 = f1;
					f1 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) + 1.0D) / 2.0D);
					f1 = f2 + (f1 - f2) / 3.0F;
					return f1;
			
				case 2:
					return 1f;
			}
		}
		return 0f;
			
    }

//	@Override
//	public boolean isDaytime()
//	{
//		if(dimensionId != 0)
//		{
//			ConsoleTileEntity con = getConsole();
//			//ConsoleTileEntity con = Helper.getTardisConsole(worldObj);
//			if(con != null)
//				return con.getDaytimeSetting();
//		}
//		return true;
//	}
	

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
