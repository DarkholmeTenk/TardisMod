package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import tardis.common.core.Helper;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class TardisTeleport extends TardisAbstractCommand
{
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
				for(int i = 0;i<astring.length&&!nonPlayerFound;i++)
				{
					EntityPlayerMP plx = Helper.getPlayer(astring[i]);
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
						int w = Integer.parseInt(astring[0+offset]);
						int x = Integer.parseInt(astring[1+offset]);
						int y = Integer.parseInt(astring[2+offset]);
						int z = Integer.parseInt(astring[3+offset]);
						for(EntityPlayerMP plx : pls)
							Helper.teleportEntity(plx,w,x,y,z);
					}
					catch(Exception e)
					{
						for(EntityPlayerMP plx : pls)
							Helper.teleportEntity(plx,Helper.toInt(astring[0], 0));
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
							Helper.teleportEntity(plx, dest.worldObj.provider.dimensionId,dest.posX,dest.posY,dest.posZ);
					}
				}
				else
				{
					pl.addChatMessage(getCommandUsage(comSen));
				}
			}
			else
			{
				pl.addChatMessage(getCommandUsage(comSen));
			}
		}
	}
}
