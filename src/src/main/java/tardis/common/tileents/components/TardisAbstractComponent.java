package tardis.common.tileents.components;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.Helper;
import tardis.common.core.TardisConfigFile;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public abstract class TardisAbstractComponent implements ITardisComponent
{
	protected TardisComponentTileEntity parentObj;
	
	protected int world;
	protected int xCoord;
	protected int yCoord;
	protected int zCoord;
	protected int tt = 0;
	
	private static TardisConfigFile config = null;
	protected static double nanogeneRange = 36;
	protected static double nanogeneTimer = 10;
	protected static int nanogeneCost = 1;
	protected static int nanogeneHealAmount = 2;
	protected static Random rand = new Random();

	static
	{
		config = TardisMod.configHandler.getConfigFile("Components");
		nanogeneRange = Math.pow(config.getDouble("nanogene range", 6),2);
		nanogeneTimer = config.getInt("nanogene timer", 10);
		nanogeneCost = config.getInt("nanogene cost", 1);
		nanogeneHealAmount = config.getInt("nanogene heal amount", 2);
	}
	protected void parentAdded(TardisComponentTileEntity parent)
	{
		parentObj = parent;
		world  = Helper.getWorldID(parent.getWorldObj());
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
	public void revive(TardisComponentTileEntity parent)
	{
		parentAdded(parent);
	}
	
	public TardisCoreTileEntity getCore()
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
