package tardis.core.console.screen;

import io.darkcraft.darkcore.mod.client.TextRenderer;

import tardis.core.TardisInfo;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.screen.ScreenFunction.IScreenRenderer;

public class FlightRenderer implements IScreenRenderer
{
	public static final IScreenRenderer i = new FlightRenderer();

	private FlightRenderer(){}

	@Override
	public void render(TextRenderer renderer, TardisInfo info, ConsolePanel currentPanel)
	{
		// TODO Auto-generated method stub

	}

}
