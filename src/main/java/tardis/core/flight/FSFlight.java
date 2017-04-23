package tardis.core.flight;


public abstract class FSFlight extends FSAbstract
{
	private static final int FLIGHT_SOUND_TICKS = 69;
	private static final String FLIGHT_SOUND = "tardismod:engines";

	public FSFlight()
	{
		super(FLIGHT_SOUND_TICKS, FLIGHT_SOUND);
	}

	@Override
	protected void tick()
	{
		if(tt == 0)
			playSound("FLIGHT_SOUND");
		tickFlight();
	}

	protected void tickFlight() {}

	@Override
	protected FSAbstract getNextState()
	{
		if(shouldLand())
			return new FSLandingFast();
		return reset();
	}

	protected abstract boolean shouldLand();
}
