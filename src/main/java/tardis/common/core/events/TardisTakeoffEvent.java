package tardis.common.core.events;

import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class TardisTakeoffEvent extends TardisEvent
{
	private String cancelledMessage = null;

	public TardisTakeoffEvent(CoreTileEntity core)
	{
		super(core);
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
