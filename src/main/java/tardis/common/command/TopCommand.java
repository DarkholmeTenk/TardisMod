package tardis.common.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;

import tardis.TardisMod;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;

public class TopCommand extends AbstractCommandNew
{

	@Override
	public String getCommandName()
	{
		return "tardistop";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("ttop");
		list.add("tscore");
	}

	@Override
	public void getCommandUsage(ICommandSender sen, String tc)
	{
		sendString(sen, tc + " [page]");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender comSen)
	{
		return true;
	}

	private void printTDS(ICommandSender sen, int num, TardisDataStore ds)
	{
		String str = String.format("%02d - L:%3d - %s", num+1, ds.getLevel(), ds.getOwnerName());
		sendString(sen,str);
	}

	private static final int perPage = 5;
	@Override
	public boolean process(ICommandSender sen, List<String> strList)
	{
		int page = 0;
		if(strList.size() == 1)
		{
			try{page = Math.max(0,Integer.parseInt(strList.get(0))-1);}
			catch(NumberFormatException e)
			{
				sendString(sen, "Either no arguments, or a number for the page");
				return false;
			}
		}
		List<TardisDataStore> stores = new ArrayList(Helper.getAllDataStores());
		stores.sort(tdsComp);
		if(sen instanceof EntityPlayer)
		{
			TardisDataStore tds = TardisMod.plReg.getDataStore((EntityPlayer) sen);
			if(tds != null)
			{
				sendString(sen,"Your TARDIS:");
				printTDS(sen, stores.indexOf(tds), tds);
				sendString(sen,"----------");
			}
		}
		int s = page * perPage;
		int m = Math.min((page + 1) * perPage,stores.size());

		if(s >= m)
		{
			sendString(sen,"No page exists, you have gone too far");
			return false;
		}
		int numPages =  ((stores.size()-1) / perPage) + 1;
		sendString(sen,String.format("Page: %d/%d", (page+1), numPages));
		for(;s<m;s++)
		{
			printTDS(sen,s,stores.get(s));
		}
		return true;
	}

	private static Comparator<TardisDataStore> tdsComp = new Comparator<TardisDataStore>(){

		@Override
		public int compare(TardisDataStore a, TardisDataStore b)
		{
			int c = Integer.compare(a.getLevel(), b.getLevel());
			if(c == 0)
				return -Double.compare(a.getXP(), b.getXP());
			return -c;
		}

	};
}
