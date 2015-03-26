package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;

public class SchemaComponentBlock extends AbstractBlock
{
	public SchemaComponentBlock()
	{
		super(TardisMod.modName);
	}
	
	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return SchemaComponentItemBlock.class;
	}

	@Override
	public void initData()
	{
		this.opaque = false;
		setBlockName("SchemaComponent");
		setSubNames("DoorConnector","DoorConnectorHidden","ControlPanel","ConsoleBlock", "TardisDoorBottom","TardisDoorTop","ConsoleTop","Engine","TimeRotor");
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
			return AxisAlignedBB.getBoundingBox(0,0,0,0,0,0);
		}
		else
			return super.getCollisionBoundingBoxFromPool(w, x, y, z);
	}
	
	@Override
	public boolean isNormalCube()
	{
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess w, int x, int y, int z)
    {
        if(w.getBlockMetadata(x,y,z) != 1)
        	return super.isNormalCube(w, x, y, z);
        return false;
    }
	
	@Override
	public boolean func_149730_j()
    {
		return false;
    }
	
	@Override 
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
    {
        return isNormalCube(w,x,y,z);
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
		if(w.getBlock(x,y,z) == TardisMod.schemaComponentBlock && w.getBlockMetadata(x, y, z) <= 1)
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
		boolean ser = ServerHelper.isServer();
    	int meta = w.getBlockMetadata(x, y, z);
    	if(meta == 6 && j > 0.5 && ser || meta == 8)
		{
			CoreTileEntity core = Helper.getTardisCore(w);
			if(core != null)
				core.activate(pl, s);
		}
    	if(meta == 2 && ser)
    	{
    		boolean found = false;
    		for(int i = 1;i<10 && found == false;i++)
    		{
    			if(w.getBlock(x, y-i, z) == TardisMod.schemaCoreBlock)
    			{
    				return ((AbstractBlockContainer)TardisMod.schemaCoreBlock).onBlockActivated(w, x, y-i, z, pl, s, t, j, k);
    			}
    		}
    	}
    	if((meta == 3 || meta == 6) && !ser)
    	{
    		ConsoleTileEntity console = Helper.getTardisConsole(w);
			if(console != null)
				return console.activate(pl, x, y, z, t, j, k);
    	}
    	if((meta == 4 || meta == 5) && ser)
    	{
    		CoreTileEntity te = Helper.getTardisCore(w);
    		if(te != null)
    		{
				if((!w.isRemote) && (!te.changeLock(pl,true)))
					te.leaveTardis(pl,false);
    		}
    		return true;
    	}
    	if(meta == 7 && !ser)
    	{
    		EngineTileEntity te = Helper.getTardisEngine(w);
    		if(te != null)
    		{
    			return te.activate(pl, s, y, t, j, k);
    		}
    	}
        return false;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z)
    {
        if(w.getBlockMetadata(x, y, z) == 3)
        	this.setBlockBounds(0, 0.5F, 0, 1, 1, 1);
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
