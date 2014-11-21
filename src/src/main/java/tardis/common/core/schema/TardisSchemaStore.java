package tardis.common.core.schema;

import java.util.HashMap;
import java.util.HashSet;

import appeng.api.Blocks;

import tardis.TardisMod;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisSchemaStore
{	
	private String blockName = null;
	private int blockID;
	private int blockMeta;
	private NBTTagCompound nbtStore = null;
	
	public static final TardisSchemaStore airBlock = new TardisSchemaStore(0,0,null);
	
	private static HashSet<Integer> bannedIDs = null;
	private static HashMap<String,Integer> blockCache = new HashMap<String,Integer>();
	
	public int getBlockID()
	{
		return blockID;
	}
	
	public int getBlockMeta()
	{
		return blockMeta;
	}
	
	private TardisSchemaStore(int bid, int bm, NBTTagCompound nbt)
	{
		blockID = bid;
		blockMeta = bm;
		nbtStore = nbt;
	}
	
	private TardisSchemaStore()
	{
		if(bannedIDs == null)
		{
			bannedIDs = new HashSet<Integer>();
			bannedIDs.add(TardisMod.tardisCoreBlock.blockID);
			bannedIDs.add(TardisMod.tardisConsoleBlock.blockID);
			bannedIDs.add(TardisMod.tardisEngineBlock.blockID);
		}
	}
	
	public NBTTagCompound getTagCompound()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("tdSchemaBID", blockID);
		nbt.setInteger("tdSchemaBMD", blockMeta);
		if(blockName != null)
			nbt.setString("tdSchemaBName", blockName);
		if(nbtStore != null)
			nbt.setCompoundTag("tdSchemaNBT", nbtStore);
		return nbt;
	}
	
	public void loadToWorld(World w,int x, int y, int z)
	{
		loadToWorld(w,blockMeta,x,y,z);
	}
	
	public void loadToWorld(World w,int meta, int x, int y, int z)
	{
		w.setBlock(x, y, z, blockID, meta, 3);
		if(nbtStore != null)
		{
			nbtStore.setInteger("x", x);
			nbtStore.setInteger("y", y);
			nbtStore.setInteger("z", z);
			TileEntity newTileEntity = TileEntity.createAndLoadEntity(nbtStore);
			//newTileEntity.invalidate();
			if(newTileEntity != null)
			{
				newTileEntity.worldObj = w;
				w.setBlockTileEntity(x, y, z, newTileEntity);
				newTileEntity.validate();
			}
		}
	}
	
	public static TardisSchemaStore storeWorldBlock(World w,int x, int y, int z)
	{
		if(w.getBlockId(x, y, z) == 0 || bannedIDs.contains(w.getBlockId(x, y, z)))
			return null;
		
		TardisSchemaStore newStore = new TardisSchemaStore();
		newStore.blockID = w.getBlockId(x, y, z);
		newStore.blockMeta = w.getBlockMetadata(x, y, z);
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te != null)
		{
			newStore.nbtStore = new NBTTagCompound();
			te.writeToNBT(newStore.nbtStore);
		}
		newStore.blockName = getNameFromID(newStore.blockID);
		return newStore;
	}
	
	public static TardisSchemaStore loadFromNBT(NBTTagCompound nbt)
	{
		TardisSchemaStore newStore = new TardisSchemaStore();
		newStore.blockID = nbt.getInteger("tdSchemaBID");
		newStore.blockMeta = nbt.getInteger("tdSchemaBMD");
		if(nbt.hasKey("tdSchemaBName"))
		{
			Integer nbid = getIDFromName(nbt.getString("tdSchemaBName"));
			if(nbid != null)
				newStore.blockID = nbid;
		}
		if(nbt.hasKey("tdSchemaNBT"))
			newStore.nbtStore = nbt.getCompoundTag("tdSchemaNBT");
		//TardisOutput.print("TSS", newStore.toString());
		return newStore;
	}
	
	@Override
	public String toString()
	{
		return "TardisSchemaStore [blockID=" + blockID + ", blockMeta=" + blockMeta + "]";
	}

	private static String getNameFromID(int id)
	{
		if(id == TardisMod.decoBlock.blockID)
			return "decoBlock";
		if(id == TardisMod.internalDoorBlock.blockID)
			return "internalDoorBlock";
		if(id == TardisMod.schemaBlock.blockID)
			return "schemaBlock";
		if(id == TardisMod.schemaComponentBlock.blockID)
			return "schemaComponentBlock";
		if(id == TardisMod.schemaCoreBlock.blockID)
			return "schemaCoreBlock";
		if(id == TardisMod.tardisBlock.blockID)
			return "tardisBlock";
		if(id == TardisMod.tardisCoreBlock.blockID)
			return "tardisCoreBlock";
		if(id == TardisMod.tardisTopBlock.blockID)
			return "tardisTopBlock";
		if(id == TardisMod.tardisConsoleBlock.blockID)
			return "consoleBlock";
		if(id == TardisMod.stairBlock.blockID)
			return "stairBlock";
		if(id == TardisMod.slabBlock.blockID)
			return "slabBlock";
		if(id == TardisMod.componentBlock.blockID)
			return "compBlock";
		Block[] bList = Block.blocksList;
		if(id > 0 && bList.length > id)
		{
			Block b = bList[id];
			if(b != null)
				return b.getUnlocalizedName();
		}
		return null;
	}
	
	private static Integer getIDFromName(String name)
	{
		if(name == null)
			return null;
		if(name.equals("decoBlock"))
			return TardisMod.decoBlock.blockID;
		if(name.equals("internalDoorBlock"))
			return TardisMod.internalDoorBlock.blockID;
		if(name.equals("schemaBlock"))
			return TardisMod.schemaBlock.blockID;
		if(name.equals("schemaComponentBlock"))
			return TardisMod.schemaComponentBlock.blockID;
		if(name.equals("schemaCoreBlock"))
			return TardisMod.schemaCoreBlock.blockID;
		if(name.equals("tardisBlock"))
			return TardisMod.tardisBlock.blockID;
		if(name.equals("tardisCoreBlock"))
			return TardisMod.tardisCoreBlock.blockID;
		if(name.equals("tardisTopBlock"))
			return TardisMod.tardisTopBlock.blockID;
		if(name.equals("consoleBlock"))
			return TardisMod.tardisConsoleBlock.blockID;
		if(name.equals("stairBlock"))
			return TardisMod.stairBlock.blockID;
		if(name.equals("slabBlock"))
			return TardisMod.slabBlock.blockID;
		if(name.equals("compBlock"))
			return TardisMod.componentBlock.blockID;
		Integer b = null;
		if(blockCache.containsKey(name))
			b = blockCache.get(name);
		else
		{
			Block[] bList = Block.blocksList;
			for(int i = 0; i<4096;i++)
			{
				Block block = bList[i];
				if(block != null && block.getUnlocalizedName().equals(name))
				{
					blockCache.put(name,i);
					b = i;
				}
			}
		}
		if(b != null)
			return b;
		return null;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + blockID;
		result = prime * result + blockMeta;
		result = prime * result + ((blockName == null) ? 0 : blockName.hashCode());
		result = prime * result + ((nbtStore == null) ? 0 : nbtStore.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TardisSchemaStore))
			return false;
		TardisSchemaStore other = (TardisSchemaStore) obj;
		if (blockID != other.blockID)
			return false;
		if (blockMeta != other.blockMeta)
			return false;
		if (blockName == null)
		{
			if (other.blockName != null)
				return false;
		}
		else if (!blockName.equals(other.blockName))
			return false;
		if (nbtStore == null)
		{
			if (other.nbtStore != null)
				return false;
		}
		else if (!nbtStore.equals(other.nbtStore))
			return false;
		return true;
	}

}
