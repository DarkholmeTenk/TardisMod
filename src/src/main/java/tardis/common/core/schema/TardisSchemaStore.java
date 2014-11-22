package tardis.common.core.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cpw.mods.fml.common.registry.GameData;

import net.minecraft.init.Blocks;

import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisSchemaStore
{	
	private String blockName = null;
	private Block block;
	private int blockMeta;
	private NBTTagCompound nbtStore = null;
	
	public static final TardisSchemaStore airBlock = new TardisSchemaStore(Blocks.air,0,null);
	
	private static HashSet<Block> bannedIDs = null;
	private static HashMap<String,Block> blockCache = new HashMap<String,Block>();
	
	public Block getBlock()
	{
		return block;
	}
	
	public int getBlockMeta()
	{
		return blockMeta;
	}
	
	private TardisSchemaStore(Block bid, int bm, NBTTagCompound nbt)
	{
		block = bid;
		blockMeta = bm;
		nbtStore = nbt;
	}
	
	private TardisSchemaStore()
	{
		if(bannedIDs == null)
		{
			bannedIDs = new HashSet<Block>();
			bannedIDs.add(TardisMod.tardisCoreBlock);
			bannedIDs.add(TardisMod.tardisConsoleBlock);
			bannedIDs.add(TardisMod.tardisEngineBlock);
		}
	}
	
	public void loadToWorld(World w,int x, int y, int z)
	{
		loadToWorld(w,blockMeta,x,y,z);
	}
	
	public void loadToWorld(World w,int meta, int x, int y, int z)
	{
		w.setBlock(x, y, z, block, meta, 3);
		if(nbtStore != null)
		{
			nbtStore.setInteger("x", x);
			nbtStore.setInteger("y", y);
			nbtStore.setInteger("z", z);
			TileEntity newTileEntity = TileEntity.createAndLoadEntity(nbtStore);
			//newTileEntity.invalidate();
			if(newTileEntity != null)
			{
				newTileEntity.setWorldObj(w);
				w.setTileEntity(x, y, z, newTileEntity);
				newTileEntity.validate();
			}
		}
	}
	
	public static TardisSchemaStore storeWorldBlock(World w,int x, int y, int z)
	{
		if(bannedIDs == null)
		{
			bannedIDs = new HashSet<Block>();
			bannedIDs.add(TardisMod.tardisCoreBlock);
			bannedIDs.add(TardisMod.tardisConsoleBlock);
			bannedIDs.add(TardisMod.tardisEngineBlock);
		}
		
		if(w.getBlock(x, y, z) == Blocks.air || bannedIDs.contains(w.getBlock(x, y, z)))
			return null;
		
		TardisSchemaStore newStore = new TardisSchemaStore();
		newStore.block = w.getBlock(x, y, z);
		newStore.blockMeta = w.getBlockMetadata(x, y, z);
		TileEntity te = w.getTileEntity(x, y, z);
		if(te != null)
		{
			newStore.nbtStore = new NBTTagCompound();
			te.writeToNBT(newStore.nbtStore);
		}
		newStore.blockName = getNameFromBlock(newStore.block);
		return newStore;
	}
	
	public static TardisSchemaStore loadFromNBT(NBTTagCompound nbt)
	{
		TardisSchemaStore newStore = new TardisSchemaStore();
		newStore.blockMeta = nbt.getInteger("tdSchemaBMD");
		if(nbt.hasKey("tdSchemaBName"))
		{
			String name = nbt.getString("tdSchemaBName");
			Block nbid = getBlockFromName(name);
			if(nbid != null)
			{
				TardisOutput.print("TSS", "loading " + nbid.getUnlocalizedName());
				newStore.block = nbid;
			}
			else
			{
				newStore.block = Blocks.air;
			}
		}
		if(nbt.hasKey("tdSchemaNBT"))
			newStore.nbtStore = nbt.getCompoundTag("tdSchemaNBT");
		//TardisOutput.print("TSS", newStore.toString());
		return newStore;
	}
	
	public NBTTagCompound getTagCompound()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("tdSchemaBMD", blockMeta);
		if(blockName == null)
			blockName = getNameFromBlock(block);
		if(blockName != null)
			nbt.setString("tdSchemaBName", blockName);
		if(nbtStore != null)
			nbt.setTag("tdSchemaNBT", nbtStore);
		return nbt;
	}

	@Override
	public String toString()
	{
		return "TardisSchemaStore [blockID=" + block.getUnlocalizedName() + ", blockMeta=" + blockMeta + "]";
	}

	private static String getNameFromBlock(Block id)
	{
		if(id != null)
			return id.getUnlocalizedName();
		return null;
	}
	
	private static Block getBlockFromName(String name)
	{
		TardisOutput.print("TSS", "Seaching for " + name);
		Block b = null;
		b = GameData.getBlockRegistry().get(name);
		if(b != null && !b.equals(Blocks.air))
			return b;
		else
		{
			TardisOutput.print("TSS", "Null, searching cache");
			if(blockCache.containsKey(name))
				return blockCache.get(name);
			else
			{
				Iterator<Block> blockIter = GameData.blockRegistry.iterator();
				boolean found = false;
				while(blockIter.hasNext())
				{
					b = blockIter.next();
					if(b.getUnlocalizedName().equals(name))
					{
						TardisOutput.print("TSS", "Matching " + name + " to " + b.getUnlocalizedName());
						blockCache.put(name, b);
						return b;
					}
					else
						blockCache.put(b.getUnlocalizedName(), b);
				}
			}
		}
		TardisOutput.print("TSS", "No block found for "+ name +":(");
		return null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((block == null) ? 0 : block.hashCode());
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
		if (block == null)
		{
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (blockMeta != other.blockMeta)
			return false;
		if (blockName == null)
		{
			if (other.blockName != null)
				return false;
		} else if (!blockName.equals(other.blockName))
			return false;
		if (nbtStore == null)
		{
			if (other.nbtStore != null)
				return false;
		} else if (!nbtStore.equals(other.nbtStore))
			return false;
		return true;
	}

}
