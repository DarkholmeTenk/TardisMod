package tardis.core.console.control.models;

public abstract class AbstractControlModel implements IControlModel
{
	private final double regularX;
	private final double regularY;
	private final double xAngle;

	protected AbstractControlModel(double regularX, double regularY, double xAngle)
	{
		this.regularX = regularX;
		this.regularY = regularY;
		this.xAngle = xAngle;
	}

	@Override
	public final double regularX()
	{
		return regularX;
	}

	@Override
	public final double regularY()
	{
		return regularY;
	}

	@Override
	public final double xAngle()
	{
		return xAngle;
	}
}
