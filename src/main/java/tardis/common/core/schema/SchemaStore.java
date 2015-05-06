package tardis.common.core.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.SchemaCoreTileEntity;
import cpw.mods.fml.common.registry.GameData;

public class SchemaStore
{
	private String							blockName	= null;
	private Block							block;
	private int								blockMeta;
	private NBTTagCompound					nbtStore	= null;

	public static final SchemaStore			airBlock	= new SchemaStore(Blocks.air, 0, null);

	private static HashSet<Block>			bannedIDs	= null;
	private static HashMap<String, Block>	blockCache	= new HashMap<String, Block>();

	public Block getBlock()
	{
		return block;
	}

	public int getBlockMeta()
	{
		return blockMeta;
	}

	private SchemaStore(Block bid, int bm, NBTTagCompound nbt)
	{
		block = bid;
		blockMeta = bm;
		nbtStore = nbt;
	}

	private SchemaStore()
	{
		if (bannedIDs == null)
		{
			bannedIDs = new HashSet<Block>();
			bannedIDs.add(TardisMod.tardisCoreBlock);
			bannedIDs.add(TardisMod.tardisConsoleBlock);
			bannedIDs.add(TardisMod.tardisEngineBlock);
		}
	}

	public void loadToWorld(World w, int x, int y, int z)
	{
		loadToWorld(w, blockMeta, x, y, z);
	}

	public void loadToWorld(World w, int meta, int x, int y, int z)
	{
		if ((block == TardisMod.decoBlock) && (meta == 6))
		{
			block = TardisMod.decoTransBlock;
			meta = blockMeta = 0;
		}
		if (block == TardisMod.decoBlock)
		{
			switch(meta)
			{
				case 2: block = TardisMod.colorableRoundelBlock; meta = 3; break;
				case 4: block = TardisMod.colorableRoundelBlock; meta = 15; break;
				case 3: block = TardisMod.colorableWallBlock; meta = 15; break;
				case 5: block = TardisMod.colorableFloorBlock; meta = 15; break;
				case 7: block = TardisMod.colorableWallBlock; meta = 3; break;
			}
		}

		w.setBlock(x, y, z, block, meta, 3);
		if (nbtStore != null)
		{
			nbtStore.setInteger("x", x);
			nbtStore.setInteger("y", y);
			nbtStore.setInteger("z", z);
			TileEntity newTileEntity = TileEntity.createAndLoadEntity(nbtStore);
			if (newTileEntity != null)
			{
				newTileEntity.setWorldObj(w);
				w.setTileEntity(x, y, z, newTileEntity);
				newTileEntity.validate();
			}
		}
	}

	public static SchemaStore storeWorldBlock(World w, int x, int y, int z)
	{
		if (bannedIDs == null)
		{
			bannedIDs = new HashSet<Block>();
			bannedIDs.add(TardisMod.tardisCoreBlock);
			bannedIDs.add(TardisMod.tardisConsoleBlock);
			bannedIDs.add(TardisMod.tardisEngineBlock);
		}

		if ((w.getBlock(x, y, z) == Blocks.air) || bannedIDs.contains(w.getBlock(x, y, z))) return null;

		SchemaStore newStore = new SchemaStore();
		newStore.block = w.getBlock(x, y, z);
		newStore.blockMeta = w.getBlockMetadata(x, y, z);
		TileEntity te = w.getTileEntity(x, y, z);
		if ((te != null) && !(te instanceof SchemaCoreTileEntity))
		{
			newStore.nbtStore = new NBTTagCompound();
			te.writeToNBT(newStore.nbtStore);
		}
		newStore.blockName = getNameFromBlock(newStore.block);
		return newStore;
	}

	public static SchemaStore loadFromNBT(NBTTagCompound nbt)
	{
		SchemaStore newStore = new SchemaStore();
		newStore.blockMeta = nbt.getInteger("tdSchemaBMD");
		if (nbt.hasKey("tdSchemaBName"))
		{
			String name = nbt.getString("tdSchemaBName");
			Block nbid = getBlockFromName(name);
			if (nbid != null)
			{
				newStore.block = nbid;
			}
			else
			{
				newStore.block = Blocks.air;
			}
		}
		if (nbt.hasKey("tdSchemaNBT")) newStore.nbtStore = nbt.getCompoundTag("tdSchemaNBT");
		// TardisOutput.print("TSS", newStore.toString());
		return newStore;
	}

	public NBTTagCompound getTagCompound()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("tdSchemaBMD", blockMeta);
		if (blockName == null) blockName = getNameFromBlock(block);
		if (blockName != null) nbt.setString("tdSchemaBName", blockName);
		if (nbtStore != null) nbt.setTag("tdSchemaNBT", nbtStore);
		return nbt;
	}

	@Override
	public String toString()
	{
		return "SchemaStore [blockID=" + block.getUnlocalizedName() + ", blockMeta=" + blockMeta + "]";
	}

	private static String getNameFromBlock(Block id)
	{
		String blockName = GameData.getBlockRegistry().getNameForObject(id);
		if (blockName != null) return blockName;
		if (id != null) return id.getUnlocalizedName();
		return null;
	}

	private static Block getBlockFromName(String name)
	{
		Block b = null;
		b = GameData.getBlockRegistry().getObject(name);
		if ((b != null) && !b.equals(Blocks.air))
			return b;
		else
		{
			if (blockCache.containsKey(name))
				return blockCache.get(name);
			else
			{
				if (name.equals("TardisMod:tile.TardisMod.DecoBlockDark")) return TardisMod.decoBlock;
				Iterator<Block> blockIter = GameData.getBlockRegistry().iterator();
				while (blockIter.hasNext())
				{
					b = blockIter.next();
					if (b.getUnlocalizedName().equals(name))
					{
						if ((name == TardisMod.decoBlock.getUnlocalizedName()) && (b != TardisMod.decoBlock)) continue;
						blockCache.put(name, b);
						return b;
					}
					else
						blockCache.put(b.getUnlocalizedName(), b);
				}
			}
		}
		if (!b.equals(Blocks.air)) TardisOutput.print("TSS", "No block found for " + name + ":(");
		return null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((block == null) ? 0 : block.hashCode());
		result = (prime * result) + blockMeta;
		result = (prime * result) + ((blockName == null) ? 0 : blockName.hashCode());
		result = (prime * result) + ((nbtStore == null) ? 0 : nbtStore.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SchemaStore)) return false;
		SchemaStore other = (SchemaStore) obj;
		if (block == null)
		{
			if (other.block != null) return false;
		}
		else if (!block.equals(other.block)) return false;
		if (blockMeta != other.blockMeta) return false;
		if (blockName == null)
		{
			if (other.blockName != null) return false;
		}
		else if (!blockName.equals(other.blockName)) return false;
		if (nbtStore == null)
		{
			if (other.nbtStore != null) return false;
		}
		else if (!nbtStore.equals(other.nbtStore)) return false;
		return true;
	}

}
