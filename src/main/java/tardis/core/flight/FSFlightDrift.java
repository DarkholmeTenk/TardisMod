package tardis.core.flight;

import tardis.core.console.panel.interfaces.OptionPanels.OptPanelUncoordinated;

public class FSFlightDrift extends FSFlight
{
	public FSFlightDrift()
	{
		super();
	}

	@Override
	protected boolean shouldLand()
	{
		return info.getConsole().getPanel(OptPanelUncoordinated.class).map(p->!p.shouldBeUncoordinated()).orElse(true);
	}

}
