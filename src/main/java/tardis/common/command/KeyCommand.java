package tardis.common.command;

import java.util.List;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import tardis.TardisMod;
import tardis.common.items.KeyItem;

public class KeyCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardiskey";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "tardiskey <player1> <player2>";
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tkey");
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
	{
		if(ServerHelper.isClient())
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
		if((to == null) || (from == null))
		{
			sendString(comSen,getCommandUsage(comSen));
			return;
		}
		else
		{
			EntityPlayerMP fromPlayer = ServerHelper.getPlayer(from);
			if(fromPlayer != null)
			{
				ItemStack key = new ItemStack(TardisMod.keyItem,1);
				KeyItem.setOwnerName(key, to);
				WorldHelper.giveItemStack(fromPlayer, key);
			}
		}
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return true;
	}
}
