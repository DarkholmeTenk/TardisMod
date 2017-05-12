package tardis.core.console.panel.types.normal;

import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelFacing;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelZ;

@NBTSerialisable
public class NormalPanelZ extends ConsolePanel implements NavPanelZ, NavPanelFacing
{

	@Override
	public void randomizeDestination()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentZ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setCurrentZ(int newX, int tolerance)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCurrentFacing()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
