package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.SchemaCoreTileEntity;

public class RegCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisreg";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/treg <list|set|remove> <player> <dim>";
	}

	@Override
	public void addAliases(List<String> list)
	{
		list.add("treg");
	}

	@Override
	public void commandBody(ICommandSender comsen, String[] astring)
	{
		if(astring.length == 0)
			sendString(comsen,getCommandUsage(comsen));
		else
		{
			if(astring[0].equals("list"))
				TardisMod.plReg.chatMapping(comsen);
			else if(astring[0].equals("remove"))
			{
				if(astring.length == 2)
				{
					String un = astring[1];
					if(TardisMod.plReg.removePlayer(un))
						sendString(comsen,un + " removed from listing");
					else
						sendString(comsen,un + " could not be removed");
				}
				else if(astring.length == 3)
				{
					if(astring[1].equals("total"))
					{
						String un = astring[2];
						Integer dim = TardisMod.plReg.getDimension(un);
						if(dim != null)
						{
							World w = WorldHelper.getWorld(dim);
							if(w != null)
							{
								List l = new ArrayList(w.playerEntities);
								for(Object o : l)
								{
									if(o instanceof Entity)
										TeleportHelper.teleportEntityToOverworldSpawn((Entity)o);
								}
								CoreTileEntity core = Helper.getTardisCore(w);
								if(core != null)
									core.removeAllRooms(true);
								WorldHelper.removeTE(w, Helper.getTardisEngine(w));
								WorldHelper.removeTE(w, Helper.getTardisConsole(w));
								WorldHelper.removeTE(w, Helper.getTardisCore(w));
								TileEntity te = w.getTileEntity(Helper.tardisCoreX, Helper.tardisCoreY - 10, Helper.tardisCoreZ);
								if(te instanceof SchemaCoreTileEntity)
									((SchemaCoreTileEntity)te).remove(false);
								TardisMod.plReg.removePlayer(un);
								TardisMod.dimReg.unregisterDim(dim);
							}
						}
					}
				}
			}
			else if(astring[0].equals("set"))
			{
				if(astring.length == 3)
				{
					String un = astring[1];
					int dim = MathHelper.toInt(astring[2], 0);
					if(TardisMod.plReg.addPlayer(un, dim))
						sendString(comsen,un + " added to listing");
					else
						sendString(comsen,un + " could not be added to listing");
				}
			}
		}
	}

}
