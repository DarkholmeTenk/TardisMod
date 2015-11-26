package tardis;

import io.darkcraft.darkcore.mod.config.ConfigFile;

public class Configs
{
	/*
	 * LEVEL CONFIG
	 */
	public static ConfigFile	levelConfig;
	public static int			xpBase			= 80;
	public static int			xpInc			= 20;
	public static int			rfBase			= 50000;
	public static int			rfInc			= 50000;
	public static int			rfPerT			= 4098;
	public static int			maxFlu			= 32000;
	public static int			numTanks		= 5;
	public static int			numInvs			= 30;

	public static int			levelLocate		= 3;
	public static int			levelSensors	= 5;
	public static int			levelStable		= 7;
	public static int			levelTransmat	= 9;
	public static int			levelRecall		= 11;
	public static int			levelTranq		= 15;
	public static int			levelClarity	= 13;
	public static int			levelSpawnProt	= 15;

	private static void refreshLevelConfigs()
	{
		if (levelConfig == null) levelConfig = TardisMod.configHandler.registerConfigNeeder("levels");
		levelLocate = levelConfig.getInt("Function - Locate", 3, "When the locate functionality is unlocked, -1 disables");
		levelSensors = levelConfig.getInt("Function - Sensors", 5, "When the sensors functionality is unlocked, -1 disables");
		levelStable = levelConfig.getInt("Function - Stabilizers", 7, "When the stabilizers functionality is unlocked, -1 disables");
		levelTransmat = levelConfig.getInt("Function - Transmat", 9, "When the transmat functionality is unlocked, -1 disables");
		levelRecall = levelConfig.getInt("Function - Recall", 11, "When the recall functionality is unlocked, -1 disables");
		levelTranq = levelConfig.getInt("Function - Tranquility", 15, "When the tranquility functionality is unlocked, -1 disables");
		levelClarity = levelConfig.getInt("Function - Clarity", 13, "When the clarity functionality is unlocked, -1 disables");
		levelSpawnProt = levelConfig.getInt("Function - Spawn Prev", 15, "When the spawn prevention functionality is unlocked, -1 disables");
	}

	/*
	 * CRAFTING CONFIG
	 */

	public static ConfigFile	craftConfig;
	public static int			maxLabSpeed			= 5;
	public static boolean		kontronCraftable	= false;
	public static boolean										keyCraftable		= true;
	public static boolean										keyReqKontron		= true;

	private static void refreshCraftingConfigs()
	{
		if (craftConfig == null) craftConfig = TardisMod.configHandler.registerConfigNeeder("crafting");
		maxLabSpeed			= craftConfig.getInt("Max speed", 5, "The maximum speed which the lab can operate at");
		kontronCraftable	= craftConfig.getBoolean("kontronCraftable", false, "If true, a standard crafting recipe is added for the kontron crystal");
		keyCraftable		= craftConfig.getBoolean("keyCraftable", true, "True if the key is craftable.", "False if they can only be spawned");
		keyReqKontron		= craftConfig.getBoolean("keyRequiresKontron", true, "True if the key requires a Kontron crystal to craft");

	}

	public static void refreshConfigs()
	{
		refreshLevelConfigs();
		refreshCraftingConfigs();
	}
}
