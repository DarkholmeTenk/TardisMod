package tardis.common.integration.other;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;

public class CofHCore
{
	public static final String modname = "CoFHCore";

	private static Boolean cofh = null;
	public static boolean isCOFHInstalled()
	{
		if(cofh == null) cofh = Loader.isModLoaded(modname);
		return cofh;
	}
	
	public static boolean isItemElectric(ItemStack s){
		if(isCOFHInstalled() && s.getItem() != null){
			if(s.getItem() instanceof IEnergyContainerItem) return true;
		}
		return false;
	}
}
