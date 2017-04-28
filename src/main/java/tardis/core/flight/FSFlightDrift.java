package tardis.core.flight;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import tardis.core.console.panel.interfaces.OptionPanels.OptPanelUncoordinated;

public class FSFlightDrift extends FSFlight
{
	public FSFlightDrift()
	{
		super(true);
	}

	@Override
	protected boolean shouldLand()
	{
		return info.getConsole().getPanel(OptPanelUncoordinated.class).map(p->!p.shouldBeUncoordinated()).orElse(true);
	}

	@Override
	protected SimpleCoordStore getCurrentCoords()
	{
		return takeoffLocation;
	}

}
