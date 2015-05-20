package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.api.TardisPermission;
import tardis.common.core.Helper;
import tardis.common.dimension.TardisDataStore;
import tardis.common.tileents.CoreTileEntity;

public class ColorableFloorBlock extends AbstractBlock implements IColorableBlock
{

	public ColorableFloorBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableFloorItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableFloor");
	}

	@Override
	public void initRecipes()
	{
	}

	@Override
	protected boolean colorBlock(World w, int x, int y, int z, EntityPlayer pl, ItemStack is, int color, int depth)
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
		return super.colorBlock(w, x, y, z, pl, is, color, depth);
	}



}
