package tardis.common.blocks;

import java.io.File;
import java.util.List;

import tardis.TardisMod;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.schema.TardisCoordStore;
import tardis.common.core.schema.TardisPartBlueprint;
import tardis.common.items.TardisSchemaItem;
import tardis.common.items.TardisSonicScrewdriverItem;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TardisInternalDoorBlock extends TardisAbstractBlock
{
	public TardisInternalDoorBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("InternalDoor");
		setSubNames("InternalDoor","InternalDoorPrimary");
	}
	
	@Override
	public String getSubName(int num)
	{
		if(num % 8 < 4)
			return super.getSubName(0);
		else
			return super.getSubName(1);
	}
	
	@Override
	public void getSubBlocks(int itemID,CreativeTabs tab,List itemList)
	{
		itemList.add(new ItemStack(itemID,1,0));
		itemList.add(new ItemStack(itemID,1,4));
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}
	
	public static int opposingFace(int myFace)
	{
		return ((myFace + 2) % 4);
	}
	
	public static int dx(int myFace)
	{
		if(myFace % 4 == 0)
			return -1;
		if(myFace % 4 == 2)
			return 1;
		return 0;
	}
	
	public static int dz(int myFace)
	{
		if(myFace % 4 == 1)
			return -1;
		if(myFace % 4 == 3)
			return 1;
		return 0;
	}
	
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int side, float i, float j, float k)
	{
		TardisOutput.print("TIDB","OBA"+x+","+y+","+z);
		if(player != null)
		{
			ItemStack held =player.getHeldItem();
			if(held != null)
			{
				Item base = held.getItem();
				NBTTagCompound tag = held.stackTagCompound;
				if(base != null && tag != null && !w.isRemote)
				{
					boolean schemaCarrier = (base instanceof TardisSchemaItem);
					if(base instanceof TardisSonicScrewdriverItem)
						schemaCarrier = TardisSonicScrewdriverItem.getMode(held).equals(TardisScrewdriverMode.Schematic);
					
					TardisCoreTileEntity te = Helper.getTardisCore(w);
					if(te == null || (te.canModify(player)))
					{
						if(schemaCarrier && tag.hasKey("schemaName"))
						{
							String name = tag.getString("schemaName");
							File schemaFile = TardisMod.configHandler.getSchemaFile(name);
							TardisPartBlueprint pb = new TardisPartBlueprint(schemaFile);
							int facing = w.getBlockMetadata(x, y, z) % 4;
							TardisCoordStore door = pb.getPrimaryDoorPos(opposingFace(facing));
							int nX = x - door.x + dx(facing);
							int nY = y - door.y;
							int nZ = z - door.z + dz(facing);
							TardisOutput.print("TIDB","OBA"+door.x+","+door.y+","+door.z);
							if((!w.isRemote) && pb.roomFor(w, nX, nY, nZ, opposingFace(facing)))
							{
								if(te == null || te.addRoom(false, null)) //pass null as arg for schemacore since it adds itself
								{
									pb.reconstitute(w, nX, nY, nZ, opposingFace(facing));
									manageConnected(w,x,y,z,facing);
									manageConnected(w,x+dx(facing),y,z+dz(facing),facing);
								}
								else if(!w.isRemote)
								{
									player.addChatMessage("Too many rooms in this TARDIS");
								}
							}
							else
							{
								if(!w.isRemote)
								{
									TardisOutput.print("TIDB", "NoRoom:"+nX+","+nY+","+nZ,TardisOutput.Priority.DEBUG);
									player.addChatMessage("Not enough room for schematic");
								}
							}
						}
						else if(schemaCarrier)
						{
							player.addChatMessage("No schematic loaded");
						}
					}
					else if(!w.isRemote)
					{
						player.addChatMessage("You do not own this TARDIS");
					}
				}
				else if(w.isRemote)
					return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World w, int x, int y, int z)
	{
		return getCollisionBoundingBoxFromPool(w,x,y,z);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(w.getBlockMetadata(x, y, z) >= 8)
		{
			return AxisAlignedBB.getAABBPool().getAABB(0,0,0,0,0,0);
		}
		else
			return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@Override
	public boolean isBlockNormalCube(World w, int x, int y, int z)
	{
		return w.getBlockMetadata(x,y,z) < 8;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return shouldSideBeRendered(w,x,y,z,s);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int s)
	{
		switch(s)
		{
			case 0: y++;break;
			case 1: y--;break;
			case 2: z++;break;
			case 3: z--;break;
			case 4: x++;break;
			case 5: x--;break;
		}
		if(w.getBlockMetadata(x, y, z) >= 8)
			return false;
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, int bID)
	{
		super.onNeighborBlockChange(w, x, y, z, bID);
		if(bID != TardisMod.schemaComponentBlock.blockID)
			manageConnected(w,x,y,z,w.getBlockMetadata(x, y, z)%4);
	}
	
	public static void manageConnected(World w, int x, int y, int z, int facing)
	{
		boolean connected = hasConnector(w,x,y,z);
		if(w.getBlockMetadata(x, y, z) < 8 && connected)
			w.setBlockMetadataWithNotify(x, y, z, 8+w.getBlockMetadata(x, y, z), 3);
		else if(w.getBlockMetadata(x, y, z) >= 8 && !connected)
			w.setBlockMetadataWithNotify(x, y, z, w.getBlockMetadata(x, y, z)-8, 3);
		//TardisOutput.print("TIDB", "Connected:" + connected);
		int mY=0;
		int MY=0;
		int mD=0;
		int MD=0;
		
		int dX=(facing == 1 || facing == 3) ? 1 : 0;
		int dZ=(facing == 1 || facing == 3) ? 0 : 1;
		for(int i=1;TardisSchemaComponentBlock.isDoorConnector(w, x+(i*dX), y, z+(i*dZ));i++)
			MD=i;
		for(int i=1;TardisSchemaComponentBlock.isDoorConnector(w, x-(i*dX), y, z-(i*dZ));i++)
			mD=i;
		for(int i=1;TardisSchemaComponentBlock.isDoorConnector(w, x, y+i, z);i++)
			MY=i;
		for(int i=1;TardisSchemaComponentBlock.isDoorConnector(w, x, y-i, z);i++)
			mY=i;
		//TardisOutput.print("TIDB", "ConnHandle:" + dX + "," + dZ + ":-" + mD + "to" + MD + ":-"+mY+"to"+MY);
		for(int d=-mD;d<=MD;d++)
		{
			for(int cY=-mY;cY<=MY;cY++)
			{
				if(TardisSchemaComponentBlock.isDoorConnector(w, x+(d*dX), y+cY, z+(d*dZ)))
					w.setBlockMetadataWithNotify(x+(d*dX), y+cY, z+(d*dZ), connected?1:0, 3);
			}
		}
	}
	
	public static boolean hasConnector(World w, int x, int y, int z)
	{
		if(w.getBlockId(x, y, z) == TardisMod.internalDoorBlock.blockID)
		{
			int facing = w.getBlockMetadata(x, y, z) % 4;
			int connectedDoorX = x + dx(facing);
			int connectedDoorZ = z + dz(facing);
			if(w.getBlockId(connectedDoorX, y, connectedDoorZ) == TardisMod.internalDoorBlock.blockID)
				if((w.getBlockMetadata(connectedDoorX, y, connectedDoorZ) % 4) == opposingFace(facing))
					return true;
		}
		return false;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z)
	{
		if(w.getBlockMetadata(x, y, z) >= 8)
			this.setBlockBounds(0, 0, 0, 0, 0, 0);
		else
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public void setBlockBoundsForItemRender()
	{
		setBlockBounds(0,0,0,1,1,1);
	}
	
	@Override
	public Icon getIcon(int s, int d)
	{
		int iconMeta = (d % 8) >= 4 ? 1 : 0;
		//TardisOutput.print("TIDB", "Meta"+d+"->"+iconMeta);
		return super.getIcon(s, iconMeta);
	}
	
	@Override
	public int getDamageValue(World par1World, int par2, int par3, int par4)
    {
        return super.getDamageValue(par1World, par2, par3, par4) & 7;
    }
	
	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

}
