package tardis.common.command;

import java.util.ArrayList;
import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
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
			if(((EntityPlayerMP)comSen).capabilities.isCreativeMode)
				return true;
			if(MinecraftServer.getServer().getConfigurationManager().getOps().contains(((EntityPlayerMP)comSen).username))
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
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("trep");
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender comSen, String[] astring)
	{
		String newOwner = null;
		int worldID = 0;
		int numRooms = 0;
		int energy = 0;
		
		if(comSen instanceof EntityPlayer)
		{
			TardisOutput.print("TRCC", "WOrld?"+((EntityPlayerMP)comSen).worldObj.isRemote);
			worldID  = ((EntityPlayer) comSen).worldObj.provider.dimensionId;
			newOwner = ((EntityPlayer) comSen).username;
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
			TardisOutput.print("TRCC", "Repairing: setting owner to "+ newOwner);
			World world = Helper.getWorldServer(worldID);
			TardisCoreTileEntity tce = Helper.getTardisCore(world);
			if(tce == null && world.provider instanceof TardisWorldProvider)
			{
				world.setBlock(Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, TardisMod.tardisCoreBlock.blockID);
				tce = Helper.getTardisCore(world);
			}
			//TardisOutput.print("TRCC", "Repairing: setting owner to "+ newOwner + ","+tce.worldObj.isRemote);
			if(tce != null)
				tce.repair(newOwner, numRooms, energy);
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
