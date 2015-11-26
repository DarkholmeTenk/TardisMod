package tardis.common.core.flight;

import io.darkcraft.darkcore.mod.config.ConfigFile;

import java.util.List;

import tardis.TardisMod;

public class FlightConfiguration
{
	public static ConfigFile				config;
	public static int						shiftPressTime			= 60;
	public static double					maxSpeed;
	public static double					explodeChance			= 0.25;
	public static int						energyCostDimChange		= 2000;
	public static int						energyCostFlightMax		= 3000;
	public static int						maxMoveForFast			= 3;
	public static int						energyPerSpeed			= 200;

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
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("flight");
		setDefault("vpm;lpm;lgm;lpm;sm");
		shiftPressTime = config.getInt("shift press time", 60, "The amount of time in ticks to shift press a button after pressing normally", "20 ticks = 1 second");
		explodeChance = config.getDouble("Explosion chance", 0.6, "The chance of an explosion being caused if an active control is not pressed");
		maxSpeed = config.getDouble("max speed", 8, "The maximum speed setting that can be reached");
		energyCostDimChange = config.getInt("Dimension jump cost", 2000, "How much energy it costs to jump between dimensions");
		energyCostFlightMax = config.getInt("Max flight cost", 3000, "The maximum amount that a flight can cost");
		maxMoveForFast = config.getInt("Short hop distance", 3, "The maximum distance for which a jump can be considered a short hop which takes less time");
		energyPerSpeed = config.getInt("Energy per speed", 50, "Energy per unit of block speed", "The tardis moves at a max speed of (max flight cost / energy per speed) blocks per tick");
	}

	public static String getDefaultModifierString()
	{
		return defaultConfiguration;
	}

	public static List<IFlightModifier> getDefaultModifiers()
	{
		if (defaultMods == null) defaultMods = FlightModifierRegistry.getFlightModifierList(getDefaultModifierString());
		return defaultMods;
	}
}
