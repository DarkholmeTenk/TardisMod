package tardis.common.tileents;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.Helper;
import tardis.common.dimension.TardisDataStore;

public class LandingPadTileEntity extends ComponentTileEntity
{
	boolean hadCore = false;

	@Override
	protected void dismantle(EntityPlayer pl)
	{
		if(!ServerHelper.isServer())
			return;
		super.dismantle(pl);
		WorldHelper.giveItemStack(pl, new ItemStack(TardisMod.landingPad,1));
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

	@Override
	public CoreTileEntity getCore()
	{
		if(Helper.isTardisWorld(worldObj))
			return super.getCore();
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(te instanceof TardisTileEntity)
		{
			if(!((TardisTileEntity)te).inFlight())
			{
				CoreTileEntity core = ((TardisTileEntity)te).getCore();
				if(core != null)
				{
					if(core.canBeAccessedExternally())
						return core;
					else
						System.out.println("NE");
				}
			}
		}
		return null;
	}

	@Override
	public TardisDataStore getDS()
	{
		CoreTileEntity c = getCore();
		if(c != null)
			return Helper.getDataStore(c);
		return Helper.getDataStore(this);
	}

	@Override
	public IArtronEnergyProvider getArtronEnergyProvider()
	{
		CoreTileEntity core = getCore();
		if(core != null)
			return core;
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(te instanceof IArtronEnergyProvider)
			return (IArtronEnergyProvider)te;
		IArtronEnergyProvider prov = Helper.getArtronProvider(this, true);
		return prov;
	}

	public boolean hasTardis()
	{
		return getCore() != null;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if((tt % 5) == 0)
		{
			if(!hadCore && (getCore() != null))
			{
				restart();
				hadCore = true;
			}
			else if(hadCore && (getCore() == null))
			{
				hadCore = false;
				restart();
			}
		}
	}

	public boolean isClear()
	{
		return worldObj.isAirBlock(xCoord, yCoord+1, zCoord) && worldObj.isAirBlock(xCoord, yCoord+2, zCoord);
	}
}
