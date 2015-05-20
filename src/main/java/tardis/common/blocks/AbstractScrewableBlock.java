package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.api.IScrewable;
import tardis.common.items.SonicScrewdriverItem;

public abstract class AbstractScrewableBlock extends AbstractBlockContainer
{
	public AbstractScrewableBlock(String sm)
	{
		super(sm);
	}

	public AbstractScrewableBlock(boolean render, String sm)
	{
		super(render, sm);
	}

	public AbstractScrewableBlock(boolean visible, boolean _dropWithData, String sm)
	{
		super(visible, _dropWithData, sm);
	}

	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float i, float j, float k)
	{
		boolean cont = false;
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof IScrewable)
		{
			if(SonicScrewdriverItem.isPlayerHoldingScrewdriver(pl))
				cont = ((IScrewable)te).screw(SonicScrewdriverItem.getMode(pl.getHeldItem()), pl);
		}
		if(!cont)
			return super.onBlockActivated(w,x,y,z,pl,s,i,j,k);
		return true;
	}
}
