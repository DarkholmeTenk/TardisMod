package tardis;

import java.io.IOException;

import net.minecraftforge.common.DimensionManager;

import tardis.blocks.TardisAbstractBlock;
import tardis.blocks.TardisBlock;
import tardis.blocks.TardisDebugBlock;
import tardis.blocks.TardisDecoBlock;
import tardis.blocks.TardisInternalDoorBlock;
import tardis.blocks.TardisInternalDoorItemBlock;
import tardis.blocks.TardisSchemaBlock;
import tardis.blocks.TardisSchemaComponentBlock;
import tardis.blocks.TardisSchemaComponentItemBlock;
import tardis.blocks.TardisSchemaCoreBlock;
import tardis.blocks.TardisSchemaItemBlock;
import tardis.blocks.TardisTopBlock;
import tardis.core.Helper;
import tardis.core.TardisConfigFile;
import tardis.core.TardisConfigHandler;
import tardis.core.TardisCreativeTab;
import tardis.core.TardisOutput;
import tardis.core.TardisPacketHandler;
import tardis.core.TardisTeleporter;
import tardis.core.commands.TardisCommandRegister;
import tardis.dimension.TardisWorldProvider;
import tardis.items.TardisAbstractItem;
import tardis.items.TardisSchemaItem;
import tardis.items.TardisSonicScrewdriverItem;
import tardis.tileents.TardisSchemaCoreTileEntity;
import tardis.tileents.TardisTileEntity;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="TardisMod",name="Tardis Mod",version="1.0",dependencies="required-after:FML")
@NetworkMod(channels = { "TardisModChannel","TardisTrans" }, clientSideRequired = true, serverSideRequired = true, packetHandler = TardisPacketHandler.class)
public class TardisMod
{
	@Instance("TardisMod")
	public static TardisMod instance;
	
	@SidedProxy(clientSide="tardis.client.TardisClientProxy", serverSide="tardis.TardisProxy")
	public static TardisProxy proxy;
	
	private TardisConfigFile modConfig;
	private TardisConfigFile blockConfig;
	private TardisConfigFile itemConfig;
	
	public static TardisTeleporter teleporter = null;
	public static TardisConfigHandler configHandler;
	public static TardisCreativeTab tab = null;
	
	public static TardisOutput.Priority priorityLevel = TardisOutput.Priority.INFO;
	public static int providerID = 54;
	public static boolean tardisLoaded = true;
	
	public static TardisAbstractBlock tardisBlock;
	public static TardisAbstractBlock tardisTopBlock;
	public static TardisAbstractBlock internalDoorBlock;
	public static TardisAbstractBlock decoBlock;
	public static TardisAbstractBlock schemaBlock;
	public static TardisAbstractBlock schemaCoreBlock;
	public static TardisAbstractBlock schemaComponentBlock;
	public static TardisAbstractBlock debugBlock;
	
	public static TardisAbstractItem schemaItem;
	public static TardisAbstractItem screwItem;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException
	{
		configHandler = new TardisConfigHandler(event.getModConfigurationDirectory());
		
		tab = new TardisCreativeTab();
		
		modConfig   = configHandler.getConfigFile("Mod");
		
		int prioLevel = Helper.clamp(modConfig.getInt("Debug Level", priorityLevel.ordinal()),0,TardisOutput.Priority.values().length);
		priorityLevel = TardisOutput.Priority.values()[prioLevel];
		
		providerID    = modConfig.getInt("Dimension Provider ID", 54);
		tardisLoaded  = modConfig.getBoolean("Dimension always loaded", true);
		DimensionManager.registerProviderType(providerID, TardisWorldProvider.class, tardisLoaded);
		
		blockConfig = configHandler.getConfigFile("Blocks");
		initBlocks();
		
		itemConfig = configHandler.getConfigFile("Items");
		initItems();
	}
	
	private void initBlocks()
	{
		tardisBlock = new TardisBlock(blockConfig.getInt("tardisBlock",639));
		GameRegistry.registerBlock(tardisBlock,tardisBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisTileEntity.class, tardisBlock.getUnlocalizedName());
		
		tardisTopBlock = new TardisTopBlock(blockConfig.getInt("tardisTopBlockID",644));
		GameRegistry.registerBlock(tardisTopBlock,tardisTopBlock.getUnlocalizedName());
		
		internalDoorBlock = new TardisInternalDoorBlock(blockConfig.getInt("tardisInternalDoorBlockID", 645));
		GameRegistry.registerBlock(internalDoorBlock,TardisInternalDoorItemBlock.class,internalDoorBlock.getUnlocalizedName());
		
		decoBlock = new TardisDecoBlock(blockConfig.getInt("decoBlockID", 640));
		GameRegistry.registerBlock(decoBlock, decoBlock.getUnlocalizedName());
		
		debugBlock = new TardisDebugBlock(blockConfig.getInt("debugBlockID", 641));
		GameRegistry.registerBlock(debugBlock, debugBlock.getUnlocalizedName());
		
		schemaBlock = new TardisSchemaBlock(blockConfig.getInt("schemaBlockID", 642));
		GameRegistry.registerBlock(schemaBlock, TardisSchemaItemBlock.class, schemaBlock.getUnlocalizedName());
		
		schemaCoreBlock = new TardisSchemaCoreBlock(blockConfig.getInt("schemaCoreBlockID", 646));
		GameRegistry.registerBlock(schemaCoreBlock,schemaCoreBlock.getUnlocalizedName());
		GameRegistry.registerTileEntity(TardisSchemaCoreTileEntity.class, schemaCoreBlock.getUnlocalizedName());
		
		schemaComponentBlock = new TardisSchemaComponentBlock(blockConfig.getInt("schemaComponentBlockID", 643));
		GameRegistry.registerBlock(schemaComponentBlock, TardisSchemaComponentItemBlock.class, schemaComponentBlock.getUnlocalizedName());
	}
	
	private void initItems()
	{
		schemaItem = new TardisSchemaItem(itemConfig.getInt("Schematic Item ID",1730));
		GameRegistry.registerItem(schemaItem, schemaItem.getUnlocalizedName());
		
		screwItem = new TardisSonicScrewdriverItem(itemConfig.getInt("Screwdriver Item ID",1731));
		GameRegistry.registerItem(screwItem, screwItem.getUnlocalizedName());
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		teleporter = new TardisTeleporter(event.getServer().worldServerForDimension(0));
		TardisCommandRegister.registerCommands(event);
	}
}
