package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.TardisOutput;

import net.minecraft.command.ICommandSender;

public class SwitchDebugModeCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisdebug";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> list)
	{
		list.add("tdebug");
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] astring)
	{
		try
		{
			int a = Integer.parseInt(astring[1]);
			TardisMod.priorityLevel = TardisOutput.Priority.get(a);
			sendString(icommandsender, "Debug mode set to " + TardisMod.priorityLevel.toString());
		}
		catch(Exception e)
		{
			
		}
	}

}
