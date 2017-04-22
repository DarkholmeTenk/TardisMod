package tardis.core.console.panel.types.normal;

import tardis.core.console.control.AbstractControl.ControlBuilder;
import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlLever.ControlLeverBuilder;
import tardis.core.console.control.ControlWheel;
import tardis.core.console.control.ControlWheel.ControlWheelBuilder;
import tardis.core.console.enums.ConsolePermissions;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelX;

public class NormalPanelX extends ConsolePanel implements NavPanelX
{
	private ControlLever[] levers = new ControlLever[5];
	{
		ControlBuilder<ControlLever> regularLeverBuilder =
				new ControlLeverBuilder(-6,6,0)
					.isFlightControl()
					.requiresPermission(ConsolePermissions.FLIGHT)
					.withScale(0.3, 0.3, 0.3);
		addControl(levers[0] = regularLeverBuilder.atPosition(0.3, 0.4).build());
		addControl(levers[1] = regularLeverBuilder.atPosition(0.6, 0.4).build());
		addControl(levers[2] = regularLeverBuilder.atPosition(0.9, 0.4).build());
		addControl(levers[3] = regularLeverBuilder.atPosition(1.2, 0.4).withAngle(45).build());
		addControl(levers[4] = regularLeverBuilder.atPosition(1.6, 0.6).withAngle(90).build());
	}

	private ControlWheel[] wheels = new ControlWheel[2];
	{
		ControlBuilder<ControlWheel> regularWheelBuilder =
				new ControlWheelBuilder(-6,6,0)
				.isFlightControl()
				.requiresPermission(ConsolePermissions.FLIGHT)
				.withScale(0.5, 0.5, 0.5);
		addControl(wheels[0] = regularWheelBuilder.atPosition(0.0, 1).build());
		addControl(wheels[1] = regularWheelBuilder.atPosition(0.5, 1).build());
	}

	@Override
	public int getCurrentX()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setCurrentX(int newX, int tolerance)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
