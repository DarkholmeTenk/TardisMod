package tardis.common.core.flight;

import java.util.List;

public class FlightConfiguration
{
	private static String					defaultConfiguration	= "vpm;lpm;lgm;lpm";
	private static List<IFlightModifier>	defaultMods;

	static
	{
		FlightModifierRegistry.registerFlightModifier(new LandGroundModifier());
		FlightModifierRegistry.registerFlightModifier(new LandPadModifier());
		FlightModifierRegistry.registerFlightModifier(new ValidPositionModifier());
		FlightModifierRegistry.registerFlightModifier(new ShieldModifier());
	}

	private static void setDefault(String defaultConfig)
	{
		defaultConfiguration = defaultConfig;
	}

	public static void refreshConfigs()
	{
		setDefault("vpm;lpm;lgm;lpm;sm");
	}

	public static String getDefaultModifierString()
	{
		return defaultConfiguration;
	}

	public static List<IFlightModifier> getDefaultModifiers()
	{
		if(defaultMods == null)
			defaultMods = FlightModifierRegistry.getFlightModifierList(getDefaultModifierString());
		return defaultMods;
	}
}
