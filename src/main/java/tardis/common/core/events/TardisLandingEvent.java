package tardis.common.core.events;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import tardis.common.tileents.CoreTileEntity;

public class TardisLandingEvent extends TardisEvent
{
	public final SimpleCoordStore landingPos;

	public TardisLandingEvent(CoreTileEntity core, SimpleCoordStore pos)
	{
		super(core);
		landingPos = pos;
	}

	@Override
	public String toString()
	{
		CoreTileEntity core = getCore();
		String owner = core == null ? "null" : core.getOwner();
		return "TARDIS Landing : " + owner + ":" + WorldHelper.getWorldID(core) + " : " + landingPos;
	}
}
