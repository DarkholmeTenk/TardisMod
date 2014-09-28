package tardis.core.commands;

import java.util.List;

import tardis.core.Helper;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class TardisTeleport implements ICommand
{

	@Override
	public int compareTo(Object obj)
	{
		if(obj instanceof TardisTeleport)
			return 0;
		return 1;
	}

	@Override
	public String getCommandName()
	{
		return "ttp";
	}

	@Override
	public String getCommandUsage(ICommandSender comSen)
	{
		return "ttp dimID x y z";
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
			EntityPlayerMP pl = (EntityPlayerMP) comSen;
			if(astring.length >= 1)
			{
				try
				{
					Helper.teleportEntity(pl, Helper.toInt(astring[0], 0), Integer.parseInt(astring[1]),Integer.parseInt(astring[2]),Integer.parseInt(astring[3]));
				}
				catch(Exception e)
				{
					Helper.teleportEntity(pl, Helper.toInt(astring[0], 0));
				}
			}
			else
			{
				pl.addChatMessage(getCommandUsage(comSen));
			}
		}
	}

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
	public List addTabCompletionOptions(ICommandSender comSen, String[] astring)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

}
