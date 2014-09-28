package tardis.blocks;

import tardis.api.IScrewable;
import tardis.items.TardisSonicScrewdriverItem;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TardisAbstractBlockContainer extends TardisAbstractBlock implements ITileEntityProvider
{

	public TardisAbstractBlockContainer(int par1, Material par2Material)
	{
		super(par1, par2Material);
		this.isBlockContainer = true;
	}
	
	public TardisAbstractBlockContainer(int par1)
	{
		super(par1);
		this.isBlockContainer = true;
	}
	
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        par1World.removeBlockTileEntity(par2, par3, par4);
    }
    
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
    
    @Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float i, float j, float k)
    {
    	TileEntity te = w.getBlockTileEntity(x, y, z);
    	if(te != null && te instanceof IScrewable)
    	{
			ItemStack held = pl.getHeldItem();
			if(held != null)
			{
				Item heldBase = held.getItem();
				if(heldBase instanceof TardisSonicScrewdriverItem)
					return ((IScrewable)te).screw(((TardisSonicScrewdriverItem)heldBase).getMode(held), pl);
			}
    	}
        return false;
    }

}
