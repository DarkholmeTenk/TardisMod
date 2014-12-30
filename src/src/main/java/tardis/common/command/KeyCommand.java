package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.items.KeyItem;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class KeyCommand extends AbstractCommand
{

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
	public void addAliases(List<String> aliases)
	{
		aliases.add("tkey");
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
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
			sendString(comSen,getCommandUsage(comSen));
			return;
		}
		else
		{
			EntityPlayerMP fromPlayer = Helper.getPlayer(from);
			if(fromPlayer != null)
			{
				ItemStack key = new ItemStack(TardisMod.keyItem,1);
				KeyItem.setOwnerName(key, to);
				Helper.giveItemStack(fromPlayer, key);
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return true;
	}
}
