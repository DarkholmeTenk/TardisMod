package tardis.core.commands;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class TardisCommandRegister
{
	private static TardisTeleport teleportCommand;
	private static TardisSaveSchemaCommand saveCommand;
	private static TardisLoadSchemaCommand loadCommand;
	private static TardisGiveSchemaCommand giveCommand;
	private static TardisRepairCoreCommand repCCommand;
	
	public static void registerCommands(FMLServerStartingEvent event)
	{
		teleportCommand = new TardisTeleport();
		event.registerServerCommand(teleportCommand);
		saveCommand = new TardisSaveSchemaCommand();
		event.registerServerCommand(saveCommand);
		loadCommand = new TardisLoadSchemaCommand();
		event.registerServerCommand(loadCommand);
		giveCommand = new TardisGiveSchemaCommand();
		event.registerServerCommand(giveCommand);
		repCCommand = new TardisRepairCoreCommand();
		event.registerServerCommand(repCCommand);
	}
}
