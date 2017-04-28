package tardis.core.flight;

import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import tardis.core.console.panel.interfaces.OptionPanels.OptPanelUncoordinated;

@NBTSerialisable
public class FSTakeOff extends FSAbstract
{
	private static final int TAKEOFF_SOUND_LENGTH = 220;
	private static final String TAKEOFF_SOUND = "tardismod:takeoff";

	public FSTakeOff()
	{
		super(TAKEOFF_SOUND_LENGTH, TAKEOFF_SOUND);
	}

	@Override
	protected void tick(){}

	@Override
	protected FSAbstract getNextState()
	{
		if(info.getPanel(OptPanelUncoordinated.class).map(p->p.shouldBeUncoordinated()).orElse(false))
			return new FSFlightDrift();
		else
			return new FSFlightDestination();
	}

}
