package tardis.common.core;

import io.darkcraft.darkcore.mod.config.CType;
import io.darkcraft.darkcore.mod.config.ConfigFile;
import io.darkcraft.darkcore.mod.config.ConfigItem;
import io.darkcraft.darkcore.mod.helpers.MathHelper;
import tardis.TardisMod;

public class TardisOutput
{
	public enum Priority
	{
		NONE, ERROR, WARNING, INFO, DEBUG, OLDDEBUG;
		public static Priority get(int a)
		{
			Priority[] vals = values();
			if (a >= 0 && a < vals.length)
				return vals[a];
			return TardisMod.priorityLevel;
		}
	}

	public static Priority		defaultPriority	= Priority.INFO;
	public static ConfigFile	configFile		= null;

	private static boolean shouldDisplay(String descriptor)
	{
		if (!(descriptor.equals("CF") || descriptor.equals("TCH") || descriptor.equals("TM")))
		{
			if (configFile == null)
				if (TardisMod.inited && TardisMod.configHandler != null)
					configFile = TardisMod.configHandler.registerConfigNeeder("DebugOutput");
			if (configFile != null)
				return configFile.getConfigItem(new ConfigItem(descriptor, CType.BOOLEAN, true)).getBoolean();
		}
		return true;
	}

	public static void print(String descriptor, String message, Priority prio)
	{
		if (prio.ordinal() <= TardisMod.priorityLevel.ordinal())
		{
			if (!shouldDisplay(descriptor))
				return;
			String toDisplay = "[TM][" + descriptor + "]" + message;
			if (prio.equals(Priority.ERROR))
				System.err.println(toDisplay);
			else
				System.out.println(toDisplay);
		}
	}

	public static void print(String descriptor, Object toStr, String message, Priority prio)
	{
		print(descriptor, "[" + toStr.toString() + "]" + message, prio);
	}

	public static void print(String descriptor, String message)
	{
		print(descriptor, message, defaultPriority);
	}

	public static void print(String descriptor, Object toStr, String message)
	{
		print(descriptor, toStr, message, defaultPriority);
	}

	public static Priority getPriority(int outputPriority)
	{
		int p = MathHelper.clamp(outputPriority, 0, Priority.values().length - 1);
		return Priority.get(p);
	}
}
