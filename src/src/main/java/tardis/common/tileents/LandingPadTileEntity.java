package tardis.common.tileents;

import tardis.TardisMod;
import tardis.api.IArtronEnergyProvider;
import tardis.common.core.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class LandingPadTileEntity extends TardisComponentTileEntity
{
	boolean hadCore = false;
	
	@Override
	protected void dismantle(EntityPlayer pl)
	{
		Helper.giveItemStack(pl, new ItemStack(TardisMod.landingPad,1));
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}
	
	@Override
	public TardisCoreTileEntity getCore()
	{
		if(Helper.isTardisWorld(worldObj))
			return super.getCore();
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(te instanceof TardisTileEntity)
		{
			if(!((TardisTileEntity)te).inFlight())
			{
				TardisCoreTileEntity core = ((TardisTileEntity)te).getCore();
				if(core != null && core.canBeAccessedExternally())
					return core;
			}
		}
		return null;
	}
	
	@Override
	public IArtronEnergyProvider getArtronEnergyProvider()
	{
		TardisCoreTileEntity core = getCore();
		if(core != null)
			return (IArtronEnergyProvider)core;
		TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
		if(te instanceof IArtronEnergyProvider)
			return (IArtronEnergyProvider)te;
		return null;
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
			if(!hadCore && getCore() != null)
			{
				restart();
				hadCore = true;
			}
			else if(hadCore && getCore() == null)
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
