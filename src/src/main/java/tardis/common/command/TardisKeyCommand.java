package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.items.TardisKeyItem;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;

public class TardisKeyCommand implements ICommand
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
		return "tardiskey";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "tardiskey <username1> <username2>: gives username2's key to username1";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("tkey");
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
	{
		if(!Helper.isServer())
			return;
		String from = (comSen instanceof EntityPlayer) ? comSen.getCommandSenderName() : null;
		String to = null;
		if(astring.length == 1)
			to = astring[0];
		else if(astring.length == 2)
		{
			from = astring[0];
			to   = astring[1];
		}
		if(to == null || from == null)
		{
			comSen.sendChatToPlayer(new ChatMessageComponent().addText(getCommandUsage(comSen)));
			return;
		}
		else
		{
			EntityPlayerMP fromPlayer = Helper.getPlayer(from);
			if(fromPlayer != null)
			{
				ItemStack key = new ItemStack(TardisMod.keyItem,1);
				TardisKeyItem.setOwnerName(key, to);
				Helper.giveItemStack(fromPlayer, key);
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
		return true;
	}
}
