package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.api.ScrewdriverMode;
import tardis.api.TardisPermission;
import tardis.common.blocks.SchemaComponentBlock;
import tardis.common.core.TardisOutput;
import tardis.common.core.helpers.Helper;
import tardis.common.core.schema.CoordStore;
import tardis.common.core.schema.PartBlueprint;
import tardis.common.dimension.TardisDataStore;

public class SchemaCoreTileEntity extends AbstractTileEntity implements IScrewable, IActivatable
{
	private String name = null;
	private int[] bounds;
	private int facing;
	private ArrayList<DoorDS> doors = new ArrayList<DoorDS>();
	private PartBlueprint pb = null;

	private boolean addedToCore = false;
	private int lastScrewTT = -1;

	private int traversed = 0;
	public SchemaCoreTileEntity()
	{
	}

	public void setData(String passedName, int[] moddedBounds, int passedFacing)
	{
		doors.clear();
		name = passedName;
		bounds = moddedBounds;
		facing = passedFacing;
		setDoorArray();
	}

	public String getName()
	{
		return name;
	}

	private AxisAlignedBB getBoundingBox()
	{
		try
		{
			AxisAlignedBB retVal = AxisAlignedBB.getBoundingBox(xCoord-bounds[0],yCoord,zCoord-bounds[1],xCoord+1+bounds[2],yCoord+1+bounds[4],zCoord+1+bounds[3]);
			return retVal;
		}
		catch(Exception e)
		{
			TardisOutput.print("TSCTE","Exception " + e.getMessage(),TardisOutput.Priority.ERROR);
			e.printStackTrace();
		}
		return null;
	}

	private List<Entity> entitiesWithinRoom()
	{
		ArrayList<Entity> returnList = new ArrayList<Entity>();
		AxisAlignedBB boundBox = getBoundingBox();
		if(boundBox != null)
		{
			List tempList = worldObj.getEntitiesWithinAABBExcludingEntity(null, boundBox);
			if(tempList != null)
			{
				for(Object o: tempList)
				{
					if(o instanceof Entity)
						returnList.add((Entity) o);
				}
			}
		}
		return returnList;
	}

	@Override
	public void init()
	{
		super.init();
		CoreTileEntity core = Helper.getTardisCore(worldObj);
		if((core != null) && (name!=null) && !name.startsWith("tardis"))
			core.addRoom(this);
		addedToCore = true;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(ServerHelper.isServer() && !addedToCore)
		{
			CoreTileEntity core = Helper.getTardisCore(worldObj);
			if((core != null) && (name!=null) && !name.startsWith("tardis"))
				core.addRoom(this);
			addedToCore = true;
		}
		if(ServerHelper.isServer())
		{
			traversed = 0;
			if((lastScrewTT + TardisMod.shiftPressTime) < tt)
				lastScrewTT = -1;
		}
	}

	public void remove(boolean deleteUnconnected)
	{
		if(name != null)
		{
			List<Entity> ents = entitiesWithinRoom();
			for(Entity ent: ents)
				Helper.teleportEntityToSafety(ent);
			TardisOutput.print("TSCTE","Removing:" + (xCoord - bounds[0]) + "to" + (xCoord + bounds[2])+ ","+(zCoord-bounds[1])+"to"+(zCoord+bounds[3]));
			for(int x = (xCoord-bounds[0]);x<=(xCoord+bounds[2]);x++)
				for(int y = yCoord;y<=(yCoord+bounds[4]);y++)
					for(int z = (zCoord-bounds[1]);z<=(zCoord+bounds[3]);z++)
					{
						if(((x != xCoord) || (y != yCoord) || (z != zCoord)) && !worldObj.isAirBlock(x, y, z) && Helper.isBlockRemovable(worldObj.getBlock(x, y, z)))
							worldObj.setBlockToAir(x, y, z);
					}
			if(deleteUnconnected)
			{
				Set<SimpleCoordStore> dependantCores = getDependentCores();
				for(SimpleCoordStore scs : dependantCores)
				{
					TileEntity te = scs.getTileEntity();
					if(te instanceof SchemaCoreTileEntity)
						((SchemaCoreTileEntity)te).remove(false);
				}
			}
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
		else
		{
			TardisOutput.print("TSCTE", "No name, can't remove?");
		}
	}

	/**
	 * @return A set of schema cores which are dependent on this core for a connection to the console room
	 */
	public Set<SimpleCoordStore> getDependentCores()
	{
		traversed = rand.nextInt(1000)+1;
		if(doors.isEmpty()) setDoorArray();
		HashSet<SimpleCoordStore> toReturn = new HashSet<SimpleCoordStore>();
		SimpleCoordStore me = coords;
		mainLoop:
		for(DoorDS myDDS : doors)
		{
			HashSet<SimpleCoordStore> current = new HashSet<SimpleCoordStore>();
			LinkedBlockingQueue<DoorDS> queue = new LinkedBlockingQueue<DoorDS>();
			queue.add(myDDS);
			DoorDS currentDoorDS = null;
			while((currentDoorDS = queue.poll()) != null)
			{
				if(currentDoorDS.other == null) continue;
				SimpleCoordStore other = currentDoorDS.other;
				if(other.equals(me)) continue;
				TileEntity tempTE = other.getTileEntity();
				if(!(tempTE instanceof SchemaCoreTileEntity)) continue;
				SchemaCoreTileEntity te = (SchemaCoreTileEntity)tempTE;
				if(te.traversed == traversed)
				{
					if(!current.contains(other) && !toReturn.contains(other))
						continue mainLoop;
					continue;
				}
				te.traversed = traversed;
				if(te.isCore())
				{
					continue mainLoop;
				}
				current.add(other);
				te.setDoorArray();
				for(DoorDS dds : te.doors)
					queue.add(dds);
			}
			toReturn.addAll(current);
		}
		return toReturn;
	}

	@Override
	public boolean screw(ScrewdriverMode mode, EntityPlayer player)
	{
		if(ServerHelper.isClient())
			return true;
		if(mode == ScrewdriverMode.Dismantle)
		{
			TardisDataStore ds = Helper.getDataStore(worldObj);
			if((ds == null) || ds.hasPermission(player, TardisPermission.ROOMS))
			{
				if(lastScrewTT == -1)
				{
					lastScrewTT = tt;
					Set<SimpleCoordStore> others = getDependentCores();
					ServerHelper.sendString(player, "Right click then sneak right click with a dismantle screwdriver to remove this room");
					if(TardisMod.deleteDisconnected)
						if(others.size() > 0)
							ServerHelper.sendString(player, others.size() + " rooms will be removed");
					return true;
				}
				else if(player.isSneaking())
				{
					CoreTileEntity core = Helper.getTardisCore(worldObj);
					if((core== null) || core.addRoom(true,this))
					{
						remove(TardisMod.deleteDisconnected);
						return true;
					}
				}
			}
			else
			{
				player.addChatMessage(CoreTileEntity.cannotModifyMessage);
			}
		}
		return false;
	}

	private void setDoorArray()
	{
		if(name == null)
			return;
		if(doors.size() == 0)
		{
			PartBlueprint pb = Helper.loadSchema(name);
			ArrayList<CoordStore> intDoors = pb.getDoors();
			for(CoordStore d : intDoors)
			{
				CoordStore rot = PartBlueprint.rotate(d, pb.getFacing(), facing);
				SimpleCoordStore scs = new SimpleCoordStore(worldObj,xCoord+rot.x,yCoord+rot.y,zCoord+rot.z);
				int facing = 0;
				if(rot.x == -bounds[0])
					facing = 0;
				if(rot.x == bounds[2])
					facing = 2;
				if(rot.z == -bounds[1])
					facing = 1;
				if(rot.z == bounds[3])
					facing = 3;
				doors.add(new DoorDS(facing,scs,(scs.getMetadata() % 8) >=4));
			}
		}
	}

	public void recheckDoors(boolean isRoomBeingRemoved)
	{
		if(ServerHelper.isClient())
			return;
		if(doors.size() == 0)
			setDoorArray();
		for(DoorDS dds : doors)
		{
			SimpleCoordStore scs = dds.scs;
			int facing = dds.facing;
			boolean foundPair = false;
			SimpleCoordStore other = null;
			switch(facing)
			{
				case 0: other = Helper.getExistingDoor(scs.getWorldObj(), scs.x-1, scs.y, scs.z); break;
				case 1: other = Helper.getExistingDoor(scs.getWorldObj(), scs.x, scs.y, scs.z-1); break;
				case 2: other = Helper.getExistingDoor(scs.getWorldObj(), scs.x+1, scs.y, scs.z); break;
				case 3: other = Helper.getExistingDoor(scs.getWorldObj(), scs.x, scs.y, scs.z+1); break;
			}

			dds.other = other;
			foundPair = other != null;
			if(!foundPair && isRoomBeingRemoved)
			{
				if(scs.getBlock() != TardisMod.internalDoorBlock)
				{
					if(pb == null)
						pb = Helper.loadSchema(name);
					repairDoor(dds,true);
				}
			}
			else if(foundPair)
			{
				if(scs.getBlock() != null)
					repairDoor(dds,false);
			}
		}
	}

	private boolean repairRow(int y,int stable, DoorDS door, boolean replace)
	{
		boolean repRow = false;
		boolean repCol = true;
		for(int o=((door.facing%2)==0)?door.scs.z:door.scs.x;repCol;o--)
		{
			if(replace)
				repCol = pb.repairBlock(worldObj, xCoord, yCoord, zCoord, (door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable, facing);
			else
			{
				repCol = (worldObj.getBlock((door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable) == TardisMod.internalDoorBlock);
				repCol = repCol || SchemaComponentBlock.isDoorConnector(worldObj, (door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable);
				if(repCol)
					worldObj.setBlockToAir((door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable);
			}
			repRow = repRow || repCol;
		}
		repCol = true;
		for(int o=1+(((door.facing%2)==0)?door.scs.z:door.scs.x);repCol;o++)
		{
			if(replace)
				repCol = pb.repairBlock(worldObj, xCoord, yCoord, zCoord, (door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable, facing);
			else
			{
				repCol = (worldObj.getBlock((door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable) == TardisMod.internalDoorBlock);
				repCol = repCol || SchemaComponentBlock.isDoorConnector(worldObj, (door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable);
				if(repCol)
					worldObj.setBlockToAir((door.facing%2)==0?stable:o, y, (door.facing%2)==0?o:stable);
			}
		}
		return repRow;
	}

	private void repairDoor(DoorDS door, boolean replace)
	{
		boolean repRow = true;
		//If facing is a multiple of 2, x is stable otherwise z is stable
		int stable = ((door.facing%2)==0)?door.scs.x:door.scs.z;
		for(int y = door.scs.y; repRow; y--)
			repRow = repairRow(y,stable,door,replace);
		repRow = true;
		for(int y = door.scs.y+1; repRow; y++)
			repRow = repairRow(y,stable,door,replace);
		//InternalDoorBlock.manageConnected(door.scs.getWorldObj(), door.scs.x, door.scs.y, door.scs.y, door.facing);
		if(replace)
		{
			boolean primary = worldObj.getBlockMetadata(door.scs.x, door.scs.y, door.scs.z) >= 4;
			worldObj.setBlockMetadataWithNotify(door.scs.x, door.scs.y, door.scs.z, (door.facing ) + (primary?4:0) , 3);
		}
	}

	public boolean isDoor(SimpleCoordStore pos)
	{
		if(doors.size() == 0)
			setDoorArray();
		for(DoorDS dds : doors)
		{
			if(dds.scs.equals(pos))
				return true;
		}
		return false;
	}

	public boolean isCore()
	{
		if(!Helper.isTardisWorld(getWorldObj())) return true;
		CoreTileEntity core = Helper.getTardisCore(this);
		if(core.getSchemaCore() == this)
			return true;
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if(name != null)
		{
			nbt.setString("name",name);
			nbt.setIntArray("bounds", bounds);
			nbt.setInteger("facing", facing);
			int i = 0;
			for(DoorDS ds : doors)
				ds.writeToNBT(nbt, "door"+(i++));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if(nbt.hasKey("name") && (name == null))
		{
			doors.clear();
			name = nbt.getString("name");
			bounds = nbt.getIntArray("bounds");
			facing = nbt.getInteger("facing");
			int i = 0;
			while(nbt.hasKey("door"+i))
				doors.add(DoorDS.readFromNBT(nbt, "door"+(i++)));
		}
	}

	@Override
	public void writeTransmittable(NBTTagCompound nbt)
	{
	}

	@Override
	public void readTransmittable(NBTTagCompound nbt)
	{
	}

	private static class DoorDS
	{
		private final int facing;
		private final SimpleCoordStore scs;
		public SimpleCoordStore other = null;
		private final boolean primary;
		public DoorDS(int f, SimpleCoordStore s, boolean p)
		{
			facing = f;
			scs = s;
			primary = p;
		}

		public void writeToNBT(NBTTagCompound nbt, String name)
		{
			NBTTagCompound tempNBT = scs.writeToNBT();
			tempNBT.setInteger("f", facing);
			tempNBT.setBoolean("p", primary);
			nbt.setTag(name, tempNBT);
		}

		public static DoorDS readFromNBT(NBTTagCompound nbt, String name)
		{
			if(!nbt.hasKey(name)) return null;
			NBTTagCompound temp = nbt.getCompoundTag(name);
			int f = temp.getInteger("f");
			boolean p = temp.getBoolean("p");
			SimpleCoordStore scs = SimpleCoordStore.readFromNBT(temp);
			return new DoorDS(f,scs,p);
		}
	}

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		recheckDoors(true);
		return false;
	}

}
