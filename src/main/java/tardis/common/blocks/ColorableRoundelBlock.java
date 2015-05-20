package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;
import io.darkcraft.darkcore.mod.helpers.ServerHelper;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.CoreTileEntity;

public class ColorableRoundelBlock extends AbstractBlock implements IColorableBlock
{

	public ColorableRoundelBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return ColorableRoundelItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("ColorableRoundel");
		setLightLevel(1);
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
			CoreTileEntity core = Helper.getTardisCore(w);
			if((core != null) && !core.canModify(pl))
			{
				if(ServerHelper.isServer())
					ServerHelper.sendString(pl, CoreTileEntity.cannotModifyMessage);
				return false;
			}
		}
		return super.colorBlock(w, x, y, z, pl, is, color, depth);
	}

	/*
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new DummyRoundelTE();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return DummyRoundelTE.class;
	}
	*/
}
