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
}
