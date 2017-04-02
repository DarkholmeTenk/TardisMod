package tardis.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;

public class SchemaCommand extends AbstractCommandNew
{
	public SchemaCommand()
	{
		super(new SchemaGiveCommand(), new SchemaSaveCommand(), new SchemaLoadCommand(), new SchemaReloadCommand(), new SchemaRemoveCommand());
	}

	@Override
	public String getCommandName()
	{
		return "tardisschema";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("tschem");
		list.add("tschema");
	}

	@Override
	public boolean process(ICommandSender sen, List<String> strList)
	{
		return false;
	}

}
