package tardis.common.command;

import java.util.List;

import tardis.common.core.Helper;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.CoreTileEntity;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SwitchConsoleCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisconsole";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public void addAliases(List<String> list)
	{
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] astring)
	{
		if(!Helper.isServer() || astring.length == 0)
			return;
		World w = null;
		if(astring.length == 2)
			w = Helper.getWorldServer(Helper.toInt(astring[0], 0));
		else if(icommandsender instanceof EntityPlayer)
			w = ((EntityPlayer)icommandsender).worldObj;
		
		if(w == null || !(w.provider instanceof TardisWorldProvider))
		{
			sendString(icommandsender,"Invalid world");
			return;
		}
		
		String name = astring.length == 2 ? astring[1] : astring[0];
		CoreTileEntity core = Helper.getTardisCore(w);
		if(core != null)
			core.loadConsoleRoom(name);
	}

}
