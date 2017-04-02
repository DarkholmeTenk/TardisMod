package tardis.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IBlockIteratorCondition;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;

import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.common.core.helpers.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.CoreTileEntity;

public class ColorableOpenRoundelBlock extends AbstractScrewableBlockContainer implements IColorableBlock
{

	public ColorableOpenRoundelBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableOpenRoundel");
		setLightLevel(1);
	}

	@Override
	public void initRecipes()
	{
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new ComponentTileEntity();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return ComponentTileEntity.class;
	}

	@Override
	protected boolean colorBlock(World w, int x, int y, int z, EntityPlayer pl, IBlockIteratorCondition cond, ItemStack is, int color, int depth)
	{
		if(Helper.isTardisWorld(w))
		{
			TardisDataStore ds = Helper.getDataStore(w);
			if((ds != null) && !ds.hasPermission(pl, TardisPermission.RECOLOUR))
			{
				if(ServerHelper.isServer())
					ServerHelper.sendString(pl, CoreTileEntity.cannotModifyRecolour);
				return false;
			}
		}
		return super.colorBlock(w, x, y, z, pl, cond, is, color, depth);
	}

	@Override
	public IBlockIteratorCondition getColoringIterator(SimpleCoordStore coord)
	{
		return ColorableBlock.getIterator(coord);
	}

}
