package tardis.common.command;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.common.tileents.TardisConsoleTileEntity;

import net.minecraft.command.ICommandSender;

public class TardisRemoveSchemaCommand extends TardisAbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisremove";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
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
			File schema = TardisMod.configHandler.getSchemaFile(name);
			try
			{
				if(schema.exists())
				{
					schema.delete();
					sendString(icommandsender,"Removed schematic " + name);
					TardisConsoleTileEntity.refreshSchemas();
				}
			}
			catch(Exception e)
			{
				sendString(icommandsender,"Unable to remove");
			}
		}
	}
}
