package tardis.common.tileents.components;

import appeng.api.events.LocatableEventAnnounce;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentGrid extends TardisAbstractComponent implements IGridHost, ILocatable
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	private boolean powered = false;
	
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
			TardisCoreTileEntity core = Helper.getTardisCore(parentObj);
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
	
	@Override
	public void revive(TardisComponentTileEntity parent)
	{
		TardisOutput.print("TCG", "Reviving TCG");
		super.revive(parent);
		MinecraftForge.EVENT_BUS.post(new LocatableEventAnnounce(this,LocatableEventAnnounce.LocatableEvent.Register));
	}
	
	@Override
	public void die()
	{
		if(parentObj != null && parentObj.getWorldObj() != null)
			MinecraftForge.EVENT_BUS.post(new LocatableEventAnnounce(this,LocatableEventAnnounce.LocatableEvent.Unregister));
		super.die();
	}
	@Override
	public long getLocatableSerial()
	{
		if(myCoords == null)
			myCoords = new SimpleCoordStore(parentObj);
		return myCoords.hashCode();
	}
	@Override
	public IGridNode getGridNode(ForgeDirection dir)
	{
		TardisCoreTileEntity core = Helper.getTardisCore(parentObj);
		if(core != null)
		{
			//if(core.grid == null)
			//	core.grid = TardisMod.aeAPI.createGridNode(this);
		}
		return null;
	}
	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void securityBreak()
	{
		// TODO Auto-generated method stub
		
	}

}
