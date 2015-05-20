package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import tardis.TardisMod;

public class TeleportCommand extends AbstractCommand
{
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
	public void commandBody(ICommandSender comSen, String[] astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP) comSen;
			if(astring.length >= 1)
			{
				boolean nonPlayerFound = false;
				int offset = 0;
				ArrayList<EntityPlayerMP> pls = new ArrayList<EntityPlayerMP>();
				for(int i = 0;(i<astring.length)&&!nonPlayerFound;i++)
				{
					EntityPlayerMP plx = ServerHelper.getPlayer(astring[i]);
					if(plx != null)
					{
						pls.add(plx);
						offset++;
					}
					else
						nonPlayerFound = true;
				}
				if(offset < astring.length)
				{
					if(pls.size() == 0)
						pls.add(pl);

					try
					{
						Integer w = 0;
						if(astring[0+offset].startsWith("#"))
						{
							w = TardisMod.plReg.getDimension(astring[0+offset].replaceFirst("#", ""));
							if(w == null)
							{
								sendString(comSen,"Unable to identify dimension " + astring[offset]);
								return;
							}
						}
						else
							w = Integer.parseInt(astring[0+offset]);
						int x = Integer.parseInt(astring[1+offset]);
						int y = Integer.parseInt(astring[2+offset]);
						int z = Integer.parseInt(astring[3+offset]);
						for(EntityPlayerMP plx : pls)
							TeleportHelper.teleportEntity(plx,w,x,y,z);
					}
					catch(Exception e)
					{
						Integer w = null;
						if(astring[0+offset].startsWith("#"))
						{
							w = TardisMod.plReg.getDimension(astring[0+offset].replaceFirst("#", ""));
							if(w == null)
							{
								sendString(comSen,"Unable to identify dimension " + astring[offset]);
								return;
							}
							for(EntityPlayerMP plx : pls)
								TeleportHelper.teleportEntity(plx,w,2,30,0);
						}
						else
						{
							try
							{
								w = Integer.parseInt(astring[0+offset]);
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
							TeleportHelper.teleportEntity(plx, dest.worldObj.provider.dimensionId,dest.posX,dest.posY,dest.posZ);
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
	}
}
