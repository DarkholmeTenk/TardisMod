package tardis.core.flight;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;

import tardis.core.TardisInfo;

public class FSFlightDestination extends FSFlight
{
	@NBTProperty
	private SimpleCoordStore destination;

	public FSFlightDestination()
	{
		super();
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
		//TODO: Set destination from console;
	}
}
