package tardis.common.core.flight;

import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.helpers.WorldHelper;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class LandGroundModifier implements IFlightModifier
{

	@Override
	public int[] getModifiedControls(CoreTileEntity core, ConsoleTileEntity console, World w, int[] pos)
	{
		if(!console.getLandOnGroundFromControls()) return pos;
		if (WorldHelper.softBlock(w, pos[0], pos[1] - 1, pos[2]))
		{
			int newY = pos[1] - 2;
			while ((newY > 0) && WorldHelper.softBlock(w, pos[0], newY, pos[2]))
				newY--;
			return new int[] { pos[0], newY + 1, pos[2] };
		}
		return pos;
	}

	@Override
	public String getID()
	{
		return "lgm";
	}

}
