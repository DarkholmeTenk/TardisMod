package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.ScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.core.schema.CoordStore;
import tardis.common.core.schema.PartBlueprint;
import tardis.common.items.SchemaItem;
import tardis.common.items.SonicScrewdriverItem;
import tardis.common.tileents.CoreTileEntity;

public class InternalDoorBlock extends AbstractBlock
{
	public InternalDoorBlock()
	{
		super(TardisMod.modName);
	}
	
	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return InternalDoorItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("InternalDoor");
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
	public void getSubBlocks(Item itemID,CreativeTabs tab,List itemList)
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
		TardisOutput.print("TIDB","OBA"+x+","+y+","+z+":"+w.getBlockMetadata(x, y, z));
		if(player != null)
		{
			ItemStack held =player.getHeldItem();
			if(held != null)
			{
				Item base = held.getItem();
				NBTTagCompound tag = held.stackTagCompound;
				if(base != null && tag != null && ServerHelper.isServer())
				{
					boolean schemaCarrier = (base instanceof SchemaItem);
					if(base instanceof SonicScrewdriverItem)
						schemaCarrier = SonicScrewdriverItem.getMode(held).equals(ScrewdriverMode.Schematic);
					
					CoreTileEntity te = Helper.getTardisCore(w);
					if(te == null || (te.canModify(player)))
					{
						if(schemaCarrier && tag.hasKey("schemaName") && tag.hasKey("schemaCat"))
						{
							String category = tag.getString("schemaCat");
							String name = tag.getString("schemaName");
							//File schemaFile = TardisMod.schemaHandler.getSchemaFile(category,name);
							//PartBlueprint pb = new PartBlueprint(schemaFile);
							PartBlueprint pb = TardisMod.schemaHandler.getSchema(category, name);
							int facing = w.getBlockMetadata(x, y, z) % 4;
							CoordStore door = pb.getPrimaryDoorPos(opposingFace(facing));
							int nX = x - door.x + dx(facing);
							int nY = y - door.y;
							int nZ = z - door.z + dz(facing);
							TardisOutput.print("TIDB","OBA"+door.x+","+door.y+","+door.z);
							if(pb.roomFor(w, nX, nY, nZ, opposingFace(facing)))
							{
								if(te == null || te.addRoom(false, null)) //pass null as arg for schemacore since it adds itself
								{
									pb.reconstitute(w, nX, nY, nZ, opposingFace(facing));
								}
								else if(!w.isRemote)
								{
									player.addChatMessage(new ChatComponentText("Too many rooms in this TARDIS"));
								}
							}
							else
							{
								if(!w.isRemote)
								{
									TardisOutput.print("TIDB", "NoRoom:"+nX+","+nY+","+nZ,TardisOutput.Priority.DEBUG);
									player.addChatMessage(new ChatComponentText("Not enough room for schematic"));
								}
							}
						}
						else if(schemaCarrier)
						{
							player.addChatMessage(new ChatComponentText("No schematic loaded"));
						}
					}
					else if(!w.isRemote)
					{
						player.addChatMessage(CoreTileEntity.cannotModifyMessage);
					}
				}
				else if(!ServerHelper.isServer())
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
		return super.getSelectedBoundingBoxFromPool(w, x, y, z);
		//return getCollisionBoundingBoxFromPool(w,x,y,z);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(w.getBlockMetadata(x, y, z) >= 8)
		{
			return AxisAlignedBB.getBoundingBox(0,0,0,0,0,0);
		}
		else
			return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess w, int x, int y, int z)
	{
		return w.getBlockMetadata(x,y,z) < 8;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return isNormalCube(w,x,y,z);
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
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
	public void onNeighborBlockChange(World w, int x, int y, int z, Block bID)
	{
		super.onNeighborBlockChange(w, x, y, z, bID);
		if(bID != TardisMod.schemaComponentBlock)
			manageConnected(w,x,y,z,w.getBlockMetadata(x, y, z)%4);
	}
	
	public static void manageConnected(World w, int x, int y, int z, int facing)
	{
		if(!ServerHelper.isServer())
			return;
	}
	
	public static boolean hasConnector(World w, int x, int y, int z)
	{
		if(w.getBlock(x, y, z) == TardisMod.internalDoorBlock)
		{
			int facing = w.getBlockMetadata(x, y, z) % 4;
			int connectedDoorX = x + dx(facing);
			int connectedDoorZ = z + dz(facing);
			if(w.getBlock(connectedDoorX, y, connectedDoorZ) == TardisMod.internalDoorBlock)
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
	public IIcon getIcon(int s, int d)
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
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        //this.setBlockBoundsBasedOnState(w, x, y, z);
		if(isNormalCube(w,x,y,z))
			super.addCollisionBoxesToList(w, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
    }

}
