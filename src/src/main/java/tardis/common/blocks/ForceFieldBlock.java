package tardis.common.blocks;

import java.util.List;

import tardis.common.tileents.extensions.CraftingComponentType;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ForceFieldBlock extends AbstractBlock
{
	public ForceFieldBlock()
	{
		super(false);
	}

	@Override
	public void initData()
	{
		setBlockName("ForceField");
		setSubNames("Normal","Craftable");
	}

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,9,1), false, "ggg","gcg","ggg",
				'g', "blockGlass",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}

	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB aabb, List list, Entity ent)
	{
		if(ent instanceof EntityPlayer)
		{
			if(((EntityPlayer)ent).isSneaking() || ((EntityPlayer)ent).posY < y+1)
				return;
		}
		super.addCollisionBoxesToList(w, x, y, z, aabb, list, ent);
	}
	
	@Override 
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess w, int x, int y, int z, int s)
	{
		return false;
	}
	
	@Override
	public float getBlockHardness(World w, int x, int y, int z)
	{
		int meta = w.getBlockMetadata(x, y, z);
		if(meta == 1)
			return 6;
		return super.getBlockHardness(w, x, y, z);
	}
}
