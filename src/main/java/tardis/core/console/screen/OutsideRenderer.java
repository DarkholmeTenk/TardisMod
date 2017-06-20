package tardis.core.console.screen;

import io.darkcraft.darkcore.mod.client.TextRenderer;

import tardis.core.TardisInfo;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.screen.ScreenFunction.IScreenRenderer;

public class OutsideRenderer implements IScreenRenderer
{
	public static final IScreenRenderer i = new OutsideRenderer();

	private OutsideRenderer(){}

	@Override
	public void render(TextRenderer renderer, TardisInfo info, ConsolePanel currentPanel)
	{
		// TODO Auto-generated method stub

	}

}
