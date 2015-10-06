package tardis.client.renderer.portal;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;

import java.util.WeakHashMap;

import net.minecraft.client.renderer.WorldRenderer;
import tardis.common.tileents.MagicDoorTileEntity;

public class PortalWorldRegistry
{
	private static WeakHashMap<MagicDoorTileEntity, PortalWorld> doorMap = new WeakHashMap();
	private static WeakHashMap<PortalWorld, WorldRenderer> rend = new WeakHashMap();

	public static PortalWorld getWorld(MagicDoorTileEntity door)
	{
		if(doorMap.containsKey(door))
			return doorMap.get(door);
		PortalWorld pw = new PortalWorld(door.getWorldObj(), door.coords);
		doorMap.put(door, pw);
		return pw;
	}

	public static WorldRenderer getWorldRenderer(PortalWorld pw, int x, int y, int z)
	{
		if(rend.containsKey(pw)) return rend.get(pw);
		WorldRenderer render = new WorldRenderer(pw,pw.getTEs(),x,y,z,1);
		rend.put(pw, render);
		return render;
	}

	public static WorldRenderer getWorldRenderer(MagicDoorTileEntity mdte)
	{
		SimpleCoordStore pos = mdte.coords;
		return getWorldRenderer(getWorld(mdte),pos.x, pos.y, pos.z);
	}
}
