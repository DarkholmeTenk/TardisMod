package tardis.common.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.EngineTileEntity;

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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getSkyRenderer();
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getCloudRenderer();
			if(eng.getSpaceProjection())
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
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
		return BiomeGenBase.plains;
    }

	@Override
	public long getWorldTime()
    {
		if(dimensionId != 0)
		{
			ConsoleTileEntity con = Helper.getTardisConsole(worldObj);
			if(con == null)
				return super.getWorldTime();
			int position = con.getDaytimeSetting();
			switch(position)
			{
				case 0:
					return 18000L;
				case 1:
					return super.getWorldTime();
				case 2:
					return 6000L;
			}
		}
		return super.getWorldTime();
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getSkyColor(cameraEntity, partialTicks);
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getStarBrightness(par1);
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getHorizon();
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getCloudHeight();
			if(eng.getSpaceProjection())
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
			EngineTileEntity eng = Helper.getTardisEngine(worldObj);
			if(eng == null)
				return super.getFogColor(p_76562_1_, p_76562_2_);
			if(eng.getSpaceProjection())
				return Vec3.createVectorHelper(0, 0, 0);
			else
				return super.getFogColor(p_76562_1_, p_76562_2_);
		}
        return super.getFogColor(p_76562_1_, p_76562_2_);
    }

	
}
