package tardis.common.integration.other;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.IElectricItem;

public class IC2
{
	public static final String modname="IC2";

	private static Boolean ic2 = null;
	public static boolean isIC2Installed()
	{
		if(ic2 == null) ic2 = Loader.isModLoaded(modname);
		return ic2;
	}
	
	public static boolean isItemElectric(ItemStack s){
		if(isIC2Installed() && s.getItem() != null){
			if(s.getItem() instanceof IElectricItem) return true;
		}
		return false;
	}

	@Optional.Method(modid=modname)
	public static void post(TileEntity te, boolean on)
	{
		if(on)
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) te));
		else
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) te));
	}	
}
