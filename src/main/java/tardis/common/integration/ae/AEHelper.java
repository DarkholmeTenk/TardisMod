package tardis.common.integration.ae;

import net.minecraft.item.ItemStack;

import appeng.api.AEApi;
import appeng.api.IAppEngApi;
import appeng.api.implementations.items.IAEItemPowerStorage;
import cpw.mods.fml.common.Loader;

public class AEHelper
{
	public static final String modname = "appliedenergistics2";
	public static IAppEngApi	aeAPI	= null;

	private static Boolean ae2 = null;

	public static void init()
	{
		try
		{
			AEHelper.aeAPI = AEApi.instance();
		}
		catch (Exception e)
		{
			System.err.println("Error loading AE API");
		}
		;
	}

	public static boolean isAE2Installed()
	{
		if(ae2 == null) ae2 = Loader.isModLoaded(modname);
		return ae2;
	}

	public static boolean isItemElectric(ItemStack s){
		if(isAE2Installed() && (s.getItem() != null)){
			if(s.getItem() instanceof IAEItemPowerStorage) return true;
		}
		return false;
	}
}
