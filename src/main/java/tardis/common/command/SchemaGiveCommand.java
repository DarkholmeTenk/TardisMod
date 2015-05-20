package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;

public class SchemaGiveCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisschema";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/tardisschema <schemaname>";
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
				if(TardisMod.schemaHandler.getSchemaFile(name).exists())
				{
					ItemStack is = new ItemStack(TardisMod.schemaItem,1);
					if(is.stackTagCompound == null)
						is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setString("schemaName",name);
					WorldHelper.giveItemStack(pl, is);
				}
				else
				{
					sendString(pl,"Schema " + name + " does not exist");
				}
			}
		}
	}
}
