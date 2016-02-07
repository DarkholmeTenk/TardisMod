package tardis.common.integration.ae;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;

public interface ITMGrid extends IGridHost
{
	public void addConnection(IGridConnection con);
}
