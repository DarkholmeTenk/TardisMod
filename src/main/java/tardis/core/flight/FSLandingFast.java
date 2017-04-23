package tardis.core.flight;

public class FSLandingFast extends FSLanding
{
	private static final int SOUND_LENGTH = 100;
	private static final String SOUND = "tardismod:landingInt";

	public FSLandingFast()
	{
		super(SOUND_LENGTH, SOUND);
	}

	@Override
	protected FSAbstract getNextState()
	{
		return new FSLanded();
	}
}
