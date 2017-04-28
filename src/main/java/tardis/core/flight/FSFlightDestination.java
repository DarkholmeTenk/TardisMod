package tardis.core.flight;

import java.util.NoSuchElementException;
import java.util.Optional;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;

import tardis.core.TardisInfo;
import tardis.core.console.panel.group.NavGroup;

public class FSFlightDestination extends FSFlight
{
	@NBTProperty
	private SimpleCoordStore destination;

	public FSFlightDestination()
	{
		super(false);
	}

	@Override
	protected boolean shouldLand()
	{
		return true;
	}

	@Override
	public void setTardisInfo(TardisInfo info)
	{
		super.setTardisInfo(info);
		try
		{
			if(destination == null)
			{
				Optional<NavGroup> navigation = info.getPanelGroup(NavGroup.class);
				navigation.ifPresent(nav->destination=nav.getDestination());
			}
		}
		catch(NoSuchElementException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected SimpleCoordStore getCurrentCoords()
	{
		//TODO: Sort out destination
		return takeoffLocation;
	}
}
