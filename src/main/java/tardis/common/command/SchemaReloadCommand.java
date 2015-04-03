package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.SchemaCoreTileEntity;

public class SchemaReloadCommand extends AbstractCommand
{
	@Override
	public String getCommandName()
	{
		return "tardisreload";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("tardisrefresh");
		aliases.add("treload");
		aliases.add("trefresh");
		aliases.add("trel");
		aliases.add("tref");
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] astring)
	{
		if(icommandsender instanceof EntityPlayerMP)
		{
			EntityPlayerMP pl = (EntityPlayerMP)icommandsender;
			if(astring.length == 4)
			{
				try
				{
					int x = Integer.parseInt(astring[0]);
					int y = Integer.parseInt(astring[1]);
					int z = Integer.parseInt(astring[2]);
					int facing = Integer.parseInt(astring[3]);
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
							((SchemaCoreTileEntity)te).remove();
					}
				}
				catch(Exception e)
				{
				}
			}
		}
		ConsoleTileEntity.refreshSchemas();
		EngineTileEntity.refreshAvailableConsoleRooms();
	}
}
