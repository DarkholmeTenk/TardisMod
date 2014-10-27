package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.command.ICommandSender;

public class TardisXpCommand extends TardisAbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisxp";
	}

	@Override
	public void addAliases(List<String> aliases)
	{
		aliases.add("txp");
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}
	
	private void xp(String dest, double amount)
	{
		Integer dim = Helper.toInt(dest,0);
		if(dim == 0)
			dim = TardisMod.plReg.getDimension(dest);
		if(dim != null && dim != 0)
		{
			TardisCoreTileEntity core = Helper.getTardisCore(dim);
			xp(core,amount);
		}
	}
	
	private void xp(TardisCoreTileEntity core, double amount)
	{
		if(core != null && amount != 0)
			core.addXP(amount);
	}

	@Override
	public void commandBody(ICommandSender comsen, String[] astring)
	{
		if(astring.length == 2)
			xp(astring[0],Helper.toDouble(astring[1],0));
		else if(astring.length == 1)
			xp(comsen.getCommandSenderName(),Helper.toDouble(astring[0], 0));
		else
			getCommandUsage(comsen);
	}

}
