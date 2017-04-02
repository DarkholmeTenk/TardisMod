package tardis.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import tardis.TardisMod;
import tardis.common.core.helpers.Helper;

public class SummonTardisCommand extends AbstractCommandNew
{

	@Override
	public String getCommandName()
	{
		return "tardissummon";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("tsummon");
		list.add("tsum");
	}

	@Override
	public boolean process(ICommandSender sen, List<String> strList)
	{
		EntityPlayer pl = null;
		if(sen instanceof EntityPlayer)
			pl = (EntityPlayer) sen;
		Integer w = null;
		if(strList.size() == 2)
		{
			w = Helper.getTardisDim(strList.get(0));
			if(w == null)
			{
				sendString(sen, "Unable to identify dimension " + strList.get(0));
				return false;
			}
			pl = ServerHelper.getPlayer(strList.get(1));
			if(pl == null)
			{
				sendString(sen, "Unable to identify player " + strList.get(1));
				return false;
			}
		}
		else
		{
			if(pl == null)
			{
				sendString(sen, "This command needs 2 arguments when run on the server");
				return false;
			}

			if(strList.size() == 1)
			{
				w = Helper.getTardisDim(strList.get(0));
				if(w == null)
				{
					sendString(sen, "Unable to identify dimension " + strList.get(0));
					return false;
				}
			}
			else if(strList.size() == 0)
			{
				if(pl != null)
				{
					w = TardisMod.plReg.getDimension(pl);
					if(w == null)
					{
						sendString(sen, "You do not have a TARDIS to summon");
						return false;
					}
				}
			}
			else
				return false;
		}
		if((w == null) || (pl == null)) return false;
		Helper.summonOldTardis(w, pl);
		sendString(sen, "TARDIS summoned to player");
		return true;
	}

}
