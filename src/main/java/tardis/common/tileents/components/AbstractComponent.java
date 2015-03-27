package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;

public abstract class AbstractComponent implements ITardisComponent
{
	protected ComponentTileEntity parentObj;
	
	protected int world;
	protected int xCoord;
	protected int yCoord;
	protected int zCoord;
	protected int tt = 0;
	
	private static ConfigFile config = null;
	protected static double nanogeneRange = 36;
	protected static double nanogeneTimer = 10;
	protected static int nanogeneCost = 1;
	protected static int nanogeneHealAmount = 2;
	protected static boolean nanogeneFeed = true;
	protected static Random rand = new Random();

	static
	{
		if(config == null)
			refreshConfigs();
	}
	
	public static void refreshConfigs()
	{
		if(config == null)
			config = TardisMod.configHandler.registerConfigNeeder("Components");
		nanogeneRange = Math.pow(config.getDouble("nanogene range", 6, "The range at which nanogenes can heal"),2);
		nanogeneTimer = config.getInt("nanogene timer", 10, "The number of ticks between each nanogene healing pulse");
		nanogeneCost = config.getInt("nanogene cost", 1, "The amount of Artron energy used up each time a nanogene heals");
		nanogeneHealAmount = config.getInt("nanogene heal amount", 2, "The amount of health a nanogene can restore per pulse");
		nanogeneFeed = config.getBoolean("nanogene feed", true, "Whether nanogenes should also feed players as well as heal");
	}
	
	protected void parentAdded(ComponentTileEntity parent)
	{
		parentObj = parent;
		world  = WorldHelper.getWorldID(parent.getWorldObj());
		xCoord = parent.xCoord;
		yCoord = parent.yCoord;
		zCoord = parent.zCoord;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
	}

	@Override
	public void die()
	{
		parentObj = null;
	}

	@Override
	public void revive(ComponentTileEntity parent)
	{
		parentAdded(parent);
	}
	
	public CoreTileEntity getCore()
	{
		if(parentObj != null)
			return parentObj.getCore();
		return null;
	}
	
	public IArtronEnergyProvider getArtronEnergyProvider()
	{
		if(parentObj != null)
			return parentObj.getArtronEnergyProvider();
		return null;
	}
	
	@Override
	public void updateTick()
	{
		tt++;
	}
}
