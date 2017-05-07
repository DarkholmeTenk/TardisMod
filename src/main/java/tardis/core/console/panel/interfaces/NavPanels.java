package tardis.core.console.panel.interfaces;

public interface NavPanels
{
	public void randomizeDestination();

	public static interface NavPanelX extends NavPanels
	{
		public int getCurrentX();

		public boolean setCurrentX(int newX, int tolerance);
	}

	public static interface NavPanelY extends NavPanels
	{
		public int getCurrentY();

		public boolean setCurrentY(int newY);
	}

	public static interface NavPanelZ extends NavPanels
	{
		public int getCurrentZ();

		public boolean setCurrentZ(int newX, int tolerance);
	}

	public static interface NavPanelFacing extends NavPanels
	{
		public int getCurrentFacing();
	}

	public static interface NavPanelDims
	{
		public int getDestinationDimID();

		public int setDestinationDimID();
	}
}
