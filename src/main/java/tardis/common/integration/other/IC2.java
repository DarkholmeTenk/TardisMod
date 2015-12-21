package tardis.common.integration.other;

import cpw.mods.fml.common.Loader;

public class IC2
{
	public static final String modname="IC2";

	private static Boolean ic2 = null;
	public static boolean isIC2Installed()
	{
		if(ic2 == null) ic2 = Loader.isModLoaded(modname);
		return ic2;
	}
}
