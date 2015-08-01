package tardis.common.core.events.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import tardis.api.IControlMatrix;
import tardis.common.core.Helper;
import tardis.common.core.events.TardisControlEvent;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.TardisDamageSystem;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.EngineTileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DamageEventHandler
{
	public static DamageEventHandler i = new DamageEventHandler();

	public void register()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void handleControlPress(TardisControlEvent event)
	{
		if((event == null) || event.isCanceled()) return;
		IControlMatrix matrix = event.matrix;
		if(matrix == null) return;
		if(matrix instanceof TileEntity)
		{
			TileEntity te = (TileEntity)matrix;
			TardisDataStore ds = Helper.getDataStore(te);
			if(ds == null) return;
			if(te instanceof EngineTileEntity) handleEnginePress(ds,(EngineTileEntity)te,event);
			if(te instanceof ConsoleTileEntity) handleConsolePress(ds, (ConsoleTileEntity)te,event);
		}
	}

	public void handleEnginePress(TardisDataStore ds, EngineTileEntity eng, TardisControlEvent event)
	{

	}

	public void handleConsolePress(TardisDataStore ds, ConsoleTileEntity con, TardisControlEvent event)
	{
		int hull = ds.damage.getHull();
		if(hull > (TardisDamageSystem.maxHull / 2)) return;
		event.setCanceled(true);
		EntityPlayer pl = event.player;
	}
}
