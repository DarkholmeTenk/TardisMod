package tardis.core.flight;

public class FSLanded extends FSAbstract
{
	public FSLanded()
	{
		super(-1, null);
	}

	@Override
	protected void tick(){}

	@Override
	protected FSAbstract getNextState()
	{
		return this;
	}

	@Override
	public boolean canUseFlightControls()
	{
		return true;
	}
}
