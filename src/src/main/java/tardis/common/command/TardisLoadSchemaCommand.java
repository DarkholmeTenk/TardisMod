package tardis.common.command;

import java.util.List;

import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class TardisLoadSchemaCommand extends TardisAbstractCommand
{
	@Override
	public String getCommandName()
	{
		return "tardisload";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tload");
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)comSen;
			if(astring.length == 5)
			{
				String name = astring[0];
				try
				{
					int x = Integer.parseInt(astring[1]);
					int y = Integer.parseInt(astring[2]);
					int z = Integer.parseInt(astring[3]);
					int facing = Integer.parseInt(astring[4]);
					Helper.loadSchema(name, pl.worldObj, x, y, z, facing);
				}
				catch(NumberFormatException e)
				{
					pl.addChatMessage("Totally not numbers");
				}
				catch(Exception e)
				{
					TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
					e.printStackTrace();
				}
			}
		}
	}
}
