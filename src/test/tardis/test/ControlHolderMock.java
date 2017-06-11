package tardis.test;

import tardis.core.TardisInfo;
import tardis.core.console.control.AbstractControl;
import tardis.core.console.control.ControlHolder;

public class ControlHolderMock implements ControlHolder
{

	@Override
	public double yScale()
	{
		return 1;
	}

	@Override
	public double xAngle()
	{
		return 0;
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public TardisInfo getTardisInfo()
	{
		return null;
	}

	@Override
	public int getSide()
	{
		return 0;
	}

	@Override
	public void activated(AbstractControl control)
	{
	}

}
