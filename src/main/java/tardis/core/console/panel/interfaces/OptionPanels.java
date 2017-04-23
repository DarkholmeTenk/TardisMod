package tardis.core.console.panel.interfaces;

public class OptionPanels
{
	public static interface OptPanelLandOnGround
	{
		public boolean shouldLandOnGround();
	}

	public static interface OptPanelLandOnPad
	{
		public boolean shouldLandOnLandingPad();
	}

	public static interface OptPanelRelativeCoords
	{
		public boolean areCoordinatesRelative();
	}

	public static interface OptPanelUncoordinated
	{
		public boolean shouldBeUncoordinated();
	}
}
