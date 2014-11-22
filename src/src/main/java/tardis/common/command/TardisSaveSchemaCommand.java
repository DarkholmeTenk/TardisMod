package tardis.common.command;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import tardis.common.core.exception.schema.SchemaCoreNotFoundException;
import tardis.common.core.exception.schema.SchemaDoorNotFoundException;
import tardis.common.core.schema.TardisPartBlueprint;
import tardis.common.tileents.TardisConsoleTileEntity;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TardisSaveSchemaCommand extends TardisAbstractCommand
{
	@Override
	public String getCommandName()
	{
		return "tardissave";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tsave");
	}
	
	private boolean save(String name, EntityPlayerMP pl, World w, int x, int y, int z)
	{
		try
		{
			TardisPartBlueprint bp = new TardisPartBlueprint(w, name, x,y,z);
			File saveFile = TardisMod.configHandler.getSchemaFile(name);
			bp.saveTo(saveFile);
			TardisConsoleTileEntity.refreshSchemas();
			return true;
		}
		catch(SchemaCoreNotFoundException e)
		{
			sendString(pl,e.getMessage());
			TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		catch(SchemaDoorNotFoundException e)
		{
			sendString(pl,e.getMessage());
			TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		catch(Exception e)
		{
			TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			int x=0;
			int y=0;
			int z=0;
			String name=null;
			EntityPlayerMP pl = (EntityPlayerMP)comSen;
			if(astring.length == 4)
			{
				name = astring[0];
				try
				{
					x = Integer.parseInt(astring[1]);
					y = Integer.parseInt(astring[2]);
					z = Integer.parseInt(astring[3]);
				}
				catch(NumberFormatException e)
				{
					sendString(pl,"Totally not numbers");
				}
				
			}
			else if(astring.length == 1)
			{
				name = astring[0];
				x = (int) Math.floor(pl.posX);
				y = (int) Math.floor(pl.posY);
				z = (int) Math.floor(pl.posZ);
				boolean f =false;
				for(int i = 0;i<4 && !f;i++)
				{
					if(pl.worldObj.getBlock(x, y-i, z) == TardisMod.schemaCoreBlock)
					{
						f = true;
						y = y-i;
					}
				}
			}
			
			if(astring.length >= 1)
			{
				if(save(name,pl,pl.worldObj,x,y,z))
					sendString(pl,"Schema saved");
			}
		}
	}
}
