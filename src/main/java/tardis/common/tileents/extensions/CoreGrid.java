package tardis.common.tileents.extensions;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.common.core.helpers.Helper;
import tardis.common.tileents.CoreTileEntity;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;

public class CoreGrid implements IGridBlock
{
	private DimensionalCoord coreCoords = null;

	public CoreGrid(CoreTileEntity core)
	{
		coreCoords = new DimensionalCoord(core);
	}

	@Override
	public double getIdlePowerUsage()
	{
		return 0;
	}

	@Override
	public EnumSet<GridFlags> getFlags()
	{
		return EnumSet.of(GridFlags.DENSE_CAPACITY);
	}

	public boolean isWorldAccessable()
	{
		return false;
	}

	@Override
	public boolean isWorldAccessible()
	{
		return isWorldAccessable();
	}

	@Override
	public DimensionalCoord getLocation()
	{
		return coreCoords;
	}

	@Override
	public AEColor getGridColor()
	{
		return AEColor.Transparent;
	}

	@Override
	public void onGridNotification(GridNotification notification)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkStatus(IGrid grid, int channelsInUse)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides()
	{
		return EnumSet.noneOf(ForgeDirection.class);
	}

	@Override
	public IGridHost getMachine()
	{
		return Helper.getTardisCore(coreCoords.getWorld());
	}

	@Override
	public void gridChanged()
	{
	}

	@Override
	public ItemStack getMachineRepresentation()
	{
		return null;
	}

}
