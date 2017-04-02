package tardis.common.core.flight;

import net.minecraft.world.World;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;

public interface IFlightModifier
{
	public int[] getModifiedControls(CoreTileEntity core, ConsoleTileEntity console, World w, int[] pos);

	public String getID();
}
