package tardis.common.core.events;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.lang.ref.WeakReference;

import tardis.common.core.helpers.Helper;
import tardis.common.tileents.CoreTileEntity;

public class TardisEvent extends TardisModEvent
{
	public final int worldID;
	private WeakReference<CoreTileEntity> core;

	public TardisEvent(CoreTileEntity _core)
	{
		worldID = WorldHelper.getWorldID(_core);
		core = new WeakReference<CoreTileEntity>(_core);
	}

	public CoreTileEntity getCore()
	{
		if((core != null) && (core.get() != null))
			return core.get();
		CoreTileEntity cte = Helper.getTardisCore(worldID);
		if(cte != null)
			core = new WeakReference<CoreTileEntity>(cte);
		return cte;
	}
}
