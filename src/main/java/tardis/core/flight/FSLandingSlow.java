package tardis.core.flight;

public class FSLandingSlow extends FSLanding
{
	private static final int SOUND_LENGTH = 440;
	private static final String SOUND = "tardismod:landing";

	public FSLandingSlow()
	{
		super(SOUND_LENGTH, SOUND);
	}
}
