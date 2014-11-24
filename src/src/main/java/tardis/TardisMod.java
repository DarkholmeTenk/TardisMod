package tardis;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.IAppEngApi;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import tardis.common.TardisProxy;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.blocks.TardisBlock;
import tardis.common.blocks.TardisComponentBlock;
import tardis.common.blocks.TardisComponentItemBlock;
import tardis.common.blocks.TardisConsoleBlock;
import tardis.common.blocks.TardisCoreBlock;
import tardis.common.blocks.TardisDebugBlock;
import tardis.common.blocks.TardisDecoBlock;
import tardis.common.blocks.TardisDecoItemBlock;
import tardis.common.blocks.TardisEngineBlock;
import tardis.common.blocks.TardisInternalDoorBlock;
import tardis.common.blocks.TardisInternalDoorItemBlock;
import tardis.common.blocks.TardisSchemaBlock;
import tardis.common.blocks.TardisSchemaComponentBlock;
import tardis.common.blocks.TardisSchemaComponentItemBlock;
import tardis.common.blocks.TardisSchemaCoreBlock;
import tardis.common.blocks.TardisSlabBlock;
import tardis.common.blocks.TardisSlabItemBlock;
import tardis.common.blocks.TardisStairBlock;
import tardis.common.blocks.TardisTopBlock;
import tardis.common.command.TardisCommandRegister;
import tardis.common.core.Helper;
import tardis.common.core.TardisConfigFile;
import tardis.common.core.TardisConfigHandler;
import tardis.common.core.TardisCreativeTab;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOutput;
import tardis.common.core.TardisPlayerRegistry;
import tardis.common.core.TardisSoundHandler;
import tardis.common.core.TardisTeleporter;
import tardis.common.dimension.TardisChunkLoadingManager;
import tardis.common.dimension.TardisDimensionEventHandler;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.items.TardisAbstractItem;
import tardis.common.items.TardisComponentItem;
import tardis.common.items.TardisKeyItem;
import tardis.common.items.TardisSchemaItem;
import tardis.common.items.TardisSonicScrewdriverItem;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisEngineTileEntity;
import tardis.common.tileents.TardisSchemaCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="TardisMod",name="Tardis Mod",version="0.17",dependencies="required-after:FML; after:appliedenergistics2")
public class TardisMod
{
	@Instance
	public static TardisMod i;
	public static boolean inited = false;
	
	@SidedProxy(clientSide="tardis.client.TardisClientProxy", serverSide="tardis.common.TardisProxy")
	public static TardisProxy proxy;
	public static TardisDimensionEventHandler dimEventHandler = new TardisDimensionEventHandler();
	public static TardisPacketHandler packetHandler = new TardisPacketHandler();
	public static FMLEventChannel networkChannel;
	
	public static IAppEngApi aeAPI = null;
	
	public static TardisConfigFile modConfig;
	public static TardisConfigFile blockConfig;
	public static TardisConfigFile itemConfig;
	
	public static TardisTeleporter teleporter = null;
	public static TardisConfigHandler configHandler;
	public static TardisDimensionRegistry dimReg;
	public static TardisPlayerRegistry plReg;
	public static TardisChunkLoadingManager chunkManager;
	public static TardisCreativeTab tab = null;
	
	public static TardisOutput.Priority priorityLevel = TardisOutput.Priority.INFO;
	public static int providerID = 54;
	public static boolean tardisLoaded = true;
	public static boolean keyInHand = true;
	
	public static TardisAbstractBlock tardisBlock;
	public static TardisAbstractBlock tardisTopBlock;
	public static TardisAbstractBlock tardisCoreBlock;
	public static TardisAbstractBlock tardisConsoleBlock;
	public static TardisAbstractBlock tardisEngineBlock;
	public static TardisAbstractBlock componentBlock;
	public static TardisAbstractBlock internalDoorBlock;
	public static TardisAbstractBlock decoBlock;
	public static TardisAbstractBlock schemaBlock;
	public static TardisAbstractBlock schemaCoreBlock;
	public static TardisAbstractBlock schemaComponentBlock;
	public static TardisAbstractBlock debugBlock;
	public static TardisStairBlock	  stairBlock;
	public static TardisSlabBlock	  slabBlock;
	
	public static TardisAbstractItem schemaItem;
	public static TardisAbstractItem componentItem;
	public static TardisKeyItem keyItem;
	public static TardisSonicScrewdriverItem screwItem;
	
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
		configHandler = new TardisConfigHandler(event.getModConfigurationDirectory());
		configHandler.getSchemas();
		tab = new TardisCreativeTab();
		
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
		
		//MinecraftForge.EVENT_BUS.register(new TardisSoundHandler());
		
		proxy.postAssignment();
	}
	
	private void registerNetwork()
	{
		networkChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("tardis");
		networkChannel.register(packetHandler);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		aeAPI = AEApi.instance();
		keyItem.initRecipes();
		componentItem.initRecipes();
		chunkManager = new TardisChunkLoadingManager();
		MinecraftForge.EVENT_BUS.register(dimEventHandler);
		FMLCommonHandler.instance().bus().register(chunkManager);
		ForgeChunkManager.setForcedChunkLoadingCallback(this, chunkManager);
		inited = true;
	}
	
	private void initBlocks()
	{
		tardisBlock = new TardisBlock();
		GameRegistry.registerBlock(tardisBlock,tardisBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisTileEntity.class, tardisBlock.getUnlocalizedName());
		
		tardisTopBlock = new TardisTopBlock();
		GameRegistry.registerBlock(tardisTopBlock,tardisTopBlock.getUnlocalizedName());
		
		tardisCoreBlock = new TardisCoreBlock();
		GameRegistry.registerBlock(tardisCoreBlock,tardisCoreBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisCoreTileEntity.class, tardisCoreBlock.getUnlocalizedName());
		
		tardisConsoleBlock = new TardisConsoleBlock();
		GameRegistry.registerBlock(tardisConsoleBlock, tardisConsoleBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisConsoleTileEntity.class,tardisConsoleBlock.getUnlocalizedName());
		
		tardisEngineBlock = new TardisEngineBlock();
		GameRegistry.registerBlock(tardisEngineBlock, tardisEngineBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisEngineTileEntity.class, tardisEngineBlock.getUnlocalizedName());
		
		componentBlock = new TardisComponentBlock();
		GameRegistry.registerBlock(componentBlock,TardisComponentItemBlock.class,componentBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisComponentTileEntity.class, componentBlock.getUnlocalizedName());
		
		internalDoorBlock = new TardisInternalDoorBlock();
		GameRegistry.registerBlock(internalDoorBlock,TardisInternalDoorItemBlock.class,internalDoorBlock.getUnlocalizedName());
		
		decoBlock = new TardisDecoBlock();
		GameRegistry.registerBlock(decoBlock, TardisDecoItemBlock.class, decoBlock.getUnlocalizedName());
		
		stairBlock = new TardisStairBlock();
		GameRegistry.registerBlock(stairBlock,stairBlock.getUnlocalizedName());
		
		debugBlock = new TardisDebugBlock();
		GameRegistry.registerBlock(debugBlock, debugBlock.getUnlocalizedName());
		
		schemaBlock = new TardisSchemaBlock();
		GameRegistry.registerBlock(schemaBlock, schemaBlock.getUnlocalizedName());
		
		schemaCoreBlock = new TardisSchemaCoreBlock();
		GameRegistry.registerBlock(schemaCoreBlock,schemaCoreBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisSchemaCoreTileEntity.class, schemaCoreBlock.getUnlocalizedName());
		
		schemaComponentBlock = new TardisSchemaComponentBlock();
		GameRegistry.registerBlock(schemaComponentBlock, TardisSchemaComponentItemBlock.class, schemaComponentBlock.getUnlocalizedName());
		
		slabBlock = new TardisSlabBlock();
		GameRegistry.registerBlock(slabBlock,TardisSlabItemBlock.class,slabBlock.getUnlocalizedName());
	}
	
	private void initItems()
	{
		schemaItem = new TardisSchemaItem();
		GameRegistry.registerItem(schemaItem, schemaItem.getUnlocalizedName());
		
		screwItem = new TardisSonicScrewdriverItem();
		GameRegistry.registerItem(screwItem, screwItem.getUnlocalizedName());
		
		keyItem = new TardisKeyItem();
		GameRegistry.registerItem(keyItem, keyItem.getUnlocalizedName());
		
		componentItem = new TardisComponentItem();
		GameRegistry.registerItem(componentItem, componentItem.getUnlocalizedName());
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		dimReg = TardisDimensionRegistry.load();
		dimReg.registerDims();
		FMLCommonHandler.instance().bus().register(dimReg);
		MinecraftForge.EVENT_BUS.register(dimReg);
		plReg  = TardisPlayerRegistry.load();
		TardisPlayerRegistry.save();
		teleporter = new TardisTeleporter(event.getServer().worldServerForDimension(0));
		TardisCommandRegister.registerCommands(event);
	}
}
