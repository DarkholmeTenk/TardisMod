package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;

import tardis.TardisMod;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.SchemaCoreTileEntity;

public class SchemaReloadCommand extends AbstractCommandNew
{
	@Override
	public String getCommandName()
	{
		return "reload";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("refresh");
	}

	@Override
	public boolean process(ICommandSender sen, List<String> astring)
	{
		if(sen instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)sen;
			if(astring.size() == 4)
			{
				try
				{
					int x = Integer.parseInt(astring.get(0));
					int y = Integer.parseInt(astring.get(1));
					int z = Integer.parseInt(astring.get(2));
					int facing = Integer.parseInt(astring.get(3));
					String[] schemas = TardisMod.schemaHandler.getSchemas();
					String[] consoles = TardisMod.schemaHandler.getSchemas(true);
					ArrayList<String> schemaList = new ArrayList();
					for(String s : schemas)
						schemaList.add(s);
					for(String s : consoles)
						schemaList.add(s);
					for(String s : schemaList)
					{
						Helper.loadSchema(s, pl.worldObj, x, y, z, facing);
						SchemaSaveCommand.save(s, pl, pl.worldObj, x, y, z);
						TileEntity te = pl.worldObj.getTileEntity(x, y, z);
						if(te instanceof SchemaCoreTileEntity)
							((SchemaCoreTileEntity)te).remove(false);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
//		ConsoleTileEntity.refreshCategories();
		EngineTileEntity.refreshAvailableConsoleRooms();
		sendString(sen,"Schemas reloaded");
		return true;
	}
}
