package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class TardisGiveSchemaCommand implements ICommand
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
		return "tardisschema";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("tschema");
		aliases.add("tschem");
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)comSen;
			if(astring.length == 1)
			{
				String name = astring[0];
				if(TardisMod.configHandler.getSchemaFile(name).exists())
				{
					ItemStack is = new ItemStack(TardisMod.schemaItem,1);
					if(is.stackTagCompound == null)
						is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setString("schemaName",name);
					Helper.giveItemStack(pl, is);
				}
				else
				{
					pl.addChatMessage("Schema " + name + " does not exist");
				}
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring)
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
