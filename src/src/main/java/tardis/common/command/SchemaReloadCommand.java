package tardis.common.command;

import java.util.List;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.EngineTileEntity;

import net.minecraft.command.ICommandSender;

public class SchemaReloadCommand extends AbstractCommand
{
	@Override
	public String getCommandName()
	{
		return "tardisreload";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tardisrefresh");
		aliases.add("treload");
		aliases.add("trefresh");
		aliases.add("trel");
		aliases.add("tref");
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] astring)
	{
		ConsoleTileEntity.refreshSchemas();
		EngineTileEntity.refreshAvailableConsoleRooms();
	}
}
