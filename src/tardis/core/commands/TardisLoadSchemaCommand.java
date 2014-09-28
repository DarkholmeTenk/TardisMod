package tardis.core.commands;

import java.util.List;

import tardis.TardisMod;
import tardis.core.TardisOutput;
import tardis.core.schema.TardisPartBlueprint;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class TardisLoadSchemaCommand implements ICommand
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender comSen)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			if(!MinecraftServer.getServer().getConfigurationManager().getOps().contains(comSen.getCommandSenderName()))
				return true;
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object arg0)
	{
		return 0;
	}

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
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
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
					TardisPartBlueprint bp = new TardisPartBlueprint(TardisMod.configHandler.getSchemaFile(name));
					bp.reconstitute(pl.worldObj, x, y, z, facing);
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

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
