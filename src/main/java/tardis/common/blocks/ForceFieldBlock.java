package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tardis.TardisMod;
import tardis.common.tileents.extensions.CraftingComponentType;
import cpw.mods.fml.common.registry.GameRegistry;

public class ForceFieldBlock extends AbstractBlock
{
	public ForceFieldBlock(boolean visible)
	{
		super(visible,TardisMod.modName);
		setCreativeTab(TardisMod.cTab);
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,9,1), false, "gcg","ggg","gcg",
				'g', "blockGlass",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this,9,1), false, "ggg","cgc","ggg",
				'g', "blockGlass",
				'c', CraftingComponentType.CHRONOSTEEL.getIS(1)));
	}

	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB aabb, List list, Entity ent)
	{
		if(ent instanceof EntityPlayer)
		{
			if(w.getBlock(x, y+1, z) != this)
				if(((EntityPlayer)ent).isSneaking() || (((EntityPlayer)ent).posY < (y+1)) || (w.getBlock(x, y-1, z)==this))
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

	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		if ((w.getBlock(mX, mY, mZ) == this) && (w.getBlockMetadata(mX, mY, mZ) == w.getBlockMetadata(x, y, z)))
			return false;
		return super.shouldSideBeRendered(w, s, x, y, z, mX, mY, mZ);
	}
}
