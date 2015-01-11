package tardis.common.blocks;

import tardis.api.IActivatable;
import tardis.api.IScrewable;
import tardis.api.IWatching;
import tardis.common.core.Helper;
import tardis.common.items.SonicScrewdriverItem;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class AbstractBlockContainer extends AbstractBlock implements ITileEntityProvider
{
	private boolean dropWithData = false;
	
	public AbstractBlockContainer()
	{
		super();
		this.isBlockContainer = true;
	}
	
	public AbstractBlockContainer(boolean b)
	{
		super(b);
		this.isBlockContainer = true;
	}
	
	public AbstractBlockContainer(boolean vis, boolean _dropWithData)
	{
		super(vis);
		this.isBlockContainer = true;
		dropWithData = _dropWithData;
	}

	@Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    
    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        par1World.removeTileEntity(par2, par3, par4);
    }
    
    @Override
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
    
    @Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float i, float j, float k)
    {
    	boolean answer = false;
    	TileEntity te = w.getTileEntity(x, y, z);
    	if(te != null && te instanceof IScrewable)
    	{
			ItemStack held = pl.getHeldItem();
			if(held != null)
			{
				Item heldBase = held.getItem();
				if(heldBase instanceof SonicScrewdriverItem && !w.isRemote)
					answer = answer || ((IScrewable)te).screw(SonicScrewdriverItem.getMode(held), pl);
				else if(heldBase instanceof SonicScrewdriverItem && w.isRemote)
					answer = true;
			}
			if(answer)
				Helper.playSound(Helper.getWorldID(w), x,y,z, "tardismod:sonic", 0.5F);
    	}
    	if(te != null && te instanceof IActivatable)
    	{
    		if(w.isRemote)
    			answer = true;
    		else
    			answer = answer || ((IActivatable)te).activate(pl,s);
    	}
        return answer;
    }
    
    @Override
    protected void dropBlockAsItem(World w, int x, int y, int z, ItemStack is)
    {
        if (!w.isRemote && w.getGameRules().getGameRuleBooleanValue("doTileDrops") && !w.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
        	if(dropWithData)
        	{
        		TileEntity te = w.getTileEntity(x, y, z);
        		if(te != null)
        		{
        			NBTTagCompound nbt;
        			if(is.stackTagCompound != null)
        				nbt = is.stackTagCompound;
        			else
        				nbt = new NBTTagCompound();
        			te.writeToNBT(nbt);
        			is.stackTagCompound = nbt;
        		}
        	}
            if (captureDrops.get())
            {
                capturedDrops.get().add(is);
                return;
            }
            float f = 0.7F;
            double d0 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(w, (double)x + d0, (double)y + d1, (double)z + d2, is);
            entityitem.delayBeforeCanPickup = 0;
            w.spawnEntityInWorld(entityitem);
        }
    }
    
    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block neighbourBlockID)
	{
		TileEntity te = w.getTileEntity(x, y, z);
		if(te != null && te instanceof IWatching)
		{
			((IWatching)te).neighbourUpdated(neighbourBlockID);
		}
	}

}
