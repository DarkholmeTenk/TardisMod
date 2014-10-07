package tardis.core.commands;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.core.TardisOutput;
import tardis.core.schema.TardisPartBlueprint;
import tardis.tileents.TardisConsoleTileEntity;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class TardisSaveSchemaCommand implements ICommand
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
		return "tardissave";
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
	
	private boolean save(String name, World w, int x, int y, int z)
	{
		try
		{
			TardisPartBlueprint bp = new TardisPartBlueprint(w, name, x,y,z);
			File saveFile = TardisMod.configHandler.getSchemaFile(name);
			bp.saveTo(saveFile);
			TardisConsoleTileEntity.refreshSchemas();
			return true;
		}
		catch(Exception e)
		{
			TardisOutput.print("TSSC", "ERROR:" + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
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
					pl.addChatMessage("Totally not numbers");
				}
				
			}
			else if(astring.length == 1)
			{
				name = astring[0];
				x = (int) Math.floor(pl.posX);
				y = (int) Math.floor(pl.posY);
				z = (int) Math.floor(pl.posZ);
			}
			
			if(astring.length >= 1)
			{
				if(save(name,pl.worldObj,x,y,z))
					pl.addChatMessage("Schema saved");
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
		// TODO Auto-generated method stub
		return false;
	}

}
