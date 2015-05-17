package tardis.common.dimension;

import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent.Load;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.CoreTileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TardisDimensionHandler
{
	private volatile ArrayList<Integer>			dimensionIDs			= new ArrayList<Integer>();
	private static ConfigFile					config					= null;
	private static ArrayList<Integer>			blacklistedIDs;
	private static ArrayList<String>			blacklistedNames;
	private static HashMap<Integer, Integer>	maxHeights				= new HashMap();
	private static HashMap<Integer, Integer>	minLevels				= new HashMap();
	private static HashMap<Integer, Integer>	energyCosts				= new HashMap();

	static
	{
		if (config == null) refreshConfigs();
	}

	private Thread								scanAfterDelayThread	= null;
	private Runnable							scanAfterDelay			=
		new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(30000);
						internalFindDimensions();
					}
					catch (Exception e)
					{
					}
				}
			}
		};

	private static void splitToTwoInts(String s, HashMap<Integer,Integer> toFill)
	{
		toFill.clear();
		String[] csvBlobs = s.split(",");
		for(String csvBlob : csvBlobs)
		{
			String[] intBlobs = csvBlob.split("\\|");
			if(intBlobs.length != 2)
				continue;
			try
			{
				int dim = Integer.parseInt(intBlobs[0]);
				int height = Integer.parseInt(intBlobs[1]);
				toFill.put(dim, height);
			}
			catch(NumberFormatException e){}
		}
	}

	public static void refreshConfigs()
	{
		if (config == null) config = TardisMod.configHandler.registerConfigNeeder("Dimensions");
		String ids = config.getString("Blacklisted Dimension IDs", "", "A comma separated blacklist of dimension ids which no tardis should be able to reach");
		String[] splitIDs = ids.split(",");
		blacklistedIDs = new ArrayList(splitIDs.length);
		for (String s : splitIDs)
		{
			int t = MathHelper.toInt(s, 0);
			if (t != 0) blacklistedIDs.add(t);
		}
		String names = config.getString("Blacklisted Dimension Names", "", "A comma separated list of dimension names which no tardis should be able to reach");
		String[] splitNames = names.split(",");
		blacklistedNames = new ArrayList(splitNames.length);
		for (String s : splitNames)
			blacklistedNames.add(s);

		String heightsStr = config.getString("Maximum heights", "-1|127", "A comma separated list of maximum heights in the form dimID|height", "E.g. -1|127 which sets the max height of the nether (dim -1) to 127");
		splitToTwoInts(heightsStr,maxHeights);

		String minLevelStr = config.getString("Minimum levels", "1|13,-1|5", "A comma separated list of minimum levels required to reach dimension","In the form dimID|level");
		splitToTwoInts(minLevelStr,minLevels);

		String enCostStr = config.getString("Energy costs", "1|5000,-1|3000", "A comma separated list of energy cost to reach dimension","In the form dimID|cost");
		splitToTwoInts(enCostStr,energyCosts);
	}

	public static int getMaxHeight(int dimID)
	{
		if(maxHeights.containsKey(dimID))
			return maxHeights.get(dimID);
		return 255;
	}

	public static int getEnergyCost(int dimID)
	{
		if(energyCosts.containsKey(dimID))
			return energyCosts.get(dimID);
		return CoreTileEntity.energyCostDimChange;
	}

	private synchronized void addDimension(int id)
	{
		try
		{
			World w = WorldHelper.getWorld(id);
			if (Helper.isTardisWorld(w)) return;
			if (!dimensionIDs.contains(id))
			{
				if (blacklistedIDs.contains(id) || blacklistedNames.contains(WorldHelper.getDimensionName(w))) return;
				dimensionIDs.add(id);
				TardisOutput.print("TDimH", "Adding dimension: " + id + ", " + WorldHelper.getDimensionName(w));
				cleanUp();
			}
		}
		catch (Exception e)
		{
			TardisOutput.print("TDimH", "Failed to add dimension: " + id);
		}
	}

	private synchronized void addDimension(World w)
	{
		if (Helper.isTardisWorld(w)) return;
		int id = WorldHelper.getWorldID(w);
		if (!dimensionIDs.contains(id))
		{
			if (blacklistedIDs.contains(id) || blacklistedNames.contains(WorldHelper.getDimensionName(w))) return;
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

	private synchronized void internalFindDimensions()
	{
		try
		{
			Field f = DimensionManager.class.getDeclaredField("dimensions");
			f.setAccessible(true);
			Object o = f.get(null);
			if (o instanceof Hashtable)
			{
				Hashtable hm = (Hashtable) o;
				for (Object in : hm.keySet())
				{
					if (in instanceof Integer) addDimension((Integer) in);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void scanAfterDelay()
	{
		if (scanAfterDelayThread == null)
		{
			scanAfterDelayThread = new Thread(scanAfterDelay);
			scanAfterDelayThread.start();
		}
	}

	public void findDimensions()
	{
		if (ServerHelper.isClient()) return;
		WorldServer[] loadedWorlds = DimensionManager.getWorlds();
		for (WorldServer w : loadedWorlds)
			addDimension(w);
		internalFindDimensions();
	}

	@SubscribeEvent
	public void loadWorld(Load loadEvent)
	{
		World w = loadEvent.world;
		if (w != null) addDimension(w);
	}

	public int numDims()
	{
		return Math.max(1, dimensionIDs.size());
	}

	private List<Integer> getDims(int level)
	{
		ArrayList<Integer> list = new ArrayList();
		for(Integer dim : dimensionIDs)
		{
			if(minLevels.containsKey(dim) && (minLevels.get(dim) > level))
				continue;
			list.add(dim);
		}
		return list;
	}

	public int numDims(int level)
	{
		return getDims(level).size();
	}

	public Integer getControlFromDim(int dim, int level)
	{
		if (ServerHelper.isClient()) return 0;
		cleanUp();
		List<Integer> dims = getDims(level);
		if (dimensionIDs.contains(dim))
		{
			if(dims.contains(dim))
				return dims.indexOf(dim);
		}
		else
		{
			World w = WorldHelper.getWorldServer(dim);
			if (w != null)
			{
				addDimension(w);
				return getControlFromDim(dim,level);
			}
		}
		if (dimensionIDs.contains(0)) return dimensionIDs.indexOf(0);
		return 0;
	}

	public Integer getDimFromControl(int control, int level)
	{
		if (ServerHelper.isClient()) return 0;
		List<Integer> dims = getDims(level);
		int index = MathHelper.clamp(control, 0, dims.size() - 1);
		int dim = dims.get(index);
		return dim;
	}
}
