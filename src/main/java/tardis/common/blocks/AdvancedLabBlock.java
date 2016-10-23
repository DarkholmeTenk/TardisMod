package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.client.renderer.tileents.AdvancedLabRenderer;
import tardis.common.tileents.AdvancedLab;

public class AdvancedLabBlock extends AbstractScrewableBlockContainer
{
	public AdvancedLabBlock()
	{
		super(false, false, TardisMod.modName);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new AdvancedLab();
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return AdvancedLab.class;
	}

	@Override
	public void initData()
	{
		setBlockName("AdvancedLab");
		setHardness(5.0F);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if (entity == null) return;

		int dir = MathHelper.floor_double(((entity.rotationYaw * 4.0F) / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 3);
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractBlockRenderer getRenderer()
	{
		return new AdvancedLabRenderer();
	}

	@Override
	public boolean useRendererForItem()
	{
		return true;
	}

}
