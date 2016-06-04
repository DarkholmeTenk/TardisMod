package tardis.common.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.handlers.CommandHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommandRegister
{
	private static AbstractCommand teleportCommand;
	//private static AbstractCommand saveCommand;
	//private static AbstractCommand loadCommand;
	//private static AbstractCommand giveCommand;
	private static AbstractCommand repCCommand;
	//private static AbstractCommand refCommand;
	//private static AbstractCommand remCommand;
	private static AbstractCommand keyCommand;
	private static AbstractCommand xpCommand;
	private static AbstractCommand regCommand;
	private static AbstractCommand conCommand;
	private static AbstractCommand sumCommand;
	private static AbstractCommand leCommand;

	static
	{
		CommandHandler.registerCommand(new SchemaCommand());
		teleportCommand = new TeleportCommand();
		//saveCommand = new SchemaSaveCommand();
		//loadCommand = new SchemaLoadCommand();
		//giveCommand = new SchemaGiveCommand();
		repCCommand = new RepairCoreCommand();
		//refCommand  = new SchemaReloadCommand();
		//remCommand  = new SchemaRemoveCommand();
		keyCommand  = new KeyCommand();
		xpCommand	= new XpCommand();
		regCommand	= new RegCommand();
		conCommand	= new SwitchConsoleCommand();
		sumCommand = new SummonTardisCommand();
		leCommand = new LandingEventCommand();
	}

	public static void registerListeners()
	{
		MinecraftForge.EVENT_BUS.register(leCommand);
	}

	public static void registerCommands(FMLServerStartingEvent event)
	{
		LandingEventCommand.clear();
		event.registerServerCommand(teleportCommand);
		//event.registerServerCommand(saveCommand);
		//event.registerServerCommand(loadCommand);
		//event.registerServerCommand(giveCommand);
		event.registerServerCommand(repCCommand);
		//event.registerServerCommand(refCommand);
		//event.registerServerCommand(remCommand);
		event.registerServerCommand(keyCommand);
		event.registerServerCommand(xpCommand);
		event.registerServerCommand(regCommand);
		event.registerServerCommand(conCommand);
		event.registerServerCommand(new SetValueCommand());
		event.registerServerCommand(new TopCommand());
		event.registerServerCommand(sumCommand);
		event.registerServerCommand(leCommand);
	}
}
