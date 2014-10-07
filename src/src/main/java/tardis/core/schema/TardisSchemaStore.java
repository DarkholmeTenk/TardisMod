package tardis.core.schema;

import tardis.TardisMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisSchemaStore
{	
	private String blockName = null;
	private int blockID;
	private int blockMeta;
	private NBTTagCompound nbtStore = null;
	
	public int getBlockID()
	{
		return blockID;
	}
	
	public int getBlockMeta()
	{
		return blockMeta;
	}
	
	private TardisSchemaStore()
	{
		
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
		if(w.getBlockId(x, y, z) == 0 || w.getBlockId(x, y, z) == TardisMod.tardisCoreBlock.blockID)
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
		return newStore;
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
		
		return null;
	}
	
	private static Integer getIDFromName(String name)
	{
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
		return null;
	}

}
