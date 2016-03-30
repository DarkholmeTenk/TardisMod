package tardis.common.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import tardis.common.core.helpers.Helper;

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
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer() 
	{
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getSkyRenderer();
			if(ds.getSpaceProjection())
				return new TardisWorldSkyRenderer();
			else{
				return super.getSkyRenderer();
			}
		}
		return super.getSkyRenderer();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer()
	{
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getCloudRenderer();
			if(ds.getSpaceProjection())
				return new TardisWorldSkyRenderer();
			else
				return super.getCloudRenderer();
		}
		return super.getCloudRenderer();
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
	public long getWorldTime()
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getWorldTime();
			int position = ds.getDaytimeSetting();
			switch(position)
			{
				case 0:
					return 18000L + getWorldVariance();
				case 1:
					return super.getWorldTime();
				case 2:
					return 6000L + getWorldVariance();
			}
		}
		return super.getWorldTime();
    }
	
	public float calculateRealCelestialAngle(float var)
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getWorldTime();
			int position = ds.getDaytimeSetting();
			switch(position)
			{
				case 0:
					return 0.5f;
				case 1:
					System.out.println(super.getWorldTime());
					float worldTime = super.getWorldTime();
			        int j = (int)(worldTime % 24000L);
			        float f1 = ((float)j + var) / 24000.0F - 0.25F;
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
					return 0f;
			}
		}
		return 0f;
    }
	
	public int getWorldVariance()
	{
		return MathHelper.round((Math.random() * 10) - 5);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getSkyColor(cameraEntity, partialTicks);
			if(ds.getSpaceProjection())
				return Vec3.createVectorHelper(0, 0, 0);
			else
				return super.getSkyColor(cameraEntity, partialTicks);
		}
        return super.getSkyColor(cameraEntity, partialTicks);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getStarBrightness(par1);
			if(ds.getSpaceProjection())
				return 1f;
			else
				return super.getStarBrightness(par1);
		}
		return super.getStarBrightness(par1);
    }
	
	@Override
	public double getHorizon()
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getHorizon();
			if(ds.getSpaceProjection())
				return 0;
			else
				return super.getHorizon();
		}
		return super.getHorizon();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_)
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);
			if(ds.getSpaceProjection())
				return null;
			else
				return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);
		}
		return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public float getCloudHeight()
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getCloudHeight();
			if(ds.getSpaceProjection())
				return 1f;
			else
				return super.getCloudHeight();
		}
		return super.getCloudHeight();
    }
	
	@SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
		if(dimensionId != 0)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if(ds == null)
				return super.getFogColor(p_76562_1_, p_76562_2_);
			if(ds.getSpaceProjection())
				return Vec3.createVectorHelper(0, 0, 0);
			else
				return super.getFogColor(p_76562_1_, p_76562_2_);
		}
        return super.getFogColor(p_76562_1_, p_76562_2_);
    }
}
