package tardis.common.tileents.components;

import appeng.api.DimentionalCoord;
import appeng.api.WorldCoord;
import appeng.api.events.GridTileLoadEvent;
import appeng.api.events.GridTileUnloadEvent;
import appeng.api.me.tiles.IGridTeleport;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentGrid extends TardisAbstractComponent
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	private boolean powered = false;
	private IGridInterface grid = null;
	
	protected TardisComponentGrid()	{	}
	public TardisComponentGrid(TardisComponentTileEntity parent)
	{
		//parentAdded(parent);
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentGrid(parent);
	}
	
	@Override
	public void updateTick()
	{
		if(!inited && parentObj != null)
		{
			inited = true;
			TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
			if(core != null)
			{
				core.addGridLink(myCoords);
			}
		}
	}
	
	@Override
	public void parentAdded(TardisComponentTileEntity parent)
	{
		super.parentAdded(parent);
		myCoords = new SimpleCoordStore(parent);
	}
	
	public DimentionalCoord[] findRemoteSide()
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj.worldObj);
		if(core != null)
			return core.getGridLinks(myCoords);
		return null;
	}
	
	public boolean getPowered()
	{
		return powered;
	}
	
	public void setPowered(boolean bePowered)
	{
		powered = bePowered;
	}
	
	public void setGrid(IGridInterface gi)
	{
		grid = gi;
	}
	
	public IGridInterface getGrid()
	{
		return grid;
	}
	
	@Override
	public void revive(TardisComponentTileEntity parent)
	{
		TardisOutput.print("TCG", "Reviving TCG");
		super.revive(parent);
		MinecraftForge.EVENT_BUS.post(new GridTileLoadEvent(parentObj,parentObj.worldObj,new WorldCoord(xCoord,yCoord,zCoord)));
	}
	
	@Override
	public void die()
	{
		MinecraftForge.EVENT_BUS.post(new GridTileUnloadEvent(parentObj,parentObj.worldObj,new WorldCoord(xCoord,yCoord,zCoord)));
		super.die();
	}

}
