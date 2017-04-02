package tardis;

import io.darkcraft.darkcore.mod.config.CType;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.config.ConfigItem;
import io.darkcraft.darkcore.mod.helpers.MathHelper;

import tardis.common.core.TardisOutput;

public class Configs
{
	/*
	 * LEVEL CONFIG
	 */
	public static ConfigFile	levelConfig;
	public static int			xpBase				= 80;
	public static int			xpInc				= 20;

	public static int			maxNumRooms			= 6;
	public static int			maxNumRoomsInc		= 6;
	public static int			maxEnergy			= 1000;
	public static int			maxEnergyInc		= 1000;
	public static int			energyPerSecond		= 1;
	public static int			energyPerSecondInc	= 1;

	public static int			levelLocate			= 3;
	public static int			levelSensors		= 5;
	public static int			levelStable			= 7;
	public static int			levelTransmat		= 9;
	public static int			levelRecall			= 11;
	public static int			levelTranq			= 15;
	public static int			levelClarity		= 13;
	public static int			levelSpawnProt		= 15;

	private static void refreshLevelConfig()
	{
		if (levelConfig == null) levelConfig = TardisMod.configHandler.registerConfigNeeder("levels");

		xpBase = levelConfig.getInt("xp base amount", 80, "The amount of xp it initially costs to level up");
		xpInc = levelConfig.getInt("xp increase", 20, "The amount that is added on to the xp cost every time the TARDIS levels up");

		maxEnergy = levelConfig.getInt("Max energy", 1000, "The base maximum energy");
		maxEnergyInc = levelConfig.getInt("Max energy increase", 1000, "How much a level of energy increases the max amount of energy");
		maxNumRooms = Math.max(1,levelConfig.getInt("Max rooms", 6, "The base maximum number of rooms"));
		maxNumRoomsInc = Math.max(1,levelConfig.getInt("Max rooms increase", 6, "How much a level of max rooms increases the maximum number of rooms"));
		energyPerSecond = levelConfig.getInt("Energy rate", 1, "The base amount of energy the TARDIS generates per second");
		energyPerSecondInc = levelConfig.getInt("Energy rate increase", 1, "How much a level of energy rate increases the amount of energy per second");

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
	 * ROUNDEL CONFIG
	 */
	public static ConfigFile	roundelConfig;
	public static int			euRatio						= 4;
	public static int			rfBase						= 50000;
	public static int			rfInc						= 50000;
	public static int			rfPerT						= 4098;
	public static int			maxFlu						= 32000;
	public static int			numTanks					= 5;
	public static int			numInvs						= 30;
	public static int			maxEachAspect				= 16;
	public static int			maxEachAspectInc			= 16;
	public static int			numAspects					= 16;

	public static double		nanogeneRange				= 36;
	public static int			nanogeneTimer				= 10;
	public static int			nanogeneCost				= 1;
	public static int			nanogeneHealAmount			= 2;
	public static boolean		nanogeneFeed				= true;

	public static int			restorationFieldTimer 		= 120;
	public static int			restorationFieldPercentage 	= 70;
	public static int			restorationFieldCost 		= 5;
	public static double		restorationFieldRange		= 16;


	public static int			maxComponents				= 6;

	private static void refreshRoundelConfig()
	{
		if (roundelConfig == null) roundelConfig = TardisMod.configHandler.registerConfigNeeder("roundels");
		maxComponents = rfBase = roundelConfig.getInt("RF storage base", 50000, "The amount of RF that can be stored when a TARDIS is level 0");
		euRatio = roundelConfig.getInt("RF->EU Conversion ratio", 4, "How many RF make up a single unit of EU");
		rfInc = roundelConfig.getInt("RF storage increase per level", 50000, "The extra amount of storage which is added every time the TARDIS levels up");
		rfPerT = roundelConfig.getInt("RF output per tick", 4098, "The amount of RF which the TARDIS can output per tick");
		maxFlu = roundelConfig.getInt("Internal tanks - Max mb", 16000, "The amount of millibuckets of fluid that can be stored for each internal tank");
		numTanks = roundelConfig.getInt("Internal tanks - Number", 6, "The number of internal tanks that the TARDIS has");
		numInvs = roundelConfig.getInt("Internal inventory - Slots", 30, "The number of item inventory slots that the TARDIS has");
		numAspects = roundelConfig.getInt("Internal TC - Num aspects", 32, "The number of thaumcraft aspects which can be stored in the TARDIS's cabling");
		maxEachAspect = roundelConfig.getInt("Internal TC - Max aspect", 32, "The maximum amount of each thaumcraft aspect that can be stored");
		maxEachAspectInc = roundelConfig.getInt("Internal TC - Max aspect inc", 16, "The amount of aspect storage gained per level");

		nanogeneRange = Math.pow(roundelConfig.getDouble("Nanogene - Range", 6, "The range at which nanogenes can heal"), 2);
		nanogeneTimer = roundelConfig.getInt("Nanogene - Timer", 10, "The number of ticks between each nanogene healing pulse");
		nanogeneCost = roundelConfig.getInt("Nanogene - Cost", 1, "The amount of Artron energy used up each time a nanogene heals");
		nanogeneHealAmount = roundelConfig.getInt("Nanogene - Heal amount", 2, "The amount of health a nanogene can restore per pulse");
		nanogeneFeed = roundelConfig.getBoolean("Nanogene - Feeds", true, "Whether nanogenes should also feed players as well as heal");

		restorationFieldTimer = roundelConfig.getInt("Restoration Field - Timer", 120, "The number of ticks between each restoration field pulse");
		restorationFieldPercentage = roundelConfig.getInt("Restoration Field - Repair percentage", 70, "What percentage of the tool will be repaired (1 - 100)");
		restorationFieldCost = roundelConfig.getInt("Restoration Field - Cost", 5, "The amount of Artron energy used up each time a tool gets healed by 1 point");
		restorationFieldRange = Math.pow(roundelConfig.getDouble("Restoration Field - Range", 4, "The range at which the restoration field has effect"), 2);

		maxComponents = roundelConfig.getInt("Maximum components", 6, "The number of cable interfaces/components per roundel/landing pad");
	}

	/*
	 * CRAFTING CONFIG
	 */

	public static ConfigFile	craftConfig;
	public static int			maxLabSpeed			= 5;
	public static boolean		kontronCraftable	= false;
	public static boolean		keyCraftable		= true;
	public static boolean		keyReqKontron		= true;
	public static int			numDirtRecipe		= 2;

	private static void refreshCraftingConfig()
	{
		if (craftConfig == null) craftConfig = TardisMod.configHandler.registerConfigNeeder("crafting");
		maxLabSpeed = craftConfig.getInt("Max lab speed", 5, "The maximum speed which the lab can operate at (artron/tick)");
		kontronCraftable = craftConfig.getBoolean("Craftable - Kontron", false, "If true, a standard crafting recipe is added for the kontron crystal");
		keyCraftable = craftConfig.getBoolean("Craftable - Key", true, "True if the key is craftable.", "False if they can only be spawned");
		keyReqKontron = craftConfig.getBoolean("Key requires kontron", true, "True if the key requires a Kontron crystal to craft");
		numDirtRecipe = MathHelper.clamp(craftConfig.getInt("Temporal dirt production", 2, "Number of temporal dirt per recipe instance", "Min 1, max 64"), 1, 64);
	}

	/*
	 * TOOLS CONFIG
	 */

	public static ConfigFile	toolConfig;
	public static int			batMaxEnergyPerLevel	= 100;
	public static int			batEnergyPerLevel		= 1;
	public static int			batTicksPerEnergy		= 20;
	public static boolean		batNeedsJumpStart		= true;

	public static int			gravMaxDistance			= 64;
	public static int			gravScanCeilingInterval	= 20;
	public static int			gravScanPlayerInterval	= 2;
	public static double		gravMovePerTick			= 0.25;

	public static double		dirtTickMult			= 1;
	public static double		dirtBoneChance			= 0.3;

	public static double		tempAccTickMult			= 0.2;

	public static int			decoratorRange			= 6;
	public static boolean		visibleSchema			= false;
	public static boolean		visibleForceField		= false;
	public static boolean		lightBlocks				= false;

	private static void refreshToolsConfig()
	{
		if (toolConfig == null) toolConfig = TardisMod.configHandler.registerConfigNeeder("toolsAndBlocks");
		batMaxEnergyPerLevel = toolConfig.getInt("Battery - Max energy per level", 100, "The amount of max energy that is gained per level");
		batEnergyPerLevel = toolConfig.getInt("Battery - Energy per level", 1, "The amount of energy per pulse that is gained per level");
		batTicksPerEnergy = toolConfig.getInt("Battery - Ticks per energy", 20, "The number of ticks between each energy pulse");
		batNeedsJumpStart = toolConfig.getBoolean("Battery - Needs jump start", true, "True if the battery needs to be jumpstarted from inside a TARDIS");

		gravMaxDistance = toolConfig.getInt("Grav Lift - max distance", 64);
		gravScanCeilingInterval = toolConfig.getInt("Grav Lift - interval for ceiling scan", 20);
		gravScanPlayerInterval = toolConfig.getInt("Grav Lift - interval for player scan", 2);
		gravMovePerTick = toolConfig.getDouble("Grav Lift - move per tick", 0.25);

		dirtTickMult = toolConfig.getDouble("Dirt block - Tick mult", 0.5, "The number the tick rate of the plant is multipied by to work out how often the dirt block applies a dirt tick",
				"e.g. A mult of 0.5 means a plant which would normally get a tick every 10 ticks will get an extra growth tick every 5 ticks");
		tempAccTickMult = toolConfig.getDouble("Temporal Accelerator - Tick mult", 0.2, "The multiplier which decides when the tile above it gets an extra update tick.",
				"e.g. A mult of 0.2 means a block which has a tick rate of 10, will get an extra update tick every 2 ticks.");
		dirtBoneChance = toolConfig.getDouble("Dirt block - Bonemeal chance", 0.25, "The chance for a TARDIS dirt block to apply a bonemeal affect to the plant (as well as a growth tick)");

		decoratorRange = toolConfig.getInt("Decorator - Range", 6, "The maximum range the decorator can work to");

		visibleSchema = toolConfig.getConfigItem(new ConfigItem("Visibility - Schema", CType.BOOLEAN, false, "Should schema boundaries be visible (clientside config)")).getBoolean();
		visibleForceField = toolConfig.getBoolean("Visibility - forcefields", false, "Should the forcefields be visible or not");
		lightBlocks = toolConfig.getBoolean("Visibility - lit up blocks", false, "Should most blocks give off light");
	}

	/*
	 * MECAHNICS CONFIG
	 */
	public static ConfigFile	mechConfig;

	public static boolean		deathTransmat			= true;
	public static boolean		deathTransmatLive		= true;
	public static double		transmatExitDist		= 2;
	public static int			kontronRarity			= 4;
	public static boolean		keyOnFirstJoin			= true;
	public static boolean		deleteDisconnected		= true;
	public static boolean		tardisLoaded			= true;
	public static boolean		keyInHand				= true;
	public static int			lockSoundDelay			= 40;
	public static boolean		loadWhenOffline			= true;
	public static boolean		enableLinking			= true;
	public static boolean		deleteAllOwnerOnly		= false;
	public static boolean		enableTardisMobSpawning	= false;
	public static String[]		dimUpgradesIds			;


	private static void refreshMechanicsConfig()
	{
		if (mechConfig == null) mechConfig = TardisMod.configHandler.registerConfigNeeder("mechanics");
		keyOnFirstJoin = mechConfig.getBoolean("Key - On first join", false, "If true, all players get a new key when they first join");
		kontronRarity = mechConfig.getInt("Dungeon Loot - Kontron Rarity", 20, "The higher this value, the more likely you are to find kontron crystals in chests");
		deleteDisconnected = mechConfig.getBoolean("Rooms - Delete disconnected", true, "Delete rooms which aren't connected to the console room when the connecting room is deleted");
		deathTransmatLive = mechConfig.getBoolean("Transmat - Death - Live after", true, "Whether you should live after being saved from death");
		deathTransmat = mechConfig.getBoolean("Transmat - Death", true, "If true, when you die within range of your TARDIS you will be transmatted");
		transmatExitDist = mechConfig.getDouble("Transmat - Exit distance", 2, "The distance from the transmat point within which you will be transmatted out of the TARDIS");
		keyInHand = mechConfig.getConfigItem(new ConfigItem("Key - In hand", CType.BOOLEAN, true, "Does a player need to have the key in hand to get through a locked TARDIS door")).getBoolean();
		lockSoundDelay = mechConfig.getInt("Lock sound delay", 40, "Amount of ticks between lock sounds being allowed to play", "20 ticks = 1 second");
		loadWhenOffline = mechConfig.getBoolean("Chunkload - Offline", true, "Chunkload when player is offline");
		enableLinking = mechConfig.getBoolean("Enable screwdriver linking mode", true, "Enable the linking mode on the sonic","Note: WIP. Some linking features are still buggy");
		deleteAllOwnerOnly = mechConfig.getBoolean("Delete all rooms owner only", false, "Whether or not the delete all rooms button should be owner only");
		enableTardisMobSpawning= mechConfig.getBoolean("Enable mob spawning", false, "Whether or not mobs are able to spawn in the TARDIS dimension");

		String dimIds = mechConfig.getString("Dimension ID's locked by upgrades", "", "Put all the dimension ID's you want to lock using an engine upgrade here, seperating each ID with a comma", "Like this:1,2,3,4").trim();
		dimUpgradesIds = dimIds.split(",");


	}

	/*
	 * MOD CONFIG
	 */

	public static ConfigFile			modConfig;
	public static double				tardisVol				= 1;
	public static int					exteriorGenChunksRad	= 8;
	public static int					exteriorGenChunksPT		= 1;
	public static int					exteriorGenChunksTR		= 4;
	public static TardisOutput.Priority	priorityLevel			= TardisOutput.Priority.INFO;
	public static int					providerID				= 54;
	public static int					consoleBiomeID			= 42;


	private static void refreshModConfig()
	{
		if (modConfig == null) modConfig = TardisMod.configHandler.registerConfigNeeder("TardisMod");
		priorityLevel = TardisOutput.getPriority(modConfig.getInt("Debug level", TardisOutput.Priority.INFO.ordinal(), "Sets the level of debug output"));
		providerID = modConfig.getInt("Dimension provider ID", 54, "The id of the dimension provider");
		tardisLoaded = modConfig.getBoolean("Dimension always loaded", true, "Should the TARDIS dimensions always be loaded");
		tardisVol = modConfig.getDouble("Volume", 1, "How loud should Tardis Mod sounds be (1.0 = full volume, 0.0 = no volume)");
		exteriorGenChunksRad = MathHelper.clamp(modConfig.getInt("exterior chunk gen radius", 8, "Radius in chunks for the exterior to generate while landing"), -1, 10);
		exteriorGenChunksPT = MathHelper.clamp(modConfig.getInt("exterior chunk gen per pulse", 1, "Number of chunks for the exterior to generate per pulse"), 1, 20);
		exteriorGenChunksTR = MathHelper.clamp(modConfig.getInt("exterior chunk gen ticks per pulse", 4, "Number of ticks between chunk generation pulses"), 1, 20);
		consoleBiomeID = modConfig.getInt("Console Room Dimension ID", 42, "The id of the biome which is used in the console room");


	}

	/*
	 * INITIALIZER
	 */

	public static void refreshConfigs()
	{
		refreshLevelConfig();
		refreshRoundelConfig();
		refreshCraftingConfig();
		refreshToolsConfig();
		refreshMechanicsConfig();
		refreshModConfig();
	}
}
