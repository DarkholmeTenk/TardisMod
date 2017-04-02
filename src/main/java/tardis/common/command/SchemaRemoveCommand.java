package tardis.common.command;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;

import tardis.TardisMod;
import tardis.common.tileents.ConsoleTileEntity;

public class SchemaRemoveCommand extends AbstractCommandNew
{

	@Override
	public String getCommandName()
	{
		return "remove";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("rem");
	}

	@Override
	public boolean process(ICommandSender icommandsender, List<String> astring)
	{
		if(astring.size() == 1)
		{
			String name = astring.get(0);
			File schema = TardisMod.schemaHandler.getSchemaFile(name);
			try
			{
				if(schema.exists())
				{
					schema.delete();
					sendString(icommandsender,"Removed schematic " + name);
					ConsoleTileEntity.refreshCategories();
				}
				else
					sendString(icommandsender,"Schema does not exist");
				return true;
			}
			catch(Exception e)
			{
				sendString(icommandsender,"Unable to remove");
				e.printStackTrace();
			}
		}
		return false;
	}
}
