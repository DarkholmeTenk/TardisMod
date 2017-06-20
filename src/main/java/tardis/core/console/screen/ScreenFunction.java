package tardis.core.console.screen;

import io.darkcraft.darkcore.mod.client.TextRenderer;

import tardis.core.TardisInfo;
import tardis.core.console.panel.ConsolePanel;

public enum ScreenFunction
{
	SCREENSAVER(ScreensaverRenderer.i),
	NAVIGATION(NavigationRenderer.i),
	FACING(FacingRenderer.i),
	TIME(TimeRenderer.i),
	OUTSIDE(OutsideRenderer.i),
	FLIGHT(FlightRenderer.i);

	public final IScreenRenderer renderer;

	private ScreenFunction(IScreenRenderer renderer)
	{
		this.renderer = renderer;
	}

	public static interface IScreenRenderer
	{
		public void render(TextRenderer renderer, TardisInfo info, ConsolePanel currentPanel);
	}
}
