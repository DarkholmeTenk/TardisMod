package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;

import java.util.List;

import net.minecraft.command.ICommandSender;
import tardis.Configs;
import tardis.common.core.TardisOutput;

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
		return "/tdebug (deprecated)";
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
			Configs.priorityLevel = TardisOutput.Priority.get(a);
			sendString(icommandsender, "Debug mode set to " + Configs.priorityLevel.toString());
		}
		catch(Exception e)
		{

		}
	}

}
