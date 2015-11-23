package tardis.common.core.flight;

import io.darkcraft.darkcore.mod.DarkcoreMod;
import io.darkcraft.darkcore.mod.datastore.HashMapSet;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.ShieldTileEntity;

public class ShieldModifier implements IFlightModifier
{
	private static HashMapSet<Integer,SimpleCoordStore> shieldPositions = new HashMapSet();

	public static void clear()
	{
		shieldPositions.clear();
	}

	public static void registerShieldTileEntity(ShieldTileEntity ste)
	{
		if(ste == null) return;
		SimpleCoordStore scs = ste.coords();
		int w = scs.world;
		shieldPositions.add(w, scs);
	}

	private Set<ShieldTileEntity> getValidActiveShields(int wid)
	{
		Set<ShieldTileEntity> stes = new HashSet();
		Iterator<SimpleCoordStore> iter = shieldPositions.iterator(wid);
		while(iter.hasNext())
		{
			SimpleCoordStore shieldPos = iter.next();
			TileEntity te = shieldPos.getTileEntity();
			if(te instanceof ShieldTileEntity)
			{
				ShieldTileEntity ste = (ShieldTileEntity) te;
				if(ste.isActive())
					stes.add(ste);
			}
			else
			{
				iter.remove();
				continue;
			}
		}
		return stes;
	}

	@Override
	public int[] getModifiedControls(CoreTileEntity core, ConsoleTileEntity console, World w, int[] pos)
	{
		boolean changed = false;
		boolean changedThisTurn;
		int wid = WorldHelper.getWorldID(w);
		Set<ShieldTileEntity> stes = getValidActiveShields(wid);
		if(stes.size() == 0) return pos;
		int[] newPos = pos.clone();
		int change = DarkcoreMod.r.nextInt(4);
		mainLoop:
		do
		{
			ChunkCoordIntPair ccip = new ChunkCoordIntPair(newPos[0] >> 4, newPos[2] >> 4);
			changedThisTurn = false;
			for(ShieldTileEntity ste : stes)
			{
				ChunkCoordIntPair sccip = ste.coords().toChunkCoords();
				if(sccip.equals(ccip))
				{
					ste.blockAttempt(core);
					changed = changedThisTurn = true;
					switch(change)
					{
						case 0: newPos[0] += 16; break;
						case 1: newPos[0] -= 16; break;
						case 2: newPos[2] += 16; break;
						case 3: newPos[2] -= 16; break;
					}
					continue mainLoop;
				}
			}
		}
		while(changedThisTurn);
		if(changed)
			return newPos;
		return pos;
	}

	@Override
	public String getID()
	{
		return "sm";
	}

}
