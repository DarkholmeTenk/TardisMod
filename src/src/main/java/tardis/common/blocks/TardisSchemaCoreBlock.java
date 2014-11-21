package tardis.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.tileents.TardisSchemaCoreTileEntity;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TardisSchemaCoreBlock extends TardisAbstractBlockContainer
{
	private final boolean visible;
	private Icon blankIcon;
	
	public TardisSchemaCoreBlock(int par1)
	{
		super(par1);
		visible = TardisMod.modConfig.getBoolean("Visible schematic boundaries", false);
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TardisSchemaCoreTileEntity();
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("SchemaCore");
		
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
	public void registerIcons(IconRegister register)
	{
		super.registerIcons(register);
		blankIcon = register.registerIcon("tardismod:blank");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int s, int d)
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
}
