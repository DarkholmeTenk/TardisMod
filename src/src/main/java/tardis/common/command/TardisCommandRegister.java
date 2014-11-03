package tardis.common.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class TardisCommandRegister
{
	private static TardisAbstractCommand teleportCommand;
	private static TardisAbstractCommand saveCommand;
	private static TardisAbstractCommand loadCommand;
	private static TardisAbstractCommand giveCommand;
	private static TardisAbstractCommand repCCommand;
	private static TardisAbstractCommand refCommand;
	private static TardisAbstractCommand remCommand;
	private static TardisAbstractCommand keyCommand;
	private static TardisAbstractCommand xpCommand;
	private static TardisAbstractCommand regCommand;
	private static TardisAbstractCommand conCommand;
	
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
		xpCommand	= new TardisXpCommand();
		regCommand	= new TardisRegCommand();
		conCommand	= new TardisSwitchConsoleCommand();
		event.registerServerCommand(teleportCommand);
		event.registerServerCommand(saveCommand);
		event.registerServerCommand(loadCommand);
		event.registerServerCommand(giveCommand);
		event.registerServerCommand(repCCommand);
		event.registerServerCommand(refCommand);
		event.registerServerCommand(remCommand);
		event.registerServerCommand(keyCommand);
		event.registerServerCommand(xpCommand);
		event.registerServerCommand(regCommand);
		event.registerServerCommand(conCommand);
	}
}
