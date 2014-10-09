package tardis.common.command;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.common.tileents.TardisConsoleTileEntity;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class TardisRemoveSchemaCommand implements ICommand
{

	@Override
	public int compareTo(Object arg0)
	{
		return 0;
	}

	@Override
	public String getCommandName()
	{
		return "tardisreload";
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
	public void processCommand(ICommandSender icommandsender, String[] astring)
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
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

}
