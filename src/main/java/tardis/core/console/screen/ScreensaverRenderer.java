package tardis.core.console.screen;

import io.darkcraft.darkcore.mod.client.TextRenderer;
import io.darkcraft.darkcore.mod.datastore.Colour;

import tardis.client.renderer.gallifreyan.Word;
import tardis.core.TardisInfo;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.screen.ScreenFunction.IScreenRenderer;

public class ScreensaverRenderer implements IScreenRenderer
{
	public static final IScreenRenderer i = new ScreensaverRenderer();

	private ScreensaverRenderer(){}

	private final Word tardismod = new Word("TARDISMOD").setColour(Colour.white);
	private final Word dark = new Word("DARKHOLMETENK").setColour(Colour.fromRegular(4, 130, 247));
	private final Word fox = new Word("FOXPOTATO").setColour(Colour.fromRegular(0, 204, 153));

	@Override
	public void render(TextRenderer renderer, TardisInfo info, ConsolePanel currentPanel)
	{
		renderer.setText();
		tardismod.render(24,24,24);
		dark.render(12,62,12);
		fox.render(12,62,38);
	}

}
