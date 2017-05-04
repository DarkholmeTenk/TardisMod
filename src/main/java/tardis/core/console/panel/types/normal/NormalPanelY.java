package tardis.core.console.panel.types.normal;

import tardis.core.console.control.AbstractControl.ControlBuilder;
import tardis.core.console.control.ControlToggleButton;
import tardis.core.console.control.ControlToggleButton.ControlToggleButtonBuilder;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelY;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnGround;
import tardis.core.console.panel.interfaces.OptionPanels.OptPanelLandOnPad;

public class NormalPanelY extends ConsolePanel implements NavPanelY, OptPanelLandOnGround, OptPanelLandOnPad
{
	{
		ControlBuilder<ControlToggleButton> builder = new ControlToggleButtonBuilder(true)
				.atPosition(0.75, 0.5)
				.withScale(0.2, 0.2, 0.2);
		addControl(builder);
		addControl(builder.atPosition(2.5, 0.6).withScale(0.3, 0.3, 0.3).withAngle(30));
		addControl(builder.atPosition(1.5, 0.6).withScale(0.6, 0.6, 0.6).withAngle(45));
	}

	@Override
	public void randomizeDestination()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldLandOnLandingPad()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldLandOnGround()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCurrentY()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setCurrentY(int newY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean landOnGround()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
