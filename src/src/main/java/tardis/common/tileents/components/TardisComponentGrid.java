package tardis.common.tileents.components;

import java.util.ArrayList;
import java.util.EnumSet;
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
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import scala.actors.threadpool.Arrays;
import tardis.TardisMod;
import tardis.api.IWatching;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.store.SimpleCoordStore;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;

public class TardisComponentGrid extends TardisAbstractComponent implements IGridHost, IGridBlock, IWatching
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	private IGridNode node = null;
	
	private ArrayList<IGridConnection> connections = new ArrayList<IGridConnection>();
	
	private static EnumSet validDirs = EnumSet.copyOf(Arrays.asList(ForgeDirection.VALID_DIRECTIONS));
	
	protected TardisComponentGrid()	{	}
	public TardisComponentGrid(TardisComponentTileEntity parent)
	{
		//parentAdded(parent);
	}

	@Override
	public ITardisComponent create(TardisComponentTileEntity parent)
	{
		return new TardisComponentGrid(parent);
	}
	
	@Override
	public void updateTick()
	{
		if(!inited && parentObj != null)
		{
			inited = true;
			TardisCoreTileEntity core = Helper.getTardisCore(parentObj);
			if(core != null)
			{
				core.addGridLink(myCoords);
			}
		}
	}
	
	@Override
	public void parentAdded(TardisComponentTileEntity parent)
	{
		super.parentAdded(parent);
		myCoords = new SimpleCoordStore(parent);
	}
	
	@Override
	public void revive(TardisComponentTileEntity parent)
	{
		super.revive(parent);
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
		TardisOutput.print("TCG", "Attempted node get! " + node);
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
	
	private void createNode()
	{
		if(node == null && Helper.isServer())
		{
			node = TardisMod.aeAPI.createGridNode(this);
			node.updateState();
			connections.clear();
		}
		if(parentObj != null)
		{
			TardisCoreTileEntity core = parentObj.getCore();
			if(core != null)
			{
				addConnection(core.getNode());
			}
		}
	}
	
	private boolean doesConnectionExist(IGridNode aNode, IGridNode oNode)
	{
		if(aNode == null || oNode == null)
			return false;
		for(IGridConnection c : connections)
		{
			if((c.a().equals(oNode) && c.b().equals(aNode)) || (c.a().equals(aNode) && c.b().equals(oNode)))
				return true;
		}
		if(oNode.getConnections().contains(aNode) || aNode.getConnections().contains(oNode))
			return true;
		return false;
	}
	
	private boolean addConnection(IGridNode otherNode)
	{
		if(!Helper.isServer())
			return true;
		try
		{
			if(otherNode != null)
			{
				if(!doesConnectionExist(node, otherNode))
				{
					IGridConnection con = TardisMod.aeAPI.createGridConnection(node, otherNode);
					otherNode.updateState();
					connections.add(con);
					return true;
				}
			}
		}
		catch(Exception c)
		{
			
		}
		return false;
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
	public void neighbourUpdated(Block neighbourBlockID)
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
	
	@Override
	public boolean isWorldAccessable()
	{
		return true;
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
		TardisOutput.print("TCG", "Grid note");
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
			if(w.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ) instanceof TardisComponentTileEntity)
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
		TardisOutput.print("TCG", "Grid changed!");
	}
	
	@Override
	public ItemStack getMachineRepresentation()
	{
		return new ItemStack(TardisMod.componentBlock);
	}

}
