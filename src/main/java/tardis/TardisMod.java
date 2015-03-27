package tardis;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.DarkcoreTeleporter;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItem;
import io.darkcraft.darkcore.mod.helpers.MathHelper;

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
import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.ForceFieldBlock;
import tardis.common.blocks.GravityLiftBlock;
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
import tardis.common.core.ConfigFile;
import tardis.common.core.ConfigHandler;
import tardis.common.core.CreativeTab;
import tardis.common.core.DimensionEventHandler;
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

@Mod(modid="TardisMod",name="Tardis Mod",version="0.06",dependencies="required-after:FML; required-after:darkcore; after:appliedenergistics2; after:Waila")
public class TardisMod
{
	@Instance
	public static TardisMod i;
	public static final String modName = "TardisMod";
	public static boolean inited = false;
	
	@SidedProxy(clientSide="tardis.client.TardisClientProxy", serverSide="tardis.common.TardisProxy")
	public static TardisProxy proxy;
	public static DimensionEventHandler dimEventHandler = new DimensionEventHandler();
	
	public static IAppEngApi aeAPI = null;
	
	public static ConfigFile modConfig;
	public static ConfigFile blockConfig;
	public static ConfigFile itemConfig;
	
	public static DarkcoreTeleporter teleporter = null;
	public static ConfigHandler configHandler;
	public static TardisDimensionHandler otherDims;
	public static TardisDimensionRegistry dimReg;
	public static TardisOwnershipRegistry plReg;
	public static CreativeTab tab = null;
	
	public static TardisOutput.Priority priorityLevel = TardisOutput.Priority.INFO;
	public static int providerID = 54;
	public static boolean tardisLoaded = true;
	public static boolean keyInHand = true;
	
	public static AbstractBlock tardisBlock;
	public static AbstractBlock tardisTopBlock;
	public static AbstractBlock tardisCoreBlock;
	public static AbstractBlock tardisConsoleBlock;
	public static AbstractBlock tardisEngineBlock;
	public static AbstractBlock componentBlock;
	public static AbstractBlock internalDoorBlock;
	public static AbstractBlock decoBlock;
	public static AbstractBlock darkDecoBlock;
	public static AbstractBlock schemaBlock;
	public static AbstractBlock schemaCoreBlock;
	public static AbstractBlock schemaComponentBlock;
	public static AbstractBlock debugBlock;
	public static AbstractBlock landingPad;
	public static AbstractBlock gravityLift;
	public static AbstractBlock forcefield;
	public static AbstractBlock battery;
	public static StairBlock	  stairBlock;
	public static AbstractBlock	  slabBlock;
	
	public static LabBlock	labBlock;
	
	public static AbstractItem schemaItem;
	public static AbstractItem componentItem;
	public static CraftingComponentItem craftingComponentItem;
	public static KeyItem keyItem;
	public static SonicScrewdriverItem screwItem;
	
	public static float tardisVol = 1f;
	public static boolean deathTransmatLive		= true;
	public static int xpBase	= 80;
	public static int xpInc		= 20;
	public static int rfBase	= 50000;
	public static int rfInc		= 50000;
	public static int rfPerT	= 4098;
	public static int maxFlu	= 32000;
	public static int numTanks	= 5;
	public static int numInvs	= 30;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException
	{
		configHandler = new ConfigHandler(event.getModConfigurationDirectory());
		configHandler.getSchemas();
		tab = new CreativeTab();
		DarkcoreMod.registerCreativeTab(modName, tab);
		
		modConfig   = configHandler.getConfigFile("Mod");
		int prioLevel = MathHelper.clamp(modConfig.getInt("Debug Level", priorityLevel.ordinal()),0,TardisOutput.Priority.values().length);
		priorityLevel = TardisOutput.Priority.values()[prioLevel];
		
		providerID		= modConfig.getInt("Dimension Provider ID", 54);
		tardisLoaded	= modConfig.getBoolean("Dimension always loaded", true);
		keyInHand		= modConfig.getBoolean("Key needs to be in hand", true);
		tardisVol		= (float) modConfig.getDouble("Tardis Volume", 1);
		
		xpBase			= modConfig.getInt("xp base amount", 80);
		xpInc			= modConfig.getInt("xp increase", 20);
		rfBase			= modConfig.getInt("base RF storage", 50000);
		rfInc			= modConfig.getInt("RF storage increase per level", 50000);
		rfPerT			= modConfig.getInt("RF output per tick", 4098);
		maxFlu			= modConfig.getInt("Max mb per internal tank",16000);
		numTanks		= modConfig.getInt("Number of internal tanks", 6);
		numInvs			= modConfig.getInt("Number of internal inventory slots", 30);
		deathTransmatLive	= modConfig.getBoolean("Live after death transmat", true);
		DimensionManager.registerProviderType(providerID, TardisWorldProvider.class, tardisLoaded);
		blockConfig = configHandler.getConfigFile("Blocks");
		initBlocks();
		
		itemConfig = configHandler.getConfigFile("Items");
		initItems();
		
		//MinecraftForge.EVENT_BUS.register(new SoundHandler());
		
		proxy.postAssignment();
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
		
		//darkDecoBlock = new DecoBlock(false);
		//GameRegistry.registerBlock(darkDecoBlock, DecoDarkItemBlock.class, darkDecoBlock.getUnlocalizedName()+"Dark");
		
		stairBlock = new StairBlock().register();
		
		debugBlock = new DebugBlock().register();
		
		boolean visibleSchema = modConfig.getBoolean("Visible schematic boundaries", false);
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
		if(otherDims != null)
			MinecraftForge.EVENT_BUS.unregister(otherDims);
		otherDims = new TardisDimensionHandler();
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
}
