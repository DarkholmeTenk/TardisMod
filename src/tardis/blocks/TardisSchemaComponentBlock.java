package tardis.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.api.IScrewable;
import tardis.core.TardisOutput;
import tardis.items.TardisSonicScrewdriverItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TardisSchemaComponentBlock extends TardisAbstractBlock
{

	public TardisSchemaComponentBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("SchemaComponent");
		setSubNames("DoorConnector","DoorConnectorHidden","ControlPanel");
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World w, int x, int y, int z)
	{
		return getCollisionBoundingBoxFromPool(w,x,y,z);
	}
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World w, int x, int y, int z)
	{
		if(w.getBlockMetadata(x, y, z) == 1)
		{
			return AxisAlignedBB.getAABBPool().getAABB(0,0,0,0,0,0);
		}
		else
			return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@Override
	public boolean isBlockNormalCube(World w, int x, int y, int z)
    {
        return w.getBlockMetadata(x,y,z) != 1;
    }
	
	@Override 
	public boolean isOpaqueCube()
	{
		return false;
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
		if(w.getBlockMetadata(x, y, z) == 1)
			return false;
		return true;
    }

	public static boolean isDoorConnector(World w,int x, int y, int z)
	{
		if(w.getBlockId(x,y,z) == TardisMod.schemaComponentBlock.blockID && w.getBlockMetadata(x, y, z) <= 1)
			return true;
		return false;
	}
	
	//Activate the thingum if this is a thingum
	@Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float t, float j, float k)
    {
    	int meta = w.getBlockMetadata(x, y, z);
    	if(meta == 2)
    	{
    		boolean found = false;
    		for(int i = 1;i<10 && found == false;i++)
    		{
    			if(w.getBlockId(x, y-i, z) == TardisMod.schemaCoreBlock.blockID)
    			{
    				return ((TardisAbstractBlockContainer)TardisMod.schemaCoreBlock).onBlockActivated(w, x, y-i, z, pl, s, t, j, k);
    			}
    		}
    	}
        return false;
    }

}
