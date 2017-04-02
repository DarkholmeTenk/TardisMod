package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.TardisMod;
import tardis.common.core.helpers.Helper;

public class TeleportCommand extends AbstractCommandNew
{
	@Override
	public void getAliases(List<String> list)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getCommandName()
	{
		return "ttp";
	}

	@Override
	public String getCommandUsage(ICommandSender comSen)
	{
		return "/ttp [player1] [player2] [player3]... <<playerTo>|[dimID] <x> <y> <z>";
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tardistp");
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sen, String[] args)
	{
		String lastArg = args[args.length - 1];
		if(lastArg.startsWith("#"))
		{
			return match(lastArg,TardisMod.plReg.getPlayersWithHash());
		}
		return super.addTabCompletionOptions(sen, args);
	}

	@Override
	public boolean process(ICommandSender comSen, List<String> astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP) comSen;
			if(astring.size() >= 1)
			{
				boolean nonPlayerFound = false;
				int offset = 0;
				ArrayList<EntityPlayerMP> pls = new ArrayList<EntityPlayerMP>();
				for(int i = 0;(i<astring.size())&&!nonPlayerFound;i++)
				{
					EntityPlayerMP plx = ServerHelper.getPlayer(astring.get(i));
					if(plx != null)
					{
						pls.add(plx);
						offset++;
					}
					else
						nonPlayerFound = true;
				}
				if(offset < astring.size())
				{
					if(pls.size() == 0)
						pls.add(pl);

					try
					{
						Integer w = Helper.getTardisDim(astring.get(offset));
						if(w == null)
						{
							sendString(comSen,"Unable to identify dimension " + astring.get(offset));
							return true;
						}
						int x = Integer.parseInt(astring.get(1+offset));
						int y = Integer.parseInt(astring.get(2+offset));
						int z = Integer.parseInt(astring.get(3+offset));
						for(EntityPlayerMP plx : pls)
							TeleportHelper.teleportEntity(plx,w,x,y,z);
					}
					catch(Exception e)
					{
						Integer w = null;
						if(astring.get(0+offset).startsWith("#"))
						{
							w = TardisMod.plReg.getDimension(astring.get(offset).replaceFirst("#", ""));
							if(w == null)
							{
								sendString(comSen,"Unable to identify dimension " + astring.get(offset));
								return true;
							}
							for(EntityPlayerMP plx : pls)
								TeleportHelper.teleportEntity(plx,w,2,Helper.tardisCoreY,0);
						}
						else
						{
							try
							{
								w = Integer.parseInt(astring.get(offset));
								for(EntityPlayerMP plx : pls)
									TeleportHelper.teleportEntity(plx,w);
							}
							catch(Exception er)
							{
								sendString(comSen,"Unable to identify dimension");
							}
						}
					}
				}
				else if(pls.size() > 0)
				{
					EntityPlayerMP dest = pls.get(pls.size()-1);
					if(pls.size() == 1)
						pls.add(pl);

					for(EntityPlayerMP plx : pls)
					{
						if(plx != dest)
							TeleportHelper.teleportEntity(plx, WorldHelper.getWorldID(dest),dest.posX,dest.posY,dest.posZ);
					}
				}
				else
				{
					sendString(pl,getCommandUsage(comSen));
				}
			}
			else
			{
				sendString(pl,getCommandUsage(comSen));
			}
		}
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return true;
	}
}
