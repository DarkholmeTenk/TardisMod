package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import tardis.common.core.TardisOutput;
import tardis.common.core.helpers.Helper;

public class SchemaLoadCommand extends AbstractCommandNew
{
	@Override
	public String getCommandName()
	{
		return "load";
	}

	@Override
	public void getAliases(List<String> list)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getCommandUsage(ICommandSender sen, String totalCommand)
	{
		sendString(sen,totalCommand + " <schema> <x> <y> <z> <facing>");
	}

	@Override
	public boolean process(ICommandSender comSen, List<String> astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)comSen;
			if(astring.size() == 5)
			{
				String name = astring.get(0);
				TardisOutput.print("TLC", "Attempting to load " + name +" in dim " + WorldHelper.getWorldID(pl.worldObj));
				try
				{
					int x = Integer.parseInt(astring.get(1));
					int y = Integer.parseInt(astring.get(2));
					int z = Integer.parseInt(astring.get(3));
					int facing = Integer.parseInt(astring.get(4));
					Helper.loadSchema(name, pl.worldObj, x, y, z, facing);
					return true;
				}
				catch(NumberFormatException e)
				{
					sendString(pl,"Totally not numbers");
				}
				catch(Exception e)
				{
					TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
					e.printStackTrace();
				}
			}
			return false;
		}
		sendString(comSen,"Player usable only");
		return true;
	}
}
