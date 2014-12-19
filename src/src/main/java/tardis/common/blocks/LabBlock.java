package tardis.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.tileents.LabTileEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class LabBlock extends TardisAbstractBlockContainer
{

	public LabBlock()
	{
		super(false);
	}
	
	@Override
	public TileEntity createNewTileEntity(World w, int m)
	{
		return new LabTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("Lab");
		setHardness(5.0F);
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TardisMod.labBlock,1,0),true, "igi","ioi","iri",
				'i', Items.iron_ingot,
				'g', Blocks.glass,
				'o', Items.gold_ingot,
				'r', Items.redstone));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
	{
	   return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
    {
		return false;
    }

	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}

}
