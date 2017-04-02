package tardis.common.tileents.components;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.api.IArtronEnergyProvider;
import tardis.common.dimension.TardisDataStore;
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

	protected static Random rand = new Random();

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

	public TardisDataStore getDatastore()
	{
		if(parentObj != null)
			return parentObj.getDS();
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
