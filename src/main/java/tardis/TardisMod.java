package tardis;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.DarkcoreTeleporter;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.config.CType;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.config.ConfigHandler;
import io.darkcraft.darkcore.mod.config.ConfigHandlerFactory;
import io.darkcraft.darkcore.mod.config.ConfigItem;
import io.darkcraft.darkcore.mod.interfaces.IConfigHandlerMod;

import java.io.IOException;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import tardis.common.TardisProxy;
import tardis.common.blocks.BatteryBlock;
import tardis.common.blocks.ComponentBlock;
import tardis.common.blocks.ConsoleBlock;
import tardis.common.blocks.CoreBlock;
import tardis.common.blocks.DebugBlock;
import tardis.common.blocks.DecoBlock;
import tardis.common.blocks.DecoTransBlock;
import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.ForceFieldBlock;
import tardis.common.blocks.GravityLiftBlock;
import tardis.common.blocks.InteriorDirtBlock;
import tardis.common.blocks.InternalDoorBlock;
import tardis.common.blocks.LabBlock;
import tardis.common.blocks.LandingPadBlock;
import tardis.common.blocks.SchemaBlock;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.blocks.SchemaCoreBlock;
import tardis.common.blocks.SlabBlock;
import tardis.common.blocks.StairBlock;
import tardis.common.blocks.TardisBlock;
import tardis.common.blocks.TopBlock;
import tardis.common.command.CommandRegister;
import tardis.common.core.CreativeTab;
import tardis.common.core.DimensionEventHandler;
import tardis.common.core.Helper;
import tardis.common.core.SchemaHandler;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOutput;
import tardis.common.core.TardisOwnershipRegistry;
import tardis.common.dimension.TardisDimensionHandler;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.items.ComponentItem;
import tardis.common.items.CraftingComponentItem;
import tardis.common.items.KeyItem;
import tardis.common.items.SchemaItem;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.BatteryTileEntity;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.GravityLiftTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.components.AbstractComponent;
import appeng.api.AEApi;
import appeng.api.IAppEngApi;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
		modid = "TardisMod",
		name = "Tardis Mod",
		version = "0.85",
		dependencies = "required-after:FML; required-after:darkcore; required-after:CoFHCore; after:appliedenergistics2; after:Waila")
public class TardisMod implements IConfigHandlerMod
{
	@Instance
	public static TardisMod					i;
	public static final String				modName				= "TardisMod";
	public static boolean					inited				= false;

	@SidedProxy(clientSide = "tardis.client.TardisClientProxy", serverSide = "tardis.common.TardisProxy")
	public static TardisProxy				proxy;
	public static DimensionEventHandler		dimEventHandler		= new DimensionEventHandler();

	public static IAppEngApi				aeAPI				= null;

	public static ConfigHandler				configHandler;
	public static SchemaHandler				schemaHandler;
	public static ConfigFile				modConfig;
	public static ConfigFile				miscConfig;

	public static DarkcoreTeleporter		teleporter			= null;
	public static TardisDimensionHandler	otherDims;
	public static TardisDimensionRegistry	dimReg;
	public static TardisOwnershipRegistry	plReg;
	public static CreativeTab				tab					= null;
	public static CreativeTab				cTab				= null;

	public static TardisOutput.Priority		priorityLevel		= TardisOutput.Priority.INFO;
	public static int						providerID			= 54;
	public static boolean					tardisLoaded		= true;
	public static boolean					keyInHand			= true;

	public static AbstractBlock				tardisBlock;
	public static AbstractBlock				tardisTopBlock;
	public static AbstractBlock				tardisCoreBlock;
	public static AbstractBlock				tardisConsoleBlock;
	public static AbstractBlock				tardisEngineBlock;
	public static AbstractBlock				componentBlock;
	public static AbstractBlock				internalDoorBlock;
	public static AbstractBlock				decoBlock;
	public static AbstractBlock				darkDecoBlock;
	public static AbstractBlock				decoTransBlock;
	public static AbstractBlock				schemaBlock;
	public static AbstractBlock				schemaCoreBlock;
	public static AbstractBlock				schemaComponentBlock;
	public static AbstractBlock				debugBlock;
	public static AbstractBlock				landingPad;
	public static AbstractBlock				gravityLift;
	public static AbstractBlock				forcefield;
	public static AbstractBlock				battery;
	public static StairBlock				stairBlock;
	public static AbstractBlock				slabBlock;
	public static AbstractBlock				interiorDirtBlock;

	public static LabBlock					labBlock;

	public static AbstractItem				schemaItem;
	public static AbstractItem				componentItem;
	public static CraftingComponentItem		craftingComponentItem;
	public static KeyItem					keyItem;
	public static SonicScrewdriverItem		screwItem;

	public static double					tardisVol			= 1;
	public static boolean					deathTransmatLive	= true;
	public static boolean					visibleSchema		= false;
	public static int						xpBase				= 80;
	public static int						xpInc				= 20;
	public static int						rfBase				= 50000;
	public static int						rfInc				= 50000;
	public static int						rfPerT				= 4098;
	public static int						maxFlu				= 32000;
	public static int						numTanks			= 5;
	public static int						numInvs				= 30;
	public static boolean					keyCraftable		= true;
	public static boolean					keyReqKontron		= true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException
	{
		configHandler = ConfigHandlerFactory.getConfigHandler(this);
		schemaHandler = new SchemaHandler(configHandler);
		schemaHandler.getSchemas();
		tab = new CreativeTab("TardisModTab");
		cTab = new CreativeTab("TardisModCraftableTab");
		DarkcoreMod.registerCreativeTab(modName, tab);

		modConfig = configHandler.getModConfig();
		miscConfig = configHandler.registerConfigNeeder("Misc");
		refreshConfigs();

		
		deathTransmatLive = modConfig.getBoolean("Live after death transmat", true);
		DimensionManager.registerProviderType(providerID, TardisWorldProvider.class, tardisLoaded);
		initBlocks();
		initItems();

		// MinecraftForge.EVENT_BUS.register(new SoundHandler());

		proxy.postAssignment();
	}
	
	public static void refreshConfigs()
	{
		int outputPriority = modConfig.getConfigItem(new ConfigItem("Debug level",CType.INT,TardisOutput.Priority.INFO.ordinal(),
				"Sets the level of debug output")).getInt();
		priorityLevel = TardisOutput.getPriority(outputPriority);
		
		providerID = modConfig.getConfigItem(new ConfigItem("Dimension provider ID", CType.INT, 54,
				"The id of the dimension provider")).getInt();
		
		tardisLoaded = modConfig.getConfigItem(new ConfigItem("Dimension always loaded", CType.BOOLEAN, true,
				"Should the TARDIS dimensions always be loaded")).getBoolean();
		
		keyInHand = modConfig.getConfigItem(new ConfigItem("Key in hand", CType.BOOLEAN, true,
				"Does a player need to have the key in hand to get through a locked TARDIS door")).getBoolean();
		
		keyCraftable = modConfig.getBoolean("keyCraftable", true, "True if the key is craftable.","False if they can only be spawned");
		
		keyReqKontron = modConfig.getBoolean("keyRequiresKontron", true, "True if the key requires a Kontron crystal to craft");
		
		tardisVol = modConfig.getConfigItem(new ConfigItem("Volume", CType.DOUBLE, 1,
				"How loud should Tardis Mod sounds be (1.0 = full volume, 0.0 = no volume)")).getDouble();
		
		visibleSchema = modConfig.getConfigItem(new ConfigItem("Visible Schema", CType.BOOLEAN, false,
				"Should schema boundaries be visible (clientside config)")).getBoolean();
		
		xpBase = miscConfig.getInt("xp base amount", 80,
				"The amount of xp it initially costs to level up");
		xpInc = miscConfig.getInt("xp increase", 20,
				"The amount that is added on to the xp cost every time the TARDIS levels up");
		rfBase = miscConfig.getInt("base RF storage", 50000,
				"The amount of RF that can be stored when a TARDIS is level 0");
		rfInc = miscConfig.getInt("RF storage increase per level", 50000,
				"The extra amount of storage which is added every time the TARDIS levels up");
		rfPerT = miscConfig.getInt("RF output per tick", 4098,
				"The amount of RF which the TARDIS can output per tick");
		maxFlu = miscConfig.getInt("Max mb per internal tank", 16000,
				"The amount of millibuckets of fluid that can be stored for each internal tank");
		numTanks = miscConfig.getInt("Number of internal tanks", 6,
				"The number of internal tanks that the TARDIS has");
		numInvs = miscConfig.getInt("Number of internal inventory slots", 30,
				"The number of item inventory slots that the TARDIS has");
		
		AbstractComponent.refreshConfigs();
		BatteryTileEntity.refreshConfigs();
		ComponentTileEntity.refreshConfigs();
		CoreTileEntity.refreshConfigs();
		GravityLiftTileEntity.refreshConfigs();
		LabTileEntity.refreshConfigs();
		InteriorDirtBlock.refreshConfigs();
		
		TardisDimensionHandler.refreshConfigs();
	}

	@EventHandler
	public void doingInit(FMLInitializationEvent event)
	{
		proxy.init();
		TardisPacketHandler.registerHandlers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		System.out.println("POSTINIT");
		aeAPI = AEApi.instance();
		initRecipes();
		FMLCommonHandler.instance().bus().register(dimEventHandler);
		MinecraftForge.EVENT_BUS.register(dimEventHandler);
		inited = true;
	}

	private void initBlocks()
	{
		tardisBlock = new TardisBlock().register();
		
		tardisTopBlock = new TopBlock().register();
		
		tardisCoreBlock = new CoreBlock().register();
		
		tardisConsoleBlock = new ConsoleBlock().register();
		
		tardisEngineBlock = new EngineBlock().register();
		
		componentBlock = new ComponentBlock().register();
		
		internalDoorBlock = new InternalDoorBlock().register();
		
		decoBlock = new DecoBlock(true).register();
		
		decoTransBlock = new DecoTransBlock().register();
		
		interiorDirtBlock = new InteriorDirtBlock().register();
		
		stairBlock = new StairBlock().register();
		
		debugBlock = new DebugBlock().register();
		
		schemaBlock = new SchemaBlock(visibleSchema).register();
		
		schemaCoreBlock = new SchemaCoreBlock(visibleSchema).register();
		
		schemaComponentBlock = new SchemaComponentBlock().register();
		
		slabBlock = new SlabBlock().register();
		
		landingPad = new LandingPadBlock().register();
		
		labBlock	= (LabBlock) new LabBlock().register();
		
		gravityLift = new GravityLiftBlock().register();
		
		forcefield = new ForceFieldBlock().register();
		
		battery = new BatteryBlock().register();
	}

	private void initItems()
	{
		schemaItem = new SchemaItem().register();

		screwItem = (SonicScrewdriverItem) new SonicScrewdriverItem().register();

		keyItem = (KeyItem) new KeyItem().register();

		componentItem = new ComponentItem().register();

		craftingComponentItem = (CraftingComponentItem) new CraftingComponentItem().register();

	}

	private void initRecipes()
	{
		keyItem.initRecipes();
		componentItem.initRecipes();
		labBlock.initRecipes();
		landingPad.initRecipes();
		forcefield.initRecipes();
		gravityLift.initRecipes();
		battery.initRecipes();
		craftingComponentItem.initRecipes();
	}

	@EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event)
	{
		if (otherDims != null)
			MinecraftForge.EVENT_BUS.unregister(otherDims);
		otherDims = new TardisDimensionHandler();
		dimReg = null;
		Helper.datastoreMap.clear();
		MinecraftForge.EVENT_BUS.register(otherDims);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		TardisDimensionRegistry.loadAll();
		dimReg.registerDims();
		FMLCommonHandler.instance().bus().register(dimReg);
		MinecraftForge.EVENT_BUS.register(dimReg);
		TardisOwnershipRegistry.loadAll();
		TardisOwnershipRegistry.saveAll();
		teleporter = new DarkcoreTeleporter(event.getServer().worldServerForDimension(0));
		CommandRegister.registerCommands(event);
	}

	@Override
	public String getModID()
	{
		return modName;
	}
}
