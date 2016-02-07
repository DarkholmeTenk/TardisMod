package tardis.common.tileents.components;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.helpers.WorldHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;
import io.darkcraft.darkcore.mod.interfaces.IBlockUpdateDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.integration.ae.AEHelper;
import tardis.common.integration.ae.ITMGrid;
import tardis.common.items.SonicScrewdriverItem;
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
public class ComponentGrid extends AbstractComponent implements ITMGrid, IGridBlock, IBlockUpdateDetector, IScrewable, IActivatable
{
	private boolean inited = false;
	private SimpleCoordStore myCoords = null;
	private IGridNode node = null;
	private boolean linkedToCore = false;
	private boolean linkedToOther = false;
	private SimpleCoordStore otherGrid;

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
		super.updateTick();
		if(!Loader.isModLoaded("appliedenergistics2")) return;
		if(!inited && (parentObj != null))
		{
			inited = true;
			CoreTileEntity core = getCore();
			if(core != null)
				core.addGridLink(myCoords);
		}
		if((!linkedToOther) && (otherGrid != null) && parentObj.isValid())
			linkToSCS(otherGrid);
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
		if((parentObj == null) || !parentObj.isValid())
			return null;
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

	private void breakOGConnection()
	{
		for(IGridConnection n : connections)
			n.destroy();
		connections.clear();
		linkToCore();
	}

	private boolean linkToSCS(SimpleCoordStore scs)
	{
		if((scs == null) || scs.equals(myCoords)) return false;
		TileEntity te = scs.getTileEntity();
		if(!(te instanceof ComponentTileEntity) || (scs.world != WorldHelper.getWorldID(parentObj)))
		{
			if(otherGrid == scs)
				otherGrid = null;
			return false;
		}
		ITardisComponent comp = ((ComponentTileEntity)te).getComponent(TardisTEComponent.GRID);
		if(comp == null)
		{
			if(otherGrid == scs)
				otherGrid = null;
			return false;
		}
		linkedToOther = false;
		ComponentGrid cg = (ComponentGrid) comp;
		if((otherGrid != null) && !otherGrid.equals(scs))
			breakOGConnection();
		otherGrid = scs;
		if(addConnection(cg,null))
			linkedToOther = true;
		return linkedToOther;
	}

	@Override
	public boolean screw(ScrewdriverHelper helper, ScrewdriverMode mode, EntityPlayer player)
	{
		if(mode != ScrewdriverMode.Link) return false;
		if(helper.getLinkSCS() == null)
		{
			helper.setLinkSCS(myCoords);
			if(ServerHelper.isServer())
				ServerHelper.sendString(player, SonicScrewdriverItem.screwName, "Link target set to " + myCoords);
			return true;
		}
		else
		{
			SimpleCoordStore scs = helper.getLinkSCS();
			if(scs.equals(myCoords) && (otherGrid != null))
			{
				breakOGConnection();
				if(ServerHelper.isServer())
					ServerHelper.sendString(player, SonicScrewdriverItem.screwName, "Removed link");
			}
			else if(linkToSCS(scs))
			{
				if(ServerHelper.isServer())
					ServerHelper.sendString(player, SonicScrewdriverItem.screwName, "Linked " + myCoords + " to " + scs);
			}
			else if(ServerHelper.isServer())
				ServerHelper.sendString(player, SonicScrewdriverItem.screwName, "Link target has been cleared");
			helper.setLinkSCS(null);
			return true;
		}
	}
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if(otherGrid != null)
			otherGrid.writeToNBT(nbt, "og");
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("og"))
		{
			SimpleCoordStore scs = SimpleCoordStore.readFromNBT(nbt, "og");
			if(!scs.equals(otherGrid))
			{
				if(inited)
					linkToSCS(scs);
				else
					otherGrid = scs;
			}
		}
		else if(otherGrid != null)
		{
			breakOGConnection();
			otherGrid = null;
		}
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		if(ServerHelper.isServer())
		{
			if(otherGrid == null)
				ServerHelper.sendString(ent, "Direct connection: None");
			else
				ServerHelper.sendString(ent, "Direct connection: " + otherGrid);
		}
		return true;
	}

}
