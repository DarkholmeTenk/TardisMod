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
	private static TardisKeyCommand				keyCommand;
	
	public static void registerCommands(FMLServerStartingEvent event)
	{
		teleportCommand = new TardisTeleport();
		saveCommand = new TardisSaveSchemaCommand();
		loadCommand = new TardisLoadSchemaCommand();
		giveCommand = new TardisGiveSchemaCommand();
		repCCommand = new TardisRepairCoreCommand();
		refCommand  = new TardisReloadSchemaCommand();
		remCommand  = new TardisRemoveSchemaCommand();
		keyCommand  = new TardisKeyCommand();
		event.registerServerCommand(teleportCommand);
		event.registerServerCommand(saveCommand);
		event.registerServerCommand(loadCommand);
		event.registerServerCommand(giveCommand);
		event.registerServerCommand(repCCommand);
		event.registerServerCommand(refCommand);
		event.registerServerCommand(remCommand);
		event.registerServerCommand(keyCommand);
	}
}
