package tardis.core.console.enums;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;

import tardis.core.TardisInfo;

public interface IControlFunction
{
	public void activate(TardisInfo info, PlayerContainer player, boolean sneaking);
}
