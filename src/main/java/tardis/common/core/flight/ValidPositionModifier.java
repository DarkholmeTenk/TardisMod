package tardis.common.core.flight;

import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.dimension.TardisDimensionHandler;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class ValidPositionModifier implements IFlightModifier
{

	private boolean isValidPos(World w, int x, int y, int z, int mh)
	{
		return (y > 0) && (y < (mh - 1)) && WorldHelper.softBlock(w, x, y, z) && WorldHelper.softBlock(w, x, y + 1, z);
	}

	private static final int[] check = { 0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, 6, -6, 7, -7, 8, -8, 9, -9 };
	@Override
	public int[] getModifiedControls(CoreTileEntity core, ConsoleTileEntity console, World w, int[] pos)
	{
		TardisDataStore ds = Helper.getDataStore(core);
		if ((ds != null) && (pos[0] == ds.exteriorX) && (pos[1] == ds.exteriorY) && (pos[2] == ds.exteriorZ)) return pos;
		int mh = TardisDimensionHandler.getMaxHeight(w);
		for (int yO : check)
			for(int xO : check)
				for(int zO : check)
					if (isValidPos(w, pos[0] + xO, pos[1] + yO, pos[2] + zO, mh))
						return new int[] { pos[0] + xO, pos[1] + yO, pos[2] + zO };
		/*for (int i = 0; i < check.length; i++)
		{
			int yO = check[i];
			for (int j = 0; j < check.length; j++)
			{
				int xO = check[j];
				for (int k = 0; k < check.length; k++)
				{
					int zO = check[k];
					if (isValidPos(w, pos[0] + xO, pos[1] + yO, pos[2] + zO, mh))
						return new int[] { pos[0] + xO, pos[1] + yO, pos[2] + zO };
				}
			}
		}*/
		return pos;
	}

	@Override
	public String getID()
	{
		return "vpm";
	}

}
