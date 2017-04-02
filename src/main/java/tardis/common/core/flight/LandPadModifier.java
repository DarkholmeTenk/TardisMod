package tardis.common.core.flight;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.LandingPadTileEntity;

public class LandPadModifier implements IFlightModifier
{

	@Override
	public int[] getModifiedControls(CoreTileEntity core, ConsoleTileEntity console, World w, int[] current)
	{
		if(!console.getLandOnPadFromControls()) return current;
		int[] check = { 0, 1, -1, 2, -2, 3, -3, 4, -4, 5, -5, -6, 6, -7, 7, -8, 8 };
		for (int i = 0; i < check.length; i++)
		{
			int xO = check[i];
			for (int j = 0; j < check.length; j++)
			{
				int zO = check[j];
				for (int k = 0; k < check.length; k++)
				{
					int yO = check[k];
					TileEntity te = w.getTileEntity(current[0] + xO, current[1] + yO, current[2] + zO);
					if (te instanceof LandingPadTileEntity)
					{
						if (((LandingPadTileEntity) te).isClearBottom())
						{
							return new int[] { current[0] + xO, current[1] + yO + 1, current[2] + zO };
						}
						else if (((LandingPadTileEntity) te).isClearTop()) { return new int[] { current[0] + xO, current[1] + yO + 2, current[2] + zO }; }
					}
				}
			}
		}
		return current;
	}

	@Override
	public String getID()
	{
		return "lpm";
	}

}
