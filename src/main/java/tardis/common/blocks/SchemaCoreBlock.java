package tardis.common.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.tileents.SchemaCoreTileEntity;

public class SchemaCoreBlock extends AbstractBlockContainer
{
	private final boolean visible;
	private IIcon blankIcon;

	public SchemaCoreBlock(boolean vis)
	{
		super(TardisMod.modName);
		visible = vis;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new SchemaCoreTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("SchemaCore");

	}

	@Override
	public void initRecipes()
	{
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isOpaqueCube()
	{
		return visible;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		super.registerBlockIcons(register);
		blankIcon = register.registerIcon("tardismod:blank");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int s, int d)
	{
		if(visible)
			return super.getIcon(s, d);
		return blankIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int s)
	{
		return visible;
	}

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return SchemaCoreTileEntity.class;
	}
}
