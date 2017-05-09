package tardis.core.console.control;

import tardis.core.TardisInfo;

public interface ControlHolder
{
	public double yScale();

	public double xAngle();

	public void markDirty();

	public TardisInfo getTardisInfo();

	public int getSide();
}
