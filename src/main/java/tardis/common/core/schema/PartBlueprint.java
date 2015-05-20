package tardis.common.core.schema;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.blocks.InternalDoorBlock;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.exception.schema.SchemaCoreNotFoundException;
import tardis.common.core.exception.schema.SchemaDoorNotFoundException;
import tardis.common.core.exception.schema.UnmatchingSchemaException;
import tardis.common.tileents.SchemaCoreTileEntity;

public class PartBlueprint
{
	private int[]							bounds			= new int[5];								// -X, -Z, X, Z, Y

	private static boolean					unsafeDoors		= true;
	private ArrayList<CoordStore>			doors			= new ArrayList();
	private CoordStore						primaryDoor		= null;
	private int								primaryDoorFace	= -1;
	private boolean							initialized		= false;

	public HashMap<CoordStore, SchemaStore>	storage			= new HashMap<CoordStore, SchemaStore>();

	public String							myName;

	public PartBlueprint(World world, String name, int x, int y, int z) throws SchemaDoorNotFoundException,
			SchemaCoreNotFoundException
	{
		myName = name;
		if (world.getBlock(x, y, z) == TardisMod.schemaCoreBlock)
		{
			getBounds(world, x, y, z);
			getDoor(world, x, y, z);
			if (primaryDoor == null)
				throw new SchemaDoorNotFoundException(bounds, x, y, z);
			scanStuff(world, x, y, z);
		}
		else
		{
			throw new SchemaCoreNotFoundException(x, y, z);
		}
	}

	private void getBounds(World world, int x, int y, int z)
	{
		Block schemaBlock = TardisMod.schemaBlock;
		int tmp = 1;
		boolean cont = true;
		boolean[] scanning = new boolean[] { true, true, true, true };
		while (cont)
		{
			cont = false;
			if ((world.getBlock(x - tmp, y, z) == schemaBlock) && scanning[0])
			{
				cont = true;
				bounds[0] = tmp;
			}
			else
				scanning[0] = false;

			if ((world.getBlock(x, y, z - tmp) == schemaBlock) && scanning[1])
			{
				cont = true;
				bounds[1] = tmp;
			}
			else
				scanning[1] = false;

			if ((world.getBlock(x + tmp, y, z) == schemaBlock) && scanning[2])
			{
				cont = true;
				bounds[2] = tmp;
			}
			else
				scanning[2] = false;

			if ((world.getBlock(x, y, z + tmp) == schemaBlock) && scanning[3])
			{
				cont = true;
				bounds[3] = tmp;
			}
			else
				scanning[3] = false;
			tmp++;
		}

		tmp = 0;
		cont = true;
		while (cont)
		{
			cont = false;
			if (world.getBlock(x - bounds[0], y + tmp, z - bounds[1]) == schemaBlock)
			{
				bounds[4] = tmp;
				cont = true;
			}
			tmp++;
		}
	}

	public ArrayList<CoordStore> getDoors()
	{
		return doors;
	}

	private void handleDoor(World w, int x, int y, int z, int xL, int yL, int zL, int face)
	{
		boolean primary = (w.getBlockMetadata(x, y, z) % 8) >= 4;
		if(!initialized)
		{
			doors.add(new CoordStore(xL,yL,zL));
			if (primary)
			{
				primaryDoor = new CoordStore(xL, yL, zL);
				primaryDoorFace = face;
			}
		}
		w.setBlockMetadataWithNotify(x, y, z, face + (primary ? 4 : 0), 3);
		InternalDoorBlock.manageConnected(w, x, y, z, face);
		InternalDoorBlock.manageConnected(w, x + InternalDoorBlock.dx(face), y, z + InternalDoorBlock.dz(face), face);
	}

	private void getDoor(World world, int x, int y, int z, int facing)
	{
		int[] bounds = moddedBounds(facing);
		if(!initialized)
		{
			for (int zL = -bounds[1]; zL <= bounds[3]; zL++)
			{
				int xL = -bounds[0];

				for (int yL = 0; yL <= bounds[4]; yL++)
					if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
						handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 0);

				xL = bounds[2];
				for (int yL = 0; yL <= bounds[4]; yL++)
					if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
						handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 2);
			}

			for (int xL = -bounds[0]; xL <= bounds[2]; xL++)
			{
				int zL = -bounds[1];
				for (int yL = 0; yL <= bounds[4]; yL++)
					if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
						handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 1);

				zL = bounds[3];
				for (int yL = 0; yL <= bounds[4]; yL++)
					if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
						handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 3);
			}
			initialized = true;
		}
		else
		{
			for(CoordStore door : doors)
			{
				CoordStore rotated = rotate(door,primaryDoorFace,facing);
				if(rotated.x == -bounds[0])
					handleDoor(world, x + rotated.x,y + rotated.y, z + rotated.z, rotated.x, rotated.y, rotated.z, 0);
				if(rotated.x == bounds[2])
					handleDoor(world, x + rotated.x,y + rotated.y, z + rotated.z, rotated.x, rotated.y, rotated.z, 2);
				if(rotated.z == -bounds[1])
					handleDoor(world, x + rotated.x,y + rotated.y, z + rotated.z, rotated.x, rotated.y, rotated.z, 1);
				if(rotated.z == bounds[3])
					handleDoor(world, x + rotated.x,y + rotated.y, z + rotated.z, rotated.x, rotated.y, rotated.z, 3);
			}
		}
	}

	private void getDoor(World world, int x, int y, int z)
	{
		for (int zL = -bounds[1]; zL <= bounds[3]; zL++)
		{
			int xL = -bounds[0];

			for (int yL = 0; yL <= bounds[4]; yL++)
				if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
					handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 0);

			xL = bounds[2];
			for (int yL = 0; yL <= bounds[4]; yL++)
				if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
					handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 2);
		}

		for (int xL = -bounds[0]; xL <= bounds[2]; xL++)
		{
			int zL = -bounds[1];
			for (int yL = 0; yL <= bounds[4]; yL++)
				if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
					handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 1);

			zL = bounds[3];
			for (int yL = 0; yL <= bounds[4]; yL++)
				if (world.getBlock(x + xL, y + yL, z + zL) == TardisMod.internalDoorBlock)
					handleDoor(world, x + xL, y + yL, z + zL, xL, yL, zL, 3);
		}
		initialized = true;
	}

	private void scanStuff(World w, int x, int y, int z)
	{
		for (int xL = -bounds[0]; xL <= bounds[2]; xL++)
		{
			for (int yL = 0; yL <= bounds[4]; yL++)
			{
				for (int zL = -bounds[1]; zL <= bounds[3]; zL++)
				{
					CoordStore coord = new CoordStore(xL, yL, zL);
					SchemaStore store = SchemaStore.storeWorldBlock(w, x + xL, y + yL, z + zL);
					if (store != null)
					{
						TardisOutput.print("TPB", "stuff found at " + coord.toString());
						storage.put(coord, store);
					}
				}
			}
		}
	}

	public void saveTo(File saveFile)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", myName);
		nbt.setIntArray("bounds", bounds);
		if (primaryDoor != null)
		{
			nbt.setString("primaryDoor", primaryDoor.toString());
			nbt.setInteger("primaryDoorFace", primaryDoorFace);
		}
		NBTTagCompound doorsNBT = new NBTTagCompound();
		for(int i = 0; i< doors.size(); i++)
			doorsNBT.setString(""+i, doors.get(i).toString());
		nbt.setTag("doors", doorsNBT);
		NBTTagCompound store = new NBTTagCompound();
		for (CoordStore st : storage.keySet())
		{
			// TardisOutput.print("TPB", "Adding " + st.toString() + " to store variable");
			store.setTag(st.toString(), storage.get(st).getTagCompound());
		}
		nbt.setTag("storage", store);
		try
		{
			DataOutputStream stream = new DataOutputStream(new FileOutputStream(saveFile));
			ServerHelper.writeNBT(nbt, stream);
			// NBTTagCompound.writeNamedTag(nbt, stream);
			stream.close();
			TardisMod.schemaHandler.refresh(myName,this);
		}
		catch (IOException e)
		{
			TardisOutput.print("TPB", "Error saving to " + saveFile + ":" + e.getMessage());
		}
	}

	public PartBlueprint(String name, File loadFile)
	{
		try
		{
			DataInputStream stream = new DataInputStream(new FileInputStream(loadFile));
			// NBTTagCompound nbt = (NBTTagCompound) NBTTagCompound.readNamedTag(stream);
			NBTTagCompound nbt = ServerHelper.readNBT(stream);
			myName = name;
			bounds = nbt.getIntArray("bounds");
			if (nbt.hasKey("primaryDoor"))
			{
				primaryDoor = CoordStore.fromString(nbt.getString("primaryDoor"));
				primaryDoorFace = nbt.getInteger("primaryDoorFace");
			}
			if (nbt.hasKey("storage"))
			{
				NBTTagCompound store = nbt.getCompoundTag("storage");
				for (int xL = -bounds[0]; xL <= bounds[2]; xL++)
				{
					for (int yL = 0; yL <= bounds[4]; yL++)
					{
						for (int zL = -bounds[1]; zL <= bounds[3]; zL++)
						{
							CoordStore coord = new CoordStore(xL, yL, zL);
							if (store.hasKey(coord.toString()))
							{
								SchemaStore tempStore = SchemaStore.loadFromNBT(store.getCompoundTag(coord.toString()));
								storage.put(coord, tempStore);
							}
						}
					}
				}
			}
			if (nbt.hasKey("doors"))
			{
				doors.clear();
				NBTTagCompound doorsNBT = nbt.getCompoundTag("doors");
				int i = 0;
				while(doorsNBT.hasKey(""+i))
				{
					CoordStore door = CoordStore.fromString(doorsNBT.getString(""+i));
					if(door != null)
						doors.add(door);
					i++;
				}
				initialized = true;
			}
			stream.close();
		}
		catch (IOException e)
		{
			TardisOutput.print("TPB", "IOException " + e.getMessage(), TardisOutput.Priority.ERROR);
			myName = null;
		}
	}

	private boolean matchingBounds(int[] a, int[] b)
	{
		if ((a == null) || (b == null))
			return false;
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	public PartBlueprint(PartBlueprint to, PartBlueprint from) throws UnmatchingSchemaException
	{
		if ((to.primaryDoorFace == to.primaryDoorFace) && matchingBounds(to.bounds, from.bounds)
				&& (to.primaryDoor.equals(from.primaryDoor) || unsafeDoors))
		{

			for (CoordStore coord : to.storage.keySet())
			{
				if (!from.storage.containsKey(coord))
				{
					TardisOutput.print("TPB", "In to but not from:" + coord.toString());
					storage.put(coord, to.storage.get(coord));
				}
				else
				{
					if (!from.storage.get(coord).equals(to.storage.get(coord)))
						storage.put(coord, to.storage.get(coord));
				}
			}

			for (CoordStore coord : from.storage.keySet())
			{
				if (!to.storage.containsKey(coord))
				{
					TardisOutput.print("TPB", "In from but not to:" + coord.toString());
					storage.put(coord, SchemaStore.airBlock);
				}
			}

			primaryDoor = to.primaryDoor;
			primaryDoorFace = to.primaryDoorFace;
			bounds = to.bounds;
			myName = to.myName + "." + from.myName + ".diff";
		}
		else
			throw new UnmatchingSchemaException(to, from, to.primaryDoorFace != to.primaryDoorFace,
				!matchingBounds(to.bounds,from.bounds),
				!to.primaryDoor.equals(from.primaryDoor));
	}

	public CoordStore getPrimaryDoorPos(int facing)
	{
		return rotate(primaryDoor, primaryDoorFace, facing);
	}

	public static CoordStore rotate(CoordStore key, int cFace, int dFace)
	{
		if (cFace == dFace)
			return key;
		if (((cFace == 1) && (dFace == 3)) || ((cFace == 3) && (dFace == 1)) || ((cFace == 0) && (dFace == 2)) || ((cFace == 2) && (dFace == 0)))
			return key.rotate();
		if (((cFace == 0) && (dFace == 3)) || ((dFace < cFace) && !((cFace == 3) && (dFace == 0))))
			return key.rotateRight();
		else
			return key.rotateLeft();
	}

	public void hollow(World w, int x, int y, int z, int facing)
	{
		for (CoordStore coord : storage.keySet())
		{
			CoordStore rotated = coord;
			if (primaryDoorFace != facing)
				rotated = rotate(coord, primaryDoorFace, facing);
			if (w.getBlock(x + rotated.x, y + rotated.y, z + rotated.z) != Blocks.air)
			{
				w.setBlock(x + rotated.x, y + rotated.y, z + rotated.z, Blocks.air, 0, 2);
			}
		}
	}

	public void clear(World w, int x, int y, int z, int facing)
	{
		CoordStore min = new CoordStore(-bounds[0], 0, -bounds[1]);
		CoordStore max = new CoordStore(bounds[2], bounds[4], bounds[3]);
		min = rotate(min, primaryDoorFace, facing);
		max = rotate(max, primaryDoorFace, facing);
		int minX = x + Math.min(min.x, max.x);
		int maxX = x + Math.max(min.x, max.x);
		int minY = y + Math.min(min.y, max.y);
		int maxY = y + Math.max(min.y, max.y);
		int minZ = z + Math.min(min.z, max.z);
		int maxZ = z + Math.max(min.z, max.z);
		TardisOutput.print("TPB", "Clearing area" + minX + "," + maxX + "," + minY + "," + maxY + "," + minZ + "," + maxZ);
		for (int i = minX; i <= maxX; i++)
			for (int j = minY; j <= maxY; j++)
				for (int k = minZ; k <= maxZ; k++)
					if (!w.isAirBlock(i, j, k) && Helper.isBlockRemovable(w.getBlock(i, j, k)))
						w.setBlockToAir(i, j, k);
	}

	public int[] moddedBounds(int facing)
	{
		int[] newBounds = new int[5];
		CoordStore min = new CoordStore(-bounds[0], 0, -bounds[1]);
		CoordStore max = new CoordStore(bounds[2], bounds[4], bounds[3]);
		min = rotate(min, primaryDoorFace, facing);
		max = rotate(max, primaryDoorFace, facing);
		newBounds[0] = -Math.min(min.x, max.x);
		newBounds[1] = -Math.min(min.z, max.z);
		newBounds[2] = Math.max(min.x, max.x);
		newBounds[3] = Math.max(min.z, max.z);
		newBounds[4] = Math.max(min.y, max.y);
		return newBounds;
	}

	public boolean roomFor(World w, int x, int y, int z, int facing)
	{
		if ((y < 1) || ((y + bounds[4]) > 255))
			return false;
		for (CoordStore coord : storage.keySet())
		{
			CoordStore rotated = coord;
			if (primaryDoorFace != facing)
				rotated = rotate(coord, primaryDoorFace, facing);
			if (!w.isAirBlock(x + rotated.x, y + rotated.y, z + rotated.z))
				return false;
		}
		return true;
	}

	public void reconstitute(World w, int x, int y, int z, int facing)
	{
		hollow(w, x, y, z, facing);
		for (CoordStore key : storage.keySet())
		{
			SchemaStore st = storage.get(key);
			CoordStore modKey = key;
			int newMeta = SchemaRotationHandler.getNewMetadata(st.getBlock(), st.getBlockMeta(), primaryDoorFace, facing);
			if (facing != primaryDoorFace)
				modKey = rotate(key, primaryDoorFace, facing);
			st.loadToWorld(w, newMeta, x + modKey.x, y + modKey.y, z + modKey.z);
			if (st.getBlock() == Blocks.chest)
				w.setBlockMetadataWithNotify(x + modKey.x, y + modKey.y, z + modKey.z, newMeta, 3);
		}
		TileEntity te = w.getTileEntity(x, y, z);
		if (te instanceof SchemaCoreTileEntity)
		{
			String name = myName;
			if (name.endsWith(".diff"))
			{
				String[] bits = name.split("\\.");
				name = bits[0];
				TardisOutput.print("TPB", "Name is a diff, so setting to " + name);
			}
			((SchemaCoreTileEntity) te).setData(name, moddedBounds(facing), facing);
		}
		getDoor(w, x, y, z, facing);
	}

	/**
	 *
	 * @param w the world to do this in
	 * @param x the pos of the core
	 * @param y the pos of the core
	 * @param z the pos of the core
	 * @param nx the pos of the block to repair
	 * @param ny the pos of the block to repair
	 * @param nz the pos of the block to repair
	 * @param facing the facing of the room
	 * @return true if something was repaired, false if not
	 */
	public boolean repairBlock(World w, int x, int y, int z, int nx, int ny, int nz, int facing)
	{
		CoordStore oldBlock = rotate(new CoordStore(nx-x,ny-y,nz-z),facing,primaryDoorFace);
		if(storage.containsKey(oldBlock))
		{
			SchemaStore st = storage.get(oldBlock);
			int newMeta = SchemaRotationHandler.getNewMetadata(st.getBlock(), st.getBlockMeta(), primaryDoorFace, facing);
			if((w.getBlock(nx,ny,nz) != st.getBlock()) || (w.getBlockMetadata(nx, ny, nz) != newMeta))
			{
				System.out.println("Repaired:" + nx +","+ny+","+nz+":" + newMeta);
				st.loadToWorld(w, newMeta, nx, ny, nz);
				return true;
			}
		}
		return false;
	}

	public int getFacing()
	{
		return primaryDoorFace;
	}

}
