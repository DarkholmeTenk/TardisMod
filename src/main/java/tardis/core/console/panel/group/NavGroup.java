package tardis.core.console.panel.group;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import tardis.core.console.panel.interfaces.NavPanels.NavPanelDims;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelFacing;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelX;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelY;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelZ;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnGround;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnPad;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelRelativeCoords;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelUncoordinated;

public class NavGroup extends AbstractPanelGroup
{
	@Panel
	private NavPanelX xPanel;
	@Panel
	private NavPanelY yPanel;
	@Panel
	private NavPanelZ zPanel;
	@Panel
	private NavPanelFacing fPanel;
	@Panel
	private NavPanelDims dPanel;
	@Panel(required=false)
	private OptPanelLandOnGround logPanel;
	@Panel(required=false)
	private OptPanelLandOnPad lopPanel;
	@Panel(required=false)
	private OptPanelRelativeCoords relPanel;
	@Panel(required=false)
	private OptPanelUncoordinated uncPanel;

	public SimpleCoordStore getDestination()
	{
		return new SimpleCoordStore(dPanel.getDestinationDimID(), getX(), getY(), getZ());
	}

	public int getX()
	{
		int x = xPanel.getCurrentX();
		if(isRelativeCoords())
			x += info.getDataStore().exteriorX;
		return x;
	}

	public int getY()
	{
		int y = yPanel.getCurrentY();
		if(isRelativeCoords())
			y += info.getDataStore().exteriorY;
		return y;
	}

	public int getZ()
	{
		int z = zPanel.getCurrentZ();
		if(isRelativeCoords())
			z += info.getDataStore().exteriorZ;
		return z;
	}

	private boolean isRelativeCoords()
	{
		return relPanel != null ? relPanel.areCoordinatesRelative() : false;
	}
}
