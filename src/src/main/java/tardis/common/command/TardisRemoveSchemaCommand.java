package tardis.common.command;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.common.tileents.TardisConsoleTileEntity;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

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
					icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Removed schematic " + name));
					TardisConsoleTileEntity.refreshSchemas();
				}
			}
			catch(Exception e)
			{
				icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Unable to remove"));
			}
		}
	}
}
