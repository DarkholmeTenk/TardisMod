package tardis.core.flight;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;

import tardis.core.TardisInfo;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelUncoordinated;

public abstract class FSFlight extends FSAbstract
{
	private static final int FLIGHT_SOUND_TICKS = 69;
	private static final String FLIGHT_SOUND = "tardismod:engines";

	@NBTProperty
	protected SimpleCoordStore takeoffLocation;

	private final boolean uncoordinated;

	public FSFlight(boolean uncoordinated)
	{
		super(FLIGHT_SOUND_TICKS, FLIGHT_SOUND);
		this.uncoordinated = uncoordinated;
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
		SimpleCoordStore position = getCurrentCoords();
		info.getDataStore().setExterior(position.getWorldObj(), position.x, position.y, position.z);
		if(shouldLand())
			return new FSLandingFast();
		boolean uncoord = info.getPanel(OptPanelUncoordinated.class).map(p->p.shouldBeUncoordinated()).orElse(false);
		if(uncoord == uncoordinated)
			return reset();

		FSFlight flight;
		if(uncoord)
			flight = new FSFlightDrift();
		else
			flight = new FSFlightDestination();
		flight.takeoffLocation = takeoffLocation;
		return flight;
	}

	protected abstract boolean shouldLand();

	protected abstract SimpleCoordStore getCurrentCoords();

	@Override
	public void setTardisInfo(TardisInfo info)
	{
		super.setTardisInfo(info);
		if(takeoffLocation == null)
			takeoffLocation = info.getDataStore().getExteriorSCS();
	}
}
