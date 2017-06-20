package tardis.core.console.panel.types.normal;

import static tardis.core.console.enums.ManualConstants.MNL_CONTROL_X;

import tardis.core.console.control.AbstractControl.ControlBuilder;
import tardis.core.console.control.ControlGauge.ControlGaugeBuilder;
import tardis.core.console.control.ControlLever;
import tardis.core.console.control.ControlLever.ControlLeverBuilder;
import tardis.core.console.control.ControlPush.ControlPushBuilder;
import tardis.core.console.control.ControlWheel;
import tardis.core.console.control.ControlWheel.ControlWheelBuilder;
import tardis.core.console.control.models.ModelSlider;
import tardis.core.console.enums.ConsolePermissions;
import tardis.core.console.panel.ConsolePanel;
import tardis.core.console.panel.group.NavGroup;
import tardis.core.console.panel.interfaces.NavPanels.NavPanelX;
import tardis.core.console.panel.types.normal.navmap.NavMap;
import tardis.core.console.panel.types.normal.navmap.RegularNavMap;

public class NormalPanelX extends ConsolePanel implements NavPanelX
{
	private ControlLever[] levers = new ControlLever[6];
	{
		ControlBuilder<ControlLever> regularLeverBuilder =
				new ControlLeverBuilder(-6,6,0)
					.withModel(new ModelSlider())
					.isFlightControl()
					.withScale(0.3, 0.3, 0.3)
					.withManualText(MNL_CONTROL_X);
		levers[0] = addControl(regularLeverBuilder.atPosition(0.8, 0.4));
		levers[1] = addControl(regularLeverBuilder.atPosition(1.1, 0.4));
		levers[2] = addControl(regularLeverBuilder.atPosition(1.4, 0.4));
		levers[3] = addControl(regularLeverBuilder.atPosition(1.7, 0.4));
		levers[4] = addControl(regularLeverBuilder.atPosition(2.0, 0.6));
		levers[5] = addControl(regularLeverBuilder.atPosition(2.3, 0.8).withScale(0.2, 0.6, 0.6));
		addControl(new ControlGaugeBuilder(-6, 6, ()->(double)levers[0].getValue())
					.withFormatString("Lever 0: %.0f")
					.atPosition(1.7,0.8));
	}

	private ControlWheel[] wheels = new ControlWheel[2];
	{
		ControlBuilder<ControlWheel> regularWheelBuilder = new ControlWheelBuilder(0,10,0)
				.isFlightControl()
				.requiresPermission(ConsolePermissions.FLIGHT)
				.withScale(0.5, 0.5, 0.5)
				.withManualText(MNL_CONTROL_X);
		wheels[0] = addControl(regularWheelBuilder.atPosition(0.6, 0.75));
		wheels[1] = addControl(regularWheelBuilder.atPosition(1.1, 0.75));

		addControl(new ControlPushBuilder(()->{
			getTardisInfo().getConsole().getPanelGroup(NavGroup.class).ifPresent(p->p.randomizeControls());
			markDirty();
		})
						.atPosition(1.3, 0.15));
	}

	private NavMap getNavMap()
	{
		return RegularNavMap.i;
	}

	@Override
	public int getCurrentX()
	{
		return getNavMap().getVal(NormalPanelX.class, wheels, levers);
	}

	@Override
	public boolean setCurrentX(int newX, int tolerance)
	{
		return getNavMap().setVal(newX, tolerance, NormalPanelX.class, wheels, levers);
	}

	@Override
	public void randomizeDestination()
	{
		for(ControlLever lever : levers)
			lever.randomize();
		for(ControlWheel wheel : wheels)
			wheel.randomize();
	}

}
