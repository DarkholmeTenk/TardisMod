package tardis.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.TardisMod;
import tardis.common.TMRegistry;

public class SchemaGiveCommand extends AbstractCommandNew
{

	@Override
	public String getCommandName()
	{
		return "give";
	}

	@Override
	public void getCommandUsage(ICommandSender sen, String totalCommand)
	{
		sendString(sen, totalCommand +" <schemaname>");
	}

	@Override
	public void getAliases(List<String> aliases)
	{
	}

	@Override
	public boolean process(ICommandSender comSen, List<String> astring)
	{
		if(comSen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)comSen;
			if(astring.size() == 1)
			{
				String name = astring.get(0);
				if(TardisMod.schemaHandler.getSchemaFile(name).exists())
				{
					ItemStack is = new ItemStack(TMRegistry.schemaItem,1);
					if(is.stackTagCompound == null)
						is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setString("schemaName",name);
					WorldHelper.giveItemStack(pl, is);
				}
				else
				{
					sendString(pl,"Schema " + name + " does not exist");
				}
				return true;
			}
			return false;
		}
		sendString(comSen,"Command only usable by players");
		return true;
	}
}
