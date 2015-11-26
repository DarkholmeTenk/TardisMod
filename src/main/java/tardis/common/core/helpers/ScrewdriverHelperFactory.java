package tardis.common.core.helpers;

import io.darkcraft.darkcore.mod.datastore.HalfMutablePair;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.common.items.SonicScrewdriverItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ScrewdriverHelperFactory
{
	public static final ScrewdriverHelperFactory										i			= new ScrewdriverHelperFactory();
	private static final HashMap<Integer, HalfMutablePair<Integer, ScrewdriverHelper>>	sHelperMap	= new HashMap();
	private static final HashMap<Integer, HalfMutablePair<Integer, ScrewdriverHelper>>	cHelperMap	= new HashMap();
	private static int																	t			= 0;

	private static HashMap<Integer, HalfMutablePair<Integer, ScrewdriverHelper>> hMap()
	{
		if(ServerHelper.isServer())
			return sHelperMap;
		else
			return cHelperMap;
	}

	private static int getNewID()
	{
		int next = Helper.rand.nextInt();
		Set<Integer> ids = hMap().keySet();
		while(ids.contains(next)) next = Helper.rand.nextInt();
		return next;
	}

	private static int getID(NBTTagCompound nbt)
	{
		if ((nbt == null) || !nbt.hasKey("uuid"))
			return getNewID();
		else
			return nbt.getInteger("uuid");
	}

	public static ScrewdriverHelper get(ItemStack is)
	{
		if(is == null) return null;
		if(!SonicScrewdriverItem.isScrewdriver(is)) return null;
		int id = getID(is.stackTagCompound);
		if(hMap().containsKey(id))
		{
			HalfMutablePair<Integer, ScrewdriverHelper> dataBundle = hMap().get(id);
			dataBundle.a = t;
			if(dataBundle.b.itemstack != is)
				dataBundle.b.setItemStack(is);
			return dataBundle.b;
		}
		else
		{
			ScrewdriverHelper helper = new ScrewdriverHelper(is, id);
			hMap().put(id, new HalfMutablePair(t, helper));
			return helper;
		}
	}

	public static ScrewdriverHelper get(NBTTagCompound nbt)
	{
		int id = getID(nbt);
		if(hMap().containsKey(id))
		{
			HalfMutablePair<Integer, ScrewdriverHelper> dataBundle = hMap().get(id);
			dataBundle.b.readFromNBT(nbt);
			return dataBundle.b;
		}
		else
		{
			ScrewdriverHelper helper = new ScrewdriverHelper(nbt, id);
			hMap().put(id, new HalfMutablePair(t, helper));
			return helper;
		}
	}

	public static ScrewdriverHelper get(NBTTagCompound nbt, String string)
	{
		if(nbt.hasKey(string))
			return get(nbt.getCompoundTag(string));
		return null;
	}

	public static ScrewdriverHelper getNew()
	{
		return get(new NBTTagCompound());
	}

	public static void destroy(ScrewdriverHelper screwHelper)
	{
		int id = screwHelper.id;
		hMap().remove(id);
	}

	private static final int	clearTime	= 5;
	/**
	 * Increments a timer and clears the itemstack of the helper to prevent memory leaks
	 */
	private void increment()
	{
		int clearRange = t - clearTime;
		for (Iterator<Integer> iter = hMap().keySet().iterator(); iter.hasNext();)
		{
			Integer i = iter.next();
			HalfMutablePair<Integer, ScrewdriverHelper> dataBundle = hMap().get(i);
			if (dataBundle.a < clearRange)
			{
				dataBundle.b.clear();
				dataBundle.a = t;
			}
		}
		t++;
	}

	@SubscribeEvent
	public void tickEvent(ServerTickEvent e)
	{
		if (e.phase == Phase.END) increment();
	}

	public void clear()
	{
		t = 0;
		hMap().clear();
	}
}
