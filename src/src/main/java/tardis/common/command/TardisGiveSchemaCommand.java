package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TardisGiveSchemaCommand extends TardisAbstractCommand
{
	
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
	public void addAliases(List<String> aliases)
	{
		aliases.add("tschema");
		aliases.add("tschem");
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
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
}
