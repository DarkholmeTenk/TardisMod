package tardis.common.dimension;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent.Load;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TardisDimensionHandler
{
	private volatile ArrayList<Integer>	dimensionIDs	= new ArrayList<Integer>();
	private static ConfigFile			config			= null;
	private static ArrayList<Integer>	blacklistedIDs;
	private static ArrayList<String>	blacklistedNames;

	static
	{
		if(config == null)
			refreshConfigs();
	}

	public static void refreshConfigs()
	{
		if(config == null)
			config = TardisMod.configHandler.registerConfigNeeder("Dimensions");
		String ids = config.getString("Blacklisted Dimension IDs", "",
				"A comma separated blacklist of dimension ids which no tardis should be able to reach");
		String[] splitIDs = ids.split(",");
		blacklistedIDs = new ArrayList(splitIDs.length);
		for(String s : splitIDs)
		{
			int t = MathHelper.toInt(s, 0);
			if(t != 0)
				blacklistedIDs.add(t);
		}
		String names = config.getString("Blacklisted Dimension Names", "",
				"A comma separated list of dimension names which no tardis should be able to reach");
		String[] splitNames = names.split(",");
		blacklistedNames = new ArrayList(splitNames.length);
		for(String s : splitNames)
			blacklistedNames.add(s);
	}

	private synchronized void addDimension(int id)
	{
		try
		{
			World w = WorldHelper.getWorld(id);
			if(Helper.isTardisWorld(w))
				return;
			if (!dimensionIDs.contains(id))
			{
				if (blacklistedIDs.contains(id) || blacklistedNames.contains(WorldHelper.getDimensionName(w)))
					return;
				dimensionIDs.add(id);
				TardisOutput.print("TDimH", "Adding dimension: " + id + ", " + WorldHelper.getDimensionName(w));
				cleanUp();
			}
		}
		catch(Exception e)
		{
			TardisOutput.print("TDimH", "Failed to add dimension: " + id);
		}
	}

	private synchronized void addDimension(World w)
	{
		if (Helper.isTardisWorld(w))
			return;
		int id = WorldHelper.getWorldID(w);
		if (!dimensionIDs.contains(id))
		{
			if (blacklistedIDs.contains(id) || blacklistedNames.contains(WorldHelper.getDimensionName(w)))
				return;
			dimensionIDs.add(id);
			TardisOutput.print("TDimH", "Adding dimension: " + id + ", " + WorldHelper.getDimensionName(w));
			cleanUp();
		}
	}

	private synchronized void cleanUp()
	{
		HashSet<Integer> uniques = new HashSet<Integer>();
		Iterator<Integer> iter = dimensionIDs.iterator();
		while (iter.hasNext())
		{
			Integer i = iter.next();
			if ((i == null) || uniques.contains(i))
				iter.remove();
			else
				uniques.add(i);
		}
	}

	public void findDimensions()
	{
		if (!ServerHelper.isServer())
			return;
		WorldServer[] loadedWorlds = DimensionManager.getWorlds();
		for (WorldServer w : loadedWorlds)
			addDimension(w);
		try
		{
			Field f = DimensionManager.class.getDeclaredField("dimensions");
			f.setAccessible(true);
			Object o = f.get(null);
			if(o instanceof HashMap)
			{
				HashMap hm = (HashMap)o;
				for(Object in : hm.keySet())
				{
					if(in instanceof Integer)
						addDimension((Integer)in);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void loadWorld(Load loadEvent)
	{
		World w = loadEvent.world;
		if (w != null)
			addDimension(w);
	}

	public int numDims()
	{
		return Math.max(1, dimensionIDs.size());
	}

	public Integer getControlFromDim(int dim)
	{
		if (!ServerHelper.isServer())
			return 0;
		cleanUp();
		if (dimensionIDs.contains(dim))
			return dimensionIDs.indexOf(dim);
		else
		{
			World w = WorldHelper.getWorldServer(dim);
			if (w != null)
			{
				addDimension(w);
				return dimensionIDs.indexOf(w);
			}
		}
		if (dimensionIDs.contains(0))
			return dimensionIDs.indexOf(0);
		return 0;
	}

	public Integer getDimFromControl(int control)
	{
		if (!ServerHelper.isServer())
			return 0;
		int index = MathHelper.clamp(control, 0, dimensionIDs.size() - 1);
		int dim = dimensionIDs.get(index);
		return dim;
	}
}
