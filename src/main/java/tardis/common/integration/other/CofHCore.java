package tardis.common.integration.other;

import cpw.mods.fml.common.Loader;

public class CofHCore
{
	public static final String modname = "CoFHCore";

	private static Boolean cofh = null;
	public static boolean isCOFHInstalled()
	{
		if(cofh == null) cofh = Loader.isModLoaded(modname);
		return cofh;
	}
}
