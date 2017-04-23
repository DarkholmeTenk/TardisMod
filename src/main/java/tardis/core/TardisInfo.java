package tardis.core;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;

public class TardisInfo
{
	public final int dimension;
	private WeakReference<ConsoleTileEntity> console;
	private WeakReference<CoreTileEntity> core;
	private WeakReference<EngineTileEntity> engine;
	private WeakReference<TardisDataStore> ds;

	private TardisInfo(int dimension, ConsoleTileEntity console, CoreTileEntity core, EngineTileEntity engine, TardisDataStore ds)
	{
		this.dimension = dimension;
		this.console = wrap(console);
		this.core = wrap(core);
		this.engine = wrap(engine);
		this.ds = wrap(ds);
	}

	public CoreTileEntity getCore()
	{
		if((core != null) && (core.get() != null))
			return core.get();
		return (core = new WeakReference<>(Helper.getTardisCore(dimension))).get();
	}

	public ConsoleTileEntity getConsole()
	{
		if((console != null) && (console.get() != null))
			return console.get();
		return (console = new WeakReference<>(Helper.getTardisConsole(dimension))).get();
	}

	public <T> Optional<T> getPanel(Class<T> clazz)
	{
		return getConsole().getPanel(clazz);
	}

	public EngineTileEntity getEngine()
	{
		if((engine != null) && (engine.get() != null))
			return engine.get();
		return (engine = new WeakReference<>(Helper.getTardisEngine(dimension))).get();
	}

	public TardisDataStore getDataStore()
	{
		if((ds != null) && (ds.get() != null))
			return ds.get();
		return (ds = new WeakReference<>(Helper.getDataStore(dimension))).get();
	}

	private static <T> WeakReference<T> wrap(T t)
	{
		if(t == null)
			return null;
		return new WeakReference<>(t);
	}

	private static Map<Integer, TardisInfo> clientMap = new HashMap<>();
	private static Map<Integer, TardisInfo> serverMap = new HashMap<>();

	private static Map<Integer, TardisInfo> getMap()
	{
		if(ServerHelper.isServer())
			return serverMap;
		return clientMap;
	}

	private static synchronized TardisInfo get(int dimension, ConsoleTileEntity console, CoreTileEntity core, EngineTileEntity engine, TardisDataStore ds)
	{
		Map<Integer, TardisInfo> map = getMap();
		if(map.containsKey(dimension))
			return map.get(dimension);
		TardisInfo info = new TardisInfo(dimension,console,core,engine,ds);
		map.put(dimension, info);
		return info;
	}

	public static TardisInfo get(int dimension)
	{
		return get(dimension, null, null, null, null);
	}

	public static TardisInfo get(CoreTileEntity core)
	{
		return get(WorldHelper.getWorldID(core), null, core, null, null);
	}

	public static TardisInfo get(ConsoleTileEntity console)
	{
		return get(WorldHelper.getWorldID(console), console, null, null, null);
	}

	public static TardisInfo get(EngineTileEntity engine)
	{
		return get(WorldHelper.getWorldID(engine), null, null, engine, null);
	}

	public static TardisInfo get(TardisDataStore ds)
	{
		return get(ds.getDimension(), null, null, null, ds);
	}
}
