package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import tardis.TardisMod;
import tardis.common.tileents.ConsoleTileEntity;

public class SchemaRemoveCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisremove";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/tardisremove <schemaName>";
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("trem");
		aliases.add("tremove");
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] astring)
	{
		if(astring.length == 1)
		{
			String name = astring[0];
			File schema = TardisMod.schemaHandler.getSchemaFile(name);
			try
			{
				if(schema.exists())
				{
					schema.delete();
					sendString(icommandsender,"Removed schematic " + name);
					ConsoleTileEntity.refreshCategories();
				}
			}
			catch(Exception e)
			{
				sendString(icommandsender,"Unable to remove");
			}
		}
	}
}
