package tardis.core.console.panel.types.normal;

import static tardis.core.console.enums.ManualConstants.MNL_CONTROL_Y;

import tardis.core.console.control.AbstractControl.ControlBuilder;
import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlLever.ControlLeverBuilder;
import tardis.core.console.control.ControlPushButton;
import tardis.core.console.control.ControlPushButton.ControlPushButtonBuilder;
import tardis.core.console.control.ControlScrewdriverHolder;
import tardis.core.console.control.ControlScrewdriverHolder.ControlScrewdriverHolderBuilder;
import tardis.core.console.control.ControlToggleButton;
import tardis.core.console.control.ControlToggleButton.ControlToggleButtonBuilder;
import tardis.core.console.enums.ManualConstants;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelDims;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelY;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnGround;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnPad;

public class NormalPanelY extends ConsolePanel implements NavPanelY, OptPanelLandOnGround, OptPanelLandOnPad, NavPanelDims
{
	private final ControlLever[] levers = new ControlLever[4];
	{
		ControlBuilder<ControlLever> builder = new ControlLeverBuilder(0,4,0)
				.isFlightControl()
				.withScale(0.2, 0.3, 0.3)
				.withAngle(90)
				.withManualText(MNL_CONTROL_Y);
		levers[0] = addControl(builder.atPosition(1.2, 0.6));
		levers[1] = addControl(builder.atPosition(1.3, 0.6));
		levers[2] = addControl(builder.atPosition(1.4, 0.6));
		levers[3] = addControl(builder.atPosition(1.5, 0.6));
	}

	private final ControlToggleButton landOnGround = addControl(new ControlToggleButtonBuilder(true)
			.isFlightControl()
			.withScale(0.25, 0.25, 0.25)
			.withManualText(ManualConstants.MNL_OPTION_LAND_ON_GROUND)
			.atPosition(1.0, 0.55));
	private final ControlToggleButton landOnPad = addControl(new ControlToggleButtonBuilder(true)
			.isFlightControl()
			.withScale(0.25, 0.25, 0.25)
			.withManualText(ManualConstants.MNL_OPTION_LAND_ON_PAD)
			.atPosition(1.0, 0.65));
	private final ControlScrewdriverHolder holder = addControl(new ControlScrewdriverHolderBuilder(false)
			.withScale(0.5, 0.5, 0.5)
			.atPosition(1.65,0.55));
	private final ControlPushButton generate = addControl(new ControlPushButtonBuilder(()->holder.generateScrewdriver())
			.withScale(0.25,0.25,0.25)
			.atPosition(1.65, 0.65));

	@Override
	public void randomizeDestination()
	{
		for(ControlLever lever : levers)
			lever.randomize();
	}

	@Override
	public boolean shouldLandOnLandingPad()
	{
		return landOnPad.getPressed();
	}

	@Override
	public boolean shouldLandOnGround()
	{
		return landOnGround.getPressed();
	}

	@Override
	public int getCurrentY()
	{
		return
				  (levers[0].getValue() << 6)
				+ (levers[1].getValue() << 4)
				+ (levers[2].getValue() << 2)
				+ (levers[3].getValue());
	}

	@Override
	public boolean setCurrentY(int newY)
	{
		levers[0].setValue((newY >> 6) & 3);
		levers[1].setValue((newY >> 4) & 3);
		levers[2].setValue((newY >> 2) & 3);
		levers[3].setValue((newY >> 0) & 3);
		return true;
	}

	@Override
	public int getDestinationDimID()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setDestinationDimID()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
