package tardis.common.core.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import tardis.TardisMod;
import tardis.common.blocks.TardisInternalDoorBlock;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.exception.schema.SchemaCoreNotFoundException;
import tardis.common.core.exception.schema.SchemaDoorNotFoundException;
import tardis.common.tileents.TardisSchemaCoreTileEntity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisPartBlueprint
{
	private int[] bounds = new int[5]; //-X, -Z, X, Z, Y
	
	private TardisCoordStore primaryDoor = null;
	private int primaryDoorFace = -1;
	
	public HashMap<TardisCoordStore,TardisSchemaStore>storage = new HashMap<TardisCoordStore,TardisSchemaStore>();
	
	private String myName;

	public TardisPartBlueprint(World world, String name, int x, int y, int z) throws SchemaDoorNotFoundException, SchemaCoreNotFoundException
	{
		myName = name;
		if(world.getBlockId(x, y, z) == TardisMod.schemaCoreBlock.blockID)
		{
			getBounds(world,x,y,z);
			getDoor(world,x,y,z);
			if(primaryDoor == null)
				throw new SchemaDoorNotFoundException(bounds,x,y,z);
			scanStuff(world,x,y,z);
		}
		else
		{
			throw new SchemaCoreNotFoundException(x,y,z);
		}
	}
	
	private void getBounds(World world, int x, int y, int z)
	{
		int schemaID = TardisMod.schemaBlock.blockID;
		int tmp = 1;
		boolean cont = true;
		while(cont)
		{
			cont = false;
			if(world.getBlockId(x - tmp, y, z) == schemaID)
			{
				cont = true;
				bounds[0] = tmp;
			}
			
			if(world.getBlockId(x, y, z - tmp) == schemaID)
			{
				cont = true;
				bounds[1] = tmp;
			}
			
			if(world.getBlockId(x + tmp, y, z) == schemaID)
			{
				cont = true;
				bounds[2] = tmp;
			}
			
			if(world.getBlockId(x, y, z + tmp) == schemaID)
			{
				cont = true;
				bounds[3] = tmp;
			}
			tmp++;
		}
		
		tmp = 0;
		cont = true;
		while(cont)
		{
			cont = false;
			if(world.getBlockId(x-bounds[0], y+tmp, z-bounds[1]) == schemaID)
			{
				bounds[4] = tmp;
				cont = true;
			}
			tmp++;
		}
	}
	
	private void handleDoor(World w, int x, int y, int z, int xL,int yL,int zL, int face)
	{
		boolean primary = (w.getBlockMetadata(x, y, z) % 8) >= 4;
		if(primary)
		{
			primaryDoor = new TardisCoordStore(xL,yL,zL);
			primaryDoorFace = face;
		}
		w.setBlockMetadataWithNotify(x, y, z, face + (primary ? 4 : 0), 3);
		TardisInternalDoorBlock.manageConnected(w, x, y, z, face);
		TardisInternalDoorBlock.manageConnected(w, x+TardisInternalDoorBlock.dx(face), y, z+TardisInternalDoorBlock.dz(face), face);
	}
	
	private void getDoor(World world, int x, int y, int z, int facing)
	{
		int[] bounds = moddedBounds(facing);
		for(int zL = -bounds[1];zL <= bounds[3];zL++)
		{
			int xL = -bounds[0];
			
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,0);
			
			xL = bounds[2];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,2);
		}
		
		for(int xL = -bounds[0];xL <= bounds[2];xL++)
		{
			int zL = -bounds[1];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,1);
			
			zL = bounds[3];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,3);
		}
	}
	
	private void getDoor(World world, int x,int y, int z)
	{
		for(int zL = -bounds[1];zL <= bounds[3];zL++)
		{
			int xL = -bounds[0];
			
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,0);
			
			xL = bounds[2];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,2);
		}
		
		for(int xL = -bounds[0];xL <= bounds[2];xL++)
		{
			int zL = -bounds[1];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,1);
			
			zL = bounds[3];
			for(int yL=0;yL<=bounds[4];yL++)
				if(world.getBlockId(x+xL,y+yL,z+zL) == TardisMod.internalDoorBlock.blockID)
					handleDoor(world,x+xL,y+yL,z+zL,xL,yL,zL,3);
		}
	}
	
	private void scanStuff(World w, int x, int y, int z)
	{
		for(int xL = -bounds[0];xL<=bounds[2];xL++)
		{
			for(int yL = 0; yL <= bounds[4];yL++)
			{
				for(int zL = -bounds[1];zL <= bounds[3];zL++)
				{
					TardisCoordStore coord = new TardisCoordStore(xL,yL,zL);
					TardisSchemaStore store = TardisSchemaStore.storeWorldBlock(w,x+xL,y+yL,z+zL);
					if(store != null)
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
		if(primaryDoor != null)
		{
			nbt.setString("primaryDoor", primaryDoor.toString());
			nbt.setInteger("primaryDoorFace", primaryDoorFace);
		}
		NBTTagCompound store = new NBTTagCompound();
		for(TardisCoordStore st : storage.keySet())
		{
			TardisOutput.print("TPB", "Adding " + st.toString() + " to store variable");
			store.setCompoundTag(st.toString(), storage.get(st).getTagCompound());
		}
		nbt.setCompoundTag("storage", store);
		try
		{
			DataOutputStream stream = new DataOutputStream(new FileOutputStream(saveFile));
			NBTTagCompound.writeNamedTag(nbt, stream);
			stream.close();
		}
		catch(IOException e)
		{
			TardisOutput.print("TPB", "Error saving to " + saveFile +":" + e.getMessage());
		}
	}
	
	public TardisPartBlueprint(File loadFile)
	{
		try
		{
			DataInputStream stream = new DataInputStream(new FileInputStream(loadFile));
			NBTTagCompound nbt = (NBTTagCompound) NBTTagCompound.readNamedTag(stream);
			myName = nbt.getString("name");
			bounds = nbt.getIntArray("bounds");
			if(nbt.hasKey("primaryDoor"))
			{
				primaryDoor = TardisCoordStore.fromString(nbt.getString("primaryDoor"));
				primaryDoorFace = nbt.getInteger("primaryDoorFace");
			}
			if(nbt.hasKey("storage"))
			{
				NBTTagCompound store = nbt.getCompoundTag("storage");
				for(int xL = -bounds[0];xL<=bounds[2];xL++)
				{
					for(int yL = 0; yL <= bounds[4];yL++)
					{
						for(int zL = -bounds[1];zL <= bounds[3];zL++)
						{
							TardisCoordStore coord = new TardisCoordStore(xL,yL,zL);
							if(store.hasKey(coord.toString()))
							{
								TardisSchemaStore tempStore = TardisSchemaStore.loadFromNBT(store.getCompoundTag(coord.toString()));
								storage.put(coord,tempStore);
							}
						}
					}
				}
			}
			stream.close();
		}
		catch(IOException e)
		{
			TardisOutput.print("TPB", "IOException " + e.getMessage(),TardisOutput.Priority.ERROR);
		}
	}
	
	public TardisCoordStore getPrimaryDoorPos(int facing)
	{
		return rotate(primaryDoor,primaryDoorFace,facing);
	}
	
	private TardisCoordStore rotate(TardisCoordStore key,int cFace,int dFace)
	{
		if(cFace == dFace)
			return key;
		if((cFace == 1 && dFace == 3) || (cFace == 3 && dFace == 1) || (cFace == 0 && dFace == 2) || (cFace == 2 && dFace == 0))
			return key.rotate();
		if(cFace == 0 && dFace == 3 || (dFace < cFace && !(cFace == 3 && dFace == 0)))
			return key.rotateRight();
		else
			return key.rotateLeft();
	}
	
	private void hollow(World w, int x, int y, int z, int facing)
	{
		TardisCoordStore min = new TardisCoordStore(-bounds[0],0,-bounds[1]);
		TardisCoordStore max = new TardisCoordStore(bounds[2],bounds[4],bounds[3]);
		min = rotate(min,primaryDoorFace,facing);
		max = rotate(max,primaryDoorFace,facing);
		int minX = x+Math.min(min.x, max.x);
		int maxX = x+Math.max(min.x, max.x);
		int minY = y+Math.min(min.y, max.y);
		int maxY = y+Math.max(min.y, max.y);
		int minZ = z+Math.min(min.z, max.z);
		int maxZ = z+Math.max(min.z, max.z);
		TardisOutput.print("TPB", "Clearing area"+minX+","+maxX+","+minY+","+maxY+","+minZ+","+maxZ);
		for(int i = minX;i<=maxX;i++)
			for(int j = minY;j<=maxY;j++)
				for(int k = minZ;k<=maxZ;k++)
					if(!w.isAirBlock(i, j, k) && Helper.isBlockRemovable(w.getBlockId(i, j, k)))
						w.setBlockToAir(i, j,k);
	}
	
	public int[] moddedBounds(int facing)
	{
		int[] newBounds = new int[5];
		TardisCoordStore min = new TardisCoordStore(-bounds[0],0,-bounds[1]);
		TardisCoordStore max = new TardisCoordStore(bounds[2],bounds[4],bounds[3]);
		min = rotate(min,primaryDoorFace,facing);
		max = rotate(max,primaryDoorFace,facing);
		newBounds[0] = -Math.min(min.x, max.x);
		newBounds[1] = -Math.min(min.z, max.z);
		newBounds[2] =  Math.max(min.x, max.x);
		newBounds[3] =  Math.max(min.z, max.z);
		newBounds[4] =  Math.max(min.y, max.y);
		return newBounds;
	}
	
	public boolean roomFor(World w,int x, int y, int z,int facing)
	{
		int[] bounds = moddedBounds(facing);
		if(y < 0 || (y+bounds[4]) > 254)
			return false;
		for(int xL = -bounds[0];xL <= bounds[2];xL++)
			for(int yL = 0;yL <= bounds[4];yL++)
				for(int zL = -bounds[1];zL <= bounds[3];zL++)
					if(w.getBlockId(x+xL, y+yL, z+zL) != 0)
						return false;
		return true;
	}
	
	public void reconstitute(World w,int x, int y, int z, int facing)
	{
		hollow(w,x,y,z,facing);
		for(TardisCoordStore key: storage.keySet())
		{
			TardisSchemaStore st = storage.get(key);
			TardisCoordStore modKey = key;
			int newMeta = TardisSchemaRotationHandler.getNewMetadata(st.getBlockID(), st.getBlockMeta(), primaryDoorFace, facing);
			if(facing != primaryDoorFace)
				modKey = rotate(key,primaryDoorFace,facing);
			st.loadToWorld(w, newMeta, x+modKey.x, y+modKey.y, z+modKey.z);
			if(st.getBlockID() == TardisMod.schemaCoreBlock.blockID)
			{
				TileEntity te = w.getBlockTileEntity(x+modKey.x, y+modKey.y, z+modKey.z);
				if(te != null && te instanceof TardisSchemaCoreTileEntity)
					((TardisSchemaCoreTileEntity)te).setData(myName,moddedBounds(facing),facing);
			}
			else if(st.getBlockID() == Block.chest.blockID)
				w.setBlockMetadataWithNotify(x+modKey.x, y+modKey.y, z+modKey.z, newMeta, 3);
		}
		getDoor(w,x,y,z,facing);
	}

}
