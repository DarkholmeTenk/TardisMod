package tardis.core.console.panel.types.normal.navmap;

import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlWheel;

public interface NavMap
{
	public int getVal(ControlWheel[] wheels, ControlLever[] levers);

	public boolean setVal(int val, int tolerance, ControlWheel[] wheels, ControlLever[] levers);
}
