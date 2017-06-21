package tardis.core.console.screen;

import java.util.Optional;

import io.darkcraft.darkcore.mod.client.TextRenderer;
import io.darkcraft.darkcore.mod.datastore.Colour;

import tardis.client.renderer.gallifreyan.GNumber;
import tardis.client.renderer.gallifreyan.Word;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.core.TardisInfo;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelX;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelY;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelZ;
import tardis.core.console.screen.ScreenFunction.IScreenRenderer;

public class NavigationRenderer implements IScreenRenderer
{
	public static final IScreenRenderer i = new NavigationRenderer();

	private NavigationRenderer(){}

	private static Word gX = new Word("XPOS");
	private static Word gY = new Word("YPOS");
	private static Word gZ = new Word("ZPOS");

	private static final Colour active = Colour.fromRegular(30, 124, 255);

	@Override
	public void render(TextRenderer renderer, TardisInfo info, ConsolePanel currentPanel)
	{
		ConsoleTileEntity console = info.getConsole();
		if(console == null) return;
		Optional<NavPanelX> xPanel = console.getPanel(NavPanelX.class);
		Optional<NavPanelY> yPanel = console.getPanel(NavPanelY.class);
		Optional<NavPanelZ> zPanel = console.getPanel(NavPanelZ.class);
		xPanel.ifPresent(p-> {
			GNumber number = GNumber.get(p.getCurrentX());
			number.setColour(p == currentPanel ? active : Colour.white);
			gX.render(8, 10, 40);
			number.render(15, 14, 13);
		});
		yPanel.ifPresent(p-> {
			GNumber number = GNumber.get(p.getCurrentY());
			number.setColour(p == currentPanel ? active : Colour.white);
			gY.render(8, 37.5f, 6);
			number.render(15, 37.5f, 36);
		});
		zPanel.ifPresent(p-> {
			GNumber number = GNumber.get(p.getCurrentZ());
			number.setColour(p == currentPanel ? active : Colour.white);
			gZ.render(8, 65, 40);
			number.render(15, 61, 13);
		});
	}

}
