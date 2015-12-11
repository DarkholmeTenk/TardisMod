package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IBlockUpdateDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.TardisMod;
import tardis.common.integration.ae.AEHelper;
import tardis.common.integration.ae.ITMGrid;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value={
@Optional.Interface(iface="tardis.common.integration.ae.ITMGrid",modid="appliedenergistics2"),
@Optional.Interface(iface="appeng.api.networking.IGridBlock",modid="appliedenergistics2")
})
public class ComponentGrid extends AbstractComponent implements ITMGrid, IGridBlock, IBlockUpdateDetector
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	private IGridNode node = null;
	private boolean linkedToCore = false;

	private ArrayList<IGridConnection> connections = new ArrayList<IGridConnection>();

	private static EnumSet validDirs = EnumSet.copyOf(Arrays.asList(ForgeDirection.VALID_DIRECTIONS));

	protected ComponentGrid()	{	}
	public ComponentGrid(ComponentTileEntity parent)
	{
		//parentAdded(parent);
	}

	@Override
	public ITardisComponent create(ComponentTileEntity parent)
	{
		return new ComponentGrid(parent);
	}

	@Override
	public void updateTick()
	{
		if(!Loader.isModLoaded("appliedenergistics2")) return;
		if(!inited && (parentObj != null))
		{
			inited = true;
			CoreTileEntity core = getCore();
			if(core != null)
			{
				core.addGridLink(myCoords);
			}
		}
		if(!linkedToCore)
			linkToCore();
	}

	@Override
	public void parentAdded(ComponentTileEntity parent)
	{
		super.parentAdded(parent);
		myCoords = new SimpleCoordStore(parent);
	}

	@Override
	public void revive(ComponentTileEntity parent)
	{
		super.revive(parent);
		if(!Loader.isModLoaded("appliedenergistics2")) return;
		createNode();
	}

	@Override
	public void die()
	{
		if(node != null)
		{
			node.destroy();
			node = null;
		}
		super.die();
	}

	@Override
	public IGridNode getGridNode(ForgeDirection dir)
	{
		createNode();
		return node;
	}

	@Override
	public AECableType getCableConnectionType(ForgeDirection dir)
	{
		return AECableType.SMART;
	}

	@Override
	public void securityBreak()
	{

	}

	private void linkToCore()
	{
		if(ServerHelper.isClient())
			return;
		if(!Loader.isModLoaded("appliedenergistics2")) return;
		CoreTileEntity core = getCore();
		if(core != null)
		{
			linkedToCore = true;
			addConnection(core, ForgeDirection.UNKNOWN);
		}
	}

	private void createNode()
	{
		if(AEHelper.aeAPI == null)
			return;
		if((node == null) && ServerHelper.isServer())
		{
			node = AEHelper.aeAPI.createGridNode(this);
			node.updateState();
			linkedToCore = false;
			connections.clear();
		}
		linkToCore();
	}

	private static boolean doesConnectionExist(IGridNode aNode, IGridNode oNode, Iterable<IGridConnection> connections)
	{
		for(IGridConnection c : connections)
			if((c.a().equals(oNode) && c.b().equals(aNode)) || (c.a().equals(aNode) && c.b().equals(oNode)))
				return true;
		return false;
	}

	private boolean doesConnectionExist(IGridNode aNode, IGridNode oNode)
	{
		if((aNode == null) || (oNode == null))
			return false;
		if(doesConnectionExist(aNode, oNode, connections)) return true;
		if(doesConnectionExist(aNode, oNode, oNode.getConnections())) return true;
		if(doesConnectionExist(aNode, oNode, aNode.getConnections())) return true;
		return false;
	}

	private boolean addConnection(IGridHost otherHost, ForgeDirection dir)
	{
		if(ServerHelper.isClient())
			return true;
		if(otherHost == null)
			return false;
		IGridNode otherNode = otherHost.getGridNode(dir);
		try
		{
			if(otherNode != null)
			{
				if(!doesConnectionExist(node, otherNode))
				{
					IGridConnection con = AEHelper.aeAPI.createGridConnection(node, otherNode);
					addConnection(con);
					if(otherHost instanceof ITMGrid)
						((ITMGrid)otherHost).addConnection(con);
					otherNode.updateState();
					return true;
				}
			}
		}
		catch(Exception c)
		{
			c.printStackTrace();
		}
		return false;
	}

	@Override
	public void addConnection(IGridConnection con)
	{
		if((con.a() == node) || (con.b() == node))
			connections.add(con);
	}

	private void scan()
	{
		if(parentObj == null)
			return;
		World w = parentObj.getWorldObj();
		if(w == null)
			return;
		boolean upd = false;
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity te = w.getTileEntity(xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ);
			if(te instanceof IGridHost)
			{
				IGridHost host = (IGridHost)te;
				IGridNode otherNode = host.getGridNode(dir.getOpposite());
				if(!doesConnectionExist(node,otherNode))
					upd = true;
			}
		}
		if(upd)
			parentObj.sendUpdate();
	}

	@Override
	public void blockUpdated(Block neighbourBlockID)
	{
		scan();
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
		return true;
	}

	@Override
	public boolean isWorldAccessible()
	{
		return isWorldAccessable();
	}

	@Override
	public DimensionalCoord getLocation()
	{
		return new DimensionalCoord(parentObj);
	}
	@Override
	public AEColor getGridColor()
	{
		// TODO Auto-generated method stub
		return AEColor.Transparent;
	}
	@Override
	public void onGridNotification(GridNotification notification)
	{
		//TardisOutput.print("TCG", "Grid note " + parentObj.getWorldObj().provider.dimensionId);
	}

	@Override
	public void setNetworkStatus(IGrid grid, int channelsInUse)
	{
	}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides()
	{
		if(parentObj == null)
			return validDirs;
		World w = parentObj.getWorldObj();
		if(w == null)
			return validDirs;
		ArrayList<ForgeDirection> dirs = new ArrayList<ForgeDirection>(6);
		dirs.addAll(validDirs);
		for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
		{
			if(w.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ) instanceof ComponentTileEntity)
				dirs.remove(d);
		}
		return EnumSet.copyOf(dirs);
	}

	@Override
	public IGridHost getMachine()
	{
		return parentObj;
	}

	@Override
	public void gridChanged()
	{
	}

	@Override
	public ItemStack getMachineRepresentation()
	{
		return new ItemStack(TardisMod.componentBlock);
	}

}
