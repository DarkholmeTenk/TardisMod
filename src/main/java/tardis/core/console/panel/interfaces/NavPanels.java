package tardis.core.console.panel.interfaces;

public class NavPanels
{
	public static interface NavPanelX
	{
		public int getCurrentX();

		public boolean setCurrentX(int newX, int tolerance);
	}

	public static interface NavPanelY
	{
		public int getCurrentY();

		public boolean setCurrentY(int newY);

		public boolean landOnGround();
	}

	public static interface NavPanelZ
	{
		public int getCurrentZ();

		public boolean setCurrentZ(int newX, int tolerance);
	}

	public static interface NavPanelFacing
	{
		public int getCurrentFacing();
	}

	public static interface NavPanelDims
	{
		public int getDestinationDimID();

		public int setDestinationDimID();
	}
}
