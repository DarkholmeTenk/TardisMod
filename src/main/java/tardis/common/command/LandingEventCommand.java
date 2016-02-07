package tardis.common.command;

import io.darkcraft.darkcore.mod.abstracts.AbstractCommandNew;
import io.darkcraft.darkcore.mod.datastore.HashMapList;

import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import tardis.common.core.events.TardisLandingEvent;
import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LandingEventCommand<K, V> extends AbstractCommandNew
{
	private static HashMapList<String, TardisLandingEvent> list = new HashMapList();

	public static void clear()
	{
		list.clear();
	}

	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public String getCommandName()
	{
		return "tardisevent";
	}

	@Override
	public void getAliases(List<String> list)
	{
		list.add("tevent");
		list.add("teventlist");
		list.add("tardiseventlist");
		list.add("tel");
	}

	private void print(ICommandSender sen, String n)
	{
		Iterator iter = list.iterator(n);
		while(iter.hasNext())
		{
			Object o = iter.next();
			sendString(sen, o.toString());
		}
	}

	@Override
	public boolean process(ICommandSender sen, List<String> strList)
	{
		if(strList.size() == 0)
			for(String n : list.keySet())
				print(sen, n);
		else if(strList.size() == 1)
			print(sen, strList.get(0));
		else
			return false;
		return true;
	}

	@SubscribeEvent
	public void event(TardisLandingEvent event)
	{
		CoreTileEntity core = event.getCore();
		if(core == null) return;
		String o = core.getOwner();
		if(o == null) return;
		list.add(o, event);
		List l = list.get(o);
		while(l.size() > 6)
			l.remove(0);
	}

}
