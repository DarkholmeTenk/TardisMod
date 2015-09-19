package tardis.common.core.events;

import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TardisTakeoffEvent extends TardisEvent
{
	private final CoreTileEntity core;
	private String cancelledMessage = null;

	public TardisTakeoffEvent(CoreTileEntity _core)
	{
		core = _core;
	}

	public CoreTileEntity getCore()
	{
		return core;
	}

	public String getMessage()
	{
		if(isCanceled())
			return cancelledMessage;
		return null;
	}

	public void cancel(boolean cancel, String message)
	{
		setCanceled(cancel);
		cancelledMessage = message;
	}
}
