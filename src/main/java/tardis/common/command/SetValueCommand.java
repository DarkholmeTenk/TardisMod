package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;

public class SetValueCommand extends AbstractCommand
{

	@Override
	public String getCommandName()
	{
		return "tardisset";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return "/tardisset [dim] <variable> <value>";
	}

	@Override
	public void addAliases(List<String> list)
	{
		list.add("tset");
	}

	@Override
	public void commandBody(ICommandSender icommandsender, String[] args)
	{
		int dim = 0;
		if(icommandsender instanceof EntityPlayer)
			dim = WorldHelper.getWorldID((EntityPlayer)icommandsender);
		if((args.length < 2) || (args.length > 3))
			return;
		if(args.length == 3)
			dim = MathHelper.toInt(args[0], dim);
		if(!Helper.isTardisWorld(dim)) return;
		String variable = args[args.length-2];
		String value = args[args.length - 1];
		int iVal = MathHelper.toInt(value, 0);
		if(variable.equalsIgnoreCase("energy") || variable.equalsIgnoreCase("en"))
			setEnergy(dim, iVal);
		if(variable.equalsIgnoreCase("shields") || variable.equalsIgnoreCase("sh"))
			setShields(dim, iVal);
		if(variable.equalsIgnoreCase("hull") || variable.equalsIgnoreCase("hu"))
			setHull(dim, iVal);
	}

	private void setEnergy(int dim, int val)
	{
		CoreTileEntity core = Helper.getTardisCore(dim);
		if(core == null) return;
		int en = core.getArtronEnergy();
		if(en < val)
			core.addArtronEnergy(val - en, false);
		if(en > val)
			core.takeArtronEnergy(en - val, false);
	}

	private void setShields(int dim, int val)
	{
		TardisDataStore ds = Helper.getDataStore(dim);
		if(ds == null) return;
		ds.damage.setShields(val);
	}

	private void setHull(int dim, int val)
	{
		TardisDataStore ds = Helper.getDataStore(dim);
		if(ds == null) return;
		ds.damage.setHull(val);
	}

}
