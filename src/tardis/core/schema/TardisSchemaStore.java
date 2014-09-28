package tardis.core.schema;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TardisSchemaStore
{	
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
		if(nbtStore != null)
			nbt.setCompoundTag("tdSchemaNBT", nbtStore);
		return nbt;
	}
	
	public void loadToWorld(World w,int x, int y, int z)
	{
		w.setBlock(x, y, z, blockID, blockMeta, 3);
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
		if(w.getBlockId(x, y, z) == 0)
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
		return newStore;
	}
	
	public static TardisSchemaStore loadFromNBT(NBTTagCompound nbt)
	{
		TardisSchemaStore newStore = new TardisSchemaStore();
		newStore.blockID = nbt.getInteger("tdSchemaBID");
		newStore.blockMeta = nbt.getInteger("tdSchemaBMD");
		if(nbt.hasKey("tdSchemaNBT"))
			newStore.nbtStore = nbt.getCompoundTag("tdSchemaNBT");
		return newStore;
	}

}
