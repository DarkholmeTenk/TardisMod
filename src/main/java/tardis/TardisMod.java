package tardis;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.DarkcoreTeleporter;
import io.darkcraft.darkcore.mod.config.ConfigHandler;
import io.darkcraft.darkcore.mod.config.ConfigHandlerFactory;
import io.darkcraft.darkcore.mod.helpers.PlayerHelper;
import io.darkcraft.darkcore.mod.interfaces.IConfigHandlerMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import tardis.common.TMRegistry;
import tardis.common.TardisProxy;
import tardis.common.command.CommandRegister;
import tardis.common.core.CreativeTab;
import tardis.common.core.DimensionEventHandler;
import tardis.common.core.SchemaHandler;
import tardis.common.core.TardisDimensionRegistry;
import tardis.common.core.TardisOwnershipRegistry;
import tardis.common.core.events.internal.DamageEventHandler;
import tardis.common.core.flight.FlightConfiguration;
import tardis.common.core.helpers.Helper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;
import tardis.common.dimension.BiomeGenConsoleRoom;
import tardis.common.dimension.TardisDimensionHandler;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.dimension.damage.TardisDamageSystem;
import tardis.common.integration.ae.AEHelper;
import tardis.common.items.extensions.ScrewTypeRegister;
import tardis.common.items.extensions.screwtypes.AbstractScrewdriverType;
import tardis.common.items.extensions.screwtypes.Eighth;
import tardis.common.items.extensions.screwtypes.Tenth;
import tardis.common.items.extensions.screwtypes.Twelth;
import tardis.common.network.TardisPacketHandler;
import tardis.common.tileents.extensions.chameleon.ChameleonRegistry;
import tardis.common.tileents.extensions.chameleon.tardis.AbstractTardisChameleon;
import tardis.common.tileents.extensions.chameleon.tardis.DefaultTardisCham;
import tardis.common.tileents.extensions.chameleon.tardis.NewTardisCham;
import tardis.common.tileents.extensions.chameleon.tardis.PostboxTardisCham;
import thaumcraft.api.ItemApi;

@Mod(modid = "TardisMod", name = "Tardis Mod", version = "0.995", dependencies = "required-after:FML; required-after:darkcore@[0.46,0.49]; after:CoFHCore; after:appliedenergistics2; after:Waila; before:DragonAPI")
public class TardisMod implements IConfigHandlerMod
{
	@Instance
	public static TardisMod										i;
	public static final String									modName				= "TardisMod";
	public static boolean										inited				= false;

	@SidedProxy(clientSide = "tardis.client.TardisClientProxy", serverSide = "tardis.common.TardisProxy")
	public static TardisProxy									proxy;
	public static DimensionEventHandler							dimEventHandler		= new DimensionEventHandler();

	public static ConfigHandler									configHandler;
	public static SchemaHandler									schemaHandler;

	public static DarkcoreTeleporter							teleporter			= null;
	public static TardisDimensionHandler						otherDims;
	public static TardisDimensionRegistry						dimReg;
	public static TardisOwnershipRegistry						plReg;
	public static CreativeTab									tab					= null;
	public static CreativeTab									cTab				= null;
	public static BiomeGenBase									consoleBiome		;



	public static boolean										tcInstalled			= false;

	public static AbstractScrewdriverType						defaultType			= new Tenth();
	public static ChameleonRegistry<AbstractTardisChameleon>	tardisChameleonReg	= new ChameleonRegistry(new DefaultTardisCham());

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException
	{
		configHandler = ConfigHandlerFactory.getConfigHandler(this);
		schemaHandler = new SchemaHandler(configHandler);
		schemaHandler.getSchemas();
		tab = new CreativeTab("TardisModTab");
		cTab = new CreativeTab("TardisModCraftableTab");
		DarkcoreMod.registerCreativeTab(modName, tab);

		refreshConfigs();

		consoleBiome = new BiomeGenConsoleRoom(Configs.consoleBiomeID);
//		BiomeDictionary.registerBiomeType(consoleBiome, BiomeDictionary.Type.PLAINS);
		DimensionManager.registerProviderType(Configs.providerID, TardisWorldProvider.class, Configs.tardisLoaded);
		initChameleonTypes();
		TMRegistry.init();


		// MinecraftForge.EVENT_BUS.register(new SoundHandler());

		proxy.postAssignment();
	}

	private void initChameleonTypes()
	{
		tardisChameleonReg.register(new NewTardisCham());
		tardisChameleonReg.register(new PostboxTardisCham());

		ScrewTypeRegister.register(defaultType);
		ScrewTypeRegister.register(new Twelth());
		ScrewTypeRegister.register(new Eighth());
	}

	public static void refreshConfigs()
	{
		TardisDimensionHandler.refreshConfigs();
		TardisDamageSystem.refreshConfigs();
		FlightConfiguration.refreshConfigs();
		Configs.refreshConfigs();
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
		tardisChameleonReg.postInit();
		AEHelper.init();
		TMRegistry.initRecipes();
		FMLCommonHandler.instance().bus().register(dimEventHandler);
		MinecraftForge.EVENT_BUS.register(dimEventHandler);
		inited = true;
		tcInstalled = ItemApi.getItem("itemResource", 0) != null;
		CommandRegister.registerListeners();
		if (Configs.keyOnFirstJoin) PlayerHelper.registerJoinItem(new ItemStack(TMRegistry.keyItem, 1));
		FMLCommonHandler.instance().bus().register(ScrewdriverHelperFactory.i);
	}

	@EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event)
	{
		if (otherDims != null) MinecraftForge.EVENT_BUS.unregister(otherDims);
		otherDims = new TardisDimensionHandler();
		plReg = null;
		dimReg = null;
		Helper.datastoreMap.clear();
		Helper.ssnDatastoreMap.clear();
		MinecraftForge.EVENT_BUS.register(otherDims);
		DamageEventHandler.i.register();
		ScrewdriverHelperFactory.i.clear();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		TardisDimensionRegistry.loadAll();
		dimReg.registerDims();
		TardisOwnershipRegistry.loadAll();
		TardisOwnershipRegistry.saveAll();
		teleporter = new DarkcoreTeleporter(event.getServer().worldServerForDimension(0));
		CommandRegister.registerCommands(event);
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
		otherDims.findDimensions();
	}

	@Override
	public String getModID()
	{
		return modName;
	}
}
