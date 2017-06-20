package tardis.core.console.panel.types.normal.navmap;

import tardis.core.console.control.ControlHolder;
import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlWheel;

public interface NavMap
{
	public int getVal(Class<? extends ControlHolder> clazz, ControlWheel[] wheels, ControlLever[] levers);

	public boolean setVal(int val, int tolerance, Class<? extends ControlHolder> clazz,
			ControlWheel[] wheels, ControlLever[] levers);
}
