package tardis.common.blocks;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
		setSubNames("DoorConnector","DoorConnectorHidden","ControlPanel","ConsoleBlock", "TardisDoorBottom","TardisDoorTop","ConsoleTop");
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
		if(w.getBlockMetadata(x, y, z) == 1 || w.getBlockMetadata(x, y, z) == 3 || w.getBlockMetadata(x,y,z) == 6)
			return false;
		return true;
    }

	public static boolean isDoorConnector(World w,int x, int y, int z)
	{
		if(w.getBlockId(x,y,z) == TardisMod.schemaComponentBlock.blockID && w.getBlockMetadata(x, y, z) <= 1)
			return true;
		return false;
	}
	
	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }
	
	//Activate the thingum if this is a thingum
	@Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float t, float j, float k)
    {
		if(w.isRemote)
			return true;
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
    	if(meta == 3 || meta == 6)
    	{
    		TardisConsoleTileEntity console = Helper.getTardisConsole(w);
			if(console != null)
				return console.activate(pl, x, y, z, t, j, k);
    	}
    	if(meta == 4 || meta == 5)
    	{
    		TardisCoreTileEntity te = Helper.getTardisCore(w);
    		if(te != null)
    		{
				if((!w.isRemote) && (!te.changeLock(pl,true)))
					te.leaveTardis(pl,false);
    		}
    		return true;
    	}
        return false;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z)
    {
        if(w.getBlockMetadata(x, y, z) == 3)
        	this.setBlockBounds(0, 0.5F, 0, 1, 1, 1);
        else if(w.getBlockMetadata(x, y, z) == 6)
        	this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);
        else if(w.getBlockMetadata(x,y,z) == 1)
        	this.setBlockBounds(0,0,0,0,0,0);
        else
        	this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }
	
	@Override
	public void setBlockBoundsForItemRender()
    {
		setBlockBounds(0,0,0,1,1,1);
    }

}
