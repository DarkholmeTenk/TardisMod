package tardis;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.IAppEngApi;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import tardis.common.TardisProxy;
import tardis.common.blocks.BatteryBlock;
import tardis.common.blocks.BatteryBlockItemBlock;
import tardis.common.blocks.ForceFieldBlock;
import tardis.common.blocks.ForceFieldItemBlock;
import tardis.common.blocks.GravityLiftBlock;
import tardis.common.blocks.GravityLiftItemBlock;
import tardis.common.blocks.LabBlock;
import tardis.common.blocks.LandingPadBlock;
import tardis.common.blocks.AbstractBlock;
import tardis.common.blocks.TardisBlock;
import tardis.common.blocks.ComponentBlock;
import tardis.common.blocks.ComponentItemBlock;
import tardis.common.blocks.ConsoleBlock;
import tardis.common.blocks.CoreBlock;
import tardis.common.blocks.DebugBlock;
import tardis.common.blocks.DecoBlock;
import tardis.common.blocks.DecoDarkItemBlock;
import tardis.common.blocks.DecoItemBlock;
import tardis.common.blocks.EngineBlock;
import tardis.common.blocks.InternalDoorBlock;
import tardis.common.blocks.InternalDoorItemBlock;
import tardis.common.blocks.SchemaBlock;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.blocks.SchemaComponentItemBlock;
import tardis.common.blocks.SchemaCoreBlock;
import tardis.common.blocks.SlabBlock;
import tardis.common.blocks.SlabItemBlock;
import tardis.common.blocks.StairBlock;
import tardis.common.blocks.TopBlock;
import tardis.common.command.CommandRegister;
import tardis.common.core.DimensionEventHandler;
import tardis.common.core.Helper;
import tardis.common.core.ConfigFile;
import tardis.common.core.ConfigHandler;
import tardis.common.core.CreativeTab;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOutput;
import tardis.common.core.TardisOwnershipRegistry;
import tardis.common.core.TardisTeleporter;
import tardis.common.dimension.ChunkLoadingManager;
import tardis.common.dimension.TardisDimensionHandler;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.items.AbstractItem;
import tardis.common.items.ComponentItem;
import tardis.common.items.CraftingComponentItem;
import tardis.common.items.KeyItem;
import tardis.common.items.SchemaItem;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.BatteryTileEntity;
import tardis.common.tileents.GravityLiftTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.LandingPadTileEntity;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.SchemaCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
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
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="TardisMod",name="Tardis Mod",version="0.045",dependencies="required-after:FML; after:appliedenergistics2; after:Waila")
public class TardisMod
{
	@Instance
	public static TardisMod i;
	public static boolean inited = false;
	
	@SidedProxy(clientSide="tardis.client.TardisClientProxy", serverSide="tardis.common.TardisProxy")
	public static TardisProxy proxy;
	public static DimensionEventHandler dimEventHandler = new DimensionEventHandler();
	public static TardisPacketHandler packetHandler = new TardisPacketHandler();
	public static FMLEventChannel networkChannel;
	
	public static IAppEngApi aeAPI = null;
	
	public static ConfigFile modConfig;
	public static ConfigFile blockConfig;
	public static ConfigFile itemConfig;
	
	public static TardisTeleporter teleporter = null;
	public static ConfigHandler configHandler;
	public static TardisDimensionHandler otherDims;
	public static TardisDimensionRegistry dimReg;
	public static TardisOwnershipRegistry plReg;
	public static ChunkLoadingManager chunkManager;
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
	public static SlabBlock	  slabBlock;
	
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
		
		modConfig   = configHandler.getConfigFile("Mod");
		int prioLevel = Helper.clamp(modConfig.getInt("Debug Level", priorityLevel.ordinal()),0,TardisOutput.Priority.values().length);
		priorityLevel = TardisOutput.Priority.values()[prioLevel];
		
		providerID		= modConfig.getInt("Dimension Provider ID", 54);
		tardisLoaded	= modConfig.getBoolean("Dimension always loaded", true);
		keyInHand		= modConfig.getBoolean("Key needs to be in hand", true);
		registerNetwork();
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
	
	private void registerNetwork()
	{
		networkChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("tardis");
		networkChannel.register(packetHandler);
	}
	
	@EventHandler
	public void doingInit(FMLInitializationEvent event)
	{
		proxy.init();
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
		tardisBlock = new TardisBlock();
		GameRegistry.registerBlock(tardisBlock,tardisBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisTileEntity.class, tardisBlock.getUnlocalizedName());
		
		tardisTopBlock = new TopBlock();
		GameRegistry.registerBlock(tardisTopBlock,tardisTopBlock.getUnlocalizedName());
		
		tardisCoreBlock = new CoreBlock();
		GameRegistry.registerBlock(tardisCoreBlock,tardisCoreBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(CoreTileEntity.class, tardisCoreBlock.getUnlocalizedName());
		
		tardisConsoleBlock = new ConsoleBlock();
		GameRegistry.registerBlock(tardisConsoleBlock, tardisConsoleBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(ConsoleTileEntity.class,tardisConsoleBlock.getUnlocalizedName());
		
		tardisEngineBlock = new EngineBlock();
		GameRegistry.registerBlock(tardisEngineBlock, tardisEngineBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(EngineTileEntity.class, tardisEngineBlock.getUnlocalizedName());
		
		componentBlock = new ComponentBlock();
		GameRegistry.registerBlock(componentBlock,ComponentItemBlock.class,componentBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(ComponentTileEntity.class, componentBlock.getUnlocalizedName());
		
		internalDoorBlock = new InternalDoorBlock();
		GameRegistry.registerBlock(internalDoorBlock,InternalDoorItemBlock.class,internalDoorBlock.getUnlocalizedName());
		
		decoBlock = new DecoBlock(true);
		GameRegistry.registerBlock(decoBlock, DecoItemBlock.class, decoBlock.getUnlocalizedName());
		
		darkDecoBlock = new DecoBlock(false);
		GameRegistry.registerBlock(darkDecoBlock, DecoDarkItemBlock.class, decoBlock.getUnlocalizedName()+"Dark");
		
		stairBlock = new StairBlock();
		GameRegistry.registerBlock(stairBlock,stairBlock.getUnlocalizedName());
		
		debugBlock = new DebugBlock();
		GameRegistry.registerBlock(debugBlock, debugBlock.getUnlocalizedName());
		
		boolean visibleSchema = modConfig.getBoolean("Visible schematic boundaries", false);
		schemaBlock = new SchemaBlock(visibleSchema);
		GameRegistry.registerBlock(schemaBlock, schemaBlock.getUnlocalizedName());
		
		schemaCoreBlock = new SchemaCoreBlock(visibleSchema);
		GameRegistry.registerBlock(schemaCoreBlock,schemaCoreBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(SchemaCoreTileEntity.class, schemaCoreBlock.getUnlocalizedName());
		
		schemaComponentBlock = new SchemaComponentBlock();
		GameRegistry.registerBlock(schemaComponentBlock, SchemaComponentItemBlock.class, schemaComponentBlock.getUnlocalizedName());
		
		slabBlock = new SlabBlock();
		GameRegistry.registerBlock(slabBlock,SlabItemBlock.class,slabBlock.getUnlocalizedName());
		
		landingPad = new LandingPadBlock();
		GameRegistry.registerBlock(landingPad, landingPad.getUnlocalizedName());
		GameRegistry.registerTileEntity(LandingPadTileEntity.class, landingPad.getUnlocalizedName());
		
		labBlock	= new LabBlock();
		GameRegistry.registerBlock(labBlock, labBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(LabTileEntity.class, labBlock.getUnlocalizedName());
		
		gravityLift = new GravityLiftBlock();
		GameRegistry.registerBlock(gravityLift, GravityLiftItemBlock.class, gravityLift.getUnlocalizedName());
		GameRegistry.registerTileEntity(GravityLiftTileEntity.class, gravityLift.getUnlocalizedName());
		
		forcefield = new ForceFieldBlock();
		GameRegistry.registerBlock(forcefield, ForceFieldItemBlock.class, forcefield.getUnlocalizedName());
		
		battery = new BatteryBlock();
		GameRegistry.registerBlock(battery, BatteryBlockItemBlock.class, battery.getUnlocalizedName());
		GameRegistry.registerTileEntity(BatteryTileEntity.class, battery.getUnlocalizedName());
	}
	
	private void initItems()
	{
		schemaItem = new SchemaItem();
		GameRegistry.registerItem(schemaItem, schemaItem.getUnlocalizedName());
		
		screwItem = new SonicScrewdriverItem();
		GameRegistry.registerItem(screwItem, screwItem.getUnlocalizedName());
		
		keyItem = new KeyItem();
		GameRegistry.registerItem(keyItem, keyItem.getUnlocalizedName());
		
		componentItem = new ComponentItem();
		GameRegistry.registerItem(componentItem, componentItem.getUnlocalizedName());
		
		craftingComponentItem = new CraftingComponentItem();
		GameRegistry.registerItem(craftingComponentItem, craftingComponentItem.getUnlocalizedName());
		
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
		
		if(chunkManager != null)
		{
			MinecraftForge.EVENT_BUS.unregister(chunkManager);
			FMLCommonHandler.instance().bus().unregister(chunkManager);
		}
		chunkManager = new ChunkLoadingManager();
		MinecraftForge.EVENT_BUS.register(chunkManager);
		FMLCommonHandler.instance().bus().register(chunkManager);
		ForgeChunkManager.setForcedChunkLoadingCallback(this, chunkManager);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		dimReg = TardisDimensionRegistry.load();
		dimReg.registerDims();
		FMLCommonHandler.instance().bus().register(dimReg);
		MinecraftForge.EVENT_BUS.register(dimReg);
		plReg  = TardisOwnershipRegistry.load();
		TardisOwnershipRegistry.save();
		teleporter = new TardisTeleporter(event.getServer().worldServerForDimension(0));
		CommandRegister.registerCommands(event);
	}
}
