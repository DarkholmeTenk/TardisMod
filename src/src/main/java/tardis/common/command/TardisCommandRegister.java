package tardis.common.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class TardisCommandRegister
{
	private static TardisTeleport teleportCommand;
	private static TardisSaveSchemaCommand		saveCommand;
	private static TardisLoadSchemaCommand		loadCommand;
	private static TardisGiveSchemaCommand		giveCommand;
	private static TardisRepairCoreCommand		repCCommand;
	private static TardisReloadSchemaCommand	refCommand;
	private static TardisRemoveSchemaCommand	remCommand;
	
	public static void registerCommands(FMLServerStartingEvent event)
	{
		teleportCommand = new TardisTeleport();
		saveCommand = new TardisSaveSchemaCommand();
		loadCommand = new TardisLoadSchemaCommand();
		giveCommand = new TardisGiveSchemaCommand();
		repCCommand = new TardisRepairCoreCommand();
		refCommand  = new TardisReloadSchemaCommand();
		remCommand  = new TardisRemoveSchemaCommand();
		event.registerServerCommand(teleportCommand);
		event.registerServerCommand(saveCommand);
		event.registerServerCommand(loadCommand);
		event.registerServerCommand(giveCommand);
		event.registerServerCommand(repCCommand);
		event.registerServerCommand(refCommand);
		event.registerServerCommand(remCommand);
	}
}
