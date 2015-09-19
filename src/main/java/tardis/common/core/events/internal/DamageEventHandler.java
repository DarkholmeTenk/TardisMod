package tardis.common.core.events.internal;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import tardis.api.IControlMatrix;
import tardis.common.core.events.TardisControlEvent;
import tardis.common.core.events.TardisTakeoffEvent;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.damage.TardisDamageSystem;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
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
	public void handleTakeoffEvent(TardisTakeoffEvent event)
	{
		CoreTileEntity core = event.getCore();
		TardisDataStore ds = Helper.getDataStore(core);
		if(ds == null) return;
		if(ds.damage.getHull() < TardisDamageSystem.hullToFly)
			event.cancel(true, "TARDIS has sustained too much damage to take off");
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
			CoreTileEntity core = Helper.getTardisCore(te);
			if((ds == null) || (core == null)) return;
			if(te instanceof EngineTileEntity) handleEnginePress(ds,core,(EngineTileEntity)te,event);
			if(te instanceof ConsoleTileEntity) handleConsolePress(ds,core,	 (ConsoleTileEntity)te,event);
		}
	}

	public void handleEnginePress(TardisDataStore ds, CoreTileEntity core, EngineTileEntity eng, TardisControlEvent event)
	{

	}

	public void handleConsolePress(TardisDataStore ds, CoreTileEntity core, ConsoleTileEntity con, TardisControlEvent event)
	{
		EntityPlayer pl = event.player;
		if((event.control != 901) && (core.getNumRooms() > core.getMaxNumRooms()))
		{
			event.setCanceled(true);
			ServerHelper.sendString(pl, "You have too many rooms to operate the TARDIS");
			ServerHelper.sendString(pl, "Upgrade the TARDIS or delete rooms");
			return;
		}
		int hull = ds.damage.getHull();
		if(hull > TardisDamageSystem.hullToControl) return;
		int cid = event.control;
		if(!core.inFlight())
		{
			if(con.isMovementControl(cid) || ((cid >= 40) && (cid <= 42)) || (cid == 902) || (cid == 903) || (cid >= 1000))
				event.setCanceled(true);
		}
		else
		{
			if(con.isMovementControl(cid) || ((cid >= 40) && (cid <= 41)) || (cid == 902) || (cid == 903))
				event.setCanceled(true);
		}
		if((cid != -1) && event.isCanceled())
			ServerHelper.sendString(pl, "The control refuses to move due to damage");
	}
}
