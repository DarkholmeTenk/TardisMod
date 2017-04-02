package tardis.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommand;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.common.TMRegistry;
import tardis.common.core.TardisOutput;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.TardisWorldProvider;
import tardis.common.tileents.CoreTileEntity;

public class RepairCoreCommand extends AbstractCommand
{
	@Override
	public String getCommandName()
	{
		return "tardisrepair";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/tardisrepair <dimID> <new owner> <rooms> <energy>";
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
		if(ServerHelper.isClient())
			return;
		String newOwner = null;
		int worldID = 0;
		int numRooms = 0;
		int energy = 0;

		if(comSen instanceof EntityPlayer)
		{
			TardisOutput.print("TRCC", "World?"+ServerHelper.isServer());
			worldID  = ((EntityPlayer) comSen).worldObj.provider.dimensionId;
			newOwner = ServerHelper.getUsername((EntityPlayer) comSen);
		}

		boolean total = false;
		int o = 0;
		if(astring.length > 0)
			if(astring[0].equals("total"))
				total = true;

		if(astring.length >= 4)
		{
			worldID = MathHelper.toInt(astring[total ? 1 : 0], 0);
			newOwner = astring[total ? 2 : 1];
			o = total ? 3 : 2;
		}
		if(astring.length >= 2)
		{
			numRooms = MathHelper.toInt(astring[o], 0);
			energy   = MathHelper.toInt(astring[o+1], 0);
		}
		else
		{
			CoreTileEntity tce = Helper.getTardisCore(worldID);
			if(tce != null)
			{
				numRooms = tce.getNumRooms();
				energy   = tce.getArtronEnergy();
			}
		}

		if(newOwner != null)
		{
			TardisOutput.print("TRCC", "Repairing: setting owner to "+ newOwner);
			World world = WorldHelper.getWorldServer(worldID);
			CoreTileEntity tce = Helper.getTardisCore(world);
			TardisDataStore ds = Helper.getDataStore(worldID);
			if((tce == null) && (world.provider instanceof TardisWorldProvider))
			{
				world.setBlock(Helper.tardisCoreX, Helper.tardisCoreY, Helper.tardisCoreZ, TMRegistry.tardisCoreBlock);
				tce = Helper.getTardisCore(world);
			}
			//TardisOutput.print("TRCC", "Repairing: setting owner to "+ newOwner + ","+tce.worldObj.isRemote);
			if((tce != null) && (ds != null))
			{
				tce.sendUpdate();
				ds.sendUpdate();
				if(total)
				{
					tce.removeAllRooms(true);
					Helper.generateTardisInterior(worldID, newOwner, ds.getExterior());
				}
				tce.commandRepair(newOwner, numRooms, energy);
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
