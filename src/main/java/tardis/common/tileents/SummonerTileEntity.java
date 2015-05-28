package tardis.common.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractTileEntity;
import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IActivatable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tardis.TardisMod;
import tardis.api.ITDismantleable;
import tardis.common.core.Helper;

public class SummonerTileEntity extends AbstractTileEntity implements IActivatable, ITDismantleable
{

	@Override
	public boolean activate(EntityPlayer ent, int side)
	{
		if(ServerHelper.isClient()) return true;
		if(!Helper.isTardisWorld(worldObj))
		{
			if(Helper.summonOldTardis(ent))
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	public boolean canDismantle(SimpleCoordStore scs, EntityPlayer pl)
	{
		return true;
	}

	@Override
	public List<ItemStack> dismantle(SimpleCoordStore scs, EntityPlayer pl)
	{
		List<ItemStack> is = new ArrayList<ItemStack>();
		is.add(new ItemStack(TardisMod.summonerBlock,1));
		return is;
	}

}
