package tardis.common.command;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TardisRepairCoreCommand extends TardisAbstractCommand
{
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
	public void addAliases(List<String> aliases)
	{
		aliases.add("trep");
		aliases.add("trepair");
	}

	@Override
	public void commandBody(ICommandSender comSen, String[] astring)
	{
		if(!Helper.isServer())
			return;
		String newOwner = null;
		int worldID = 0;
		int numRooms = 0;
		int energy = 0;
		
		if(comSen instanceof EntityPlayer)
		{
			TardisOutput.print("TRCC", "WOrld?"+((EntityPlayerMP)comSen).worldObj.isRemote);
			worldID  = ((EntityPlayer) comSen).worldObj.provider.dimensionId;
			newOwner = Helper.getUsername((EntityPlayer) comSen);
		}
		
		boolean total = false;
		int o = 0;
		if(astring.length > 0)
			if(astring[0].equals("total"))
				total = true;
		
		if(astring.length >= 4)
		{
			worldID = Helper.toInt(astring[total ? 1 : 0], 0);
			newOwner = astring[total ? 2 : 1];
			o = total ? 3 : 2;
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
				world.setBlock(Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, TardisMod.tardisCoreBlock);
				tce = Helper.getTardisCore(world);
			}
			//TardisOutput.print("TRCC", "Repairing: setting owner to "+ newOwner + ","+tce.worldObj.isRemote);
			if(tce != null)
			{
				if(total)
				{
					tce.removeAllRooms(true);
					Helper.generateTardisInterior(worldID, newOwner, tce.getExterior());
				}
				tce.repair(newOwner, numRooms, energy);
			}
			else if(total)
					Helper.generateTardisInterior(worldID, newOwner, null);
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
