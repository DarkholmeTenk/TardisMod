package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class TardisRepairCoreCommand implements ICommand
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
		return "tardisrepair";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return null;
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
	{
		String newOwner = null;
		int worldID = 0;
		int numRooms = 0;
		int energy = 0;
		
		if(comSen instanceof EntityPlayerMP)
		{
			worldID  = ((EntityPlayerMP) comSen).worldObj.provider.dimensionId;
			newOwner = ((EntityPlayerMP) comSen).username;
		}
		
		int o = 0;
		if(astring.length == 4)
		{
			worldID = Helper.toInt(astring[0], 0);
			newOwner = astring[1];
			o = 2;
		}
		if(astring.length >= 2)
		{
			numRooms = Helper.toInt(astring[o], 0);
			energy   = Helper.toInt(astring[o+1], 0);
		}
		else
		{
			TardisCoreTileEntity tce = Helper.getTardisCore(worldID);
			if(tce != null)
			{
				numRooms = tce.getNumRooms();
				energy   = tce.getEnergy();
			}
		}
		
		if(newOwner != null)
		{
			World world = MinecraftServer.getServer().worldServerForDimension(worldID);
			if(world.provider instanceof TardisWorldProvider)
			{
				TardisCoreTileEntity tce = Helper.getTardisCore(worldID);
				if(tce == null)
					world.setBlock(Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, TardisMod.tardisCoreBlock.blockID);
				tce = Helper.getTardisCore(worldID);
				tce.repair(newOwner, numRooms, energy);
			}
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

}
