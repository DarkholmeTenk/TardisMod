package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tardis.TardisMod;

public class SlabBlock extends AbstractBlock
{
	private final static String[] names		= {"Console","Corridor","Walkway","Glass"};
	private final static String[] suffixes	= {"topbottom", "side"};

	public SlabBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("Slab");
		setSubNames(names);
		setIconArray(names.length,suffixes.length);
		setLightLevel(TardisMod.lightBlocks ? 1 : 0);
	}

	@Override
	public String getSubName(int num)
	{
		return super.getSubName(num/2);
	}

	@Override
	public IIcon getIcon(int s, int d)
	{
		return super.getIcon(s, d/2);
	}

	@Override
	public String[] getIconSuffix()
	{
		return suffixes;
	}

	@Override
	public void getSubBlocks(Item itemID,CreativeTabs tab,List itemList)
	{
		itemList.add(new ItemStack(itemID,1,0));
		itemList.add(new ItemStack(itemID,1,2));
		itemList.add(new ItemStack(itemID,1,4));
		itemList.add(new ItemStack(itemID,1,6));
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

	@Override
	public int damageDropped(int par1)
    {
        return par1 & 14;
    }

	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z)
	{
		boolean flag = (w.getBlockMetadata(x, y, z) % 2) != 0;

		if (flag)
		{
			setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	@Override
	public int onBlockPlaced(World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
		int top = metadata + 1;
		int bot = metadata;
        return side == 0? top : (side == 1 ? bot :(hitY > 0.5F ? top : bot));
    }

	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int ox, int oy, int oz)
	{
		if((w.getBlock(x, y, z) == this) && (w.getBlock(ox, oy, oz) == this))
		{
			if(oy == y)
			{
				if(w.getBlockMetadata(x, y, z) == w.getBlockMetadata(ox, oy, oz))
					return false;
			}
			else
			{
				int baseA = w.getBlockMetadata(x, y, z) & 14;
				int baseB = w.getBlockMetadata(ox, oy, oz) & 14;
				if(baseA == baseB)
				{
					if(y > oy)
					{
						if(w.getBlockMetadata(x, y, z) == (w.getBlockMetadata(ox, oy, oz) - 1))
							return false;
					}
					else if(w.getBlockMetadata(x, y, z) == (w.getBlockMetadata(ox, oy, oz) + 1))
							return false;
				}
			}
		}
		return super.shouldSideBeRendered(w, s,x,y,z,ox, oy, oz);
	}

	@Override
	public boolean renderAsNormalBlock()
    {
		return false;
    }

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return SlabItemBlock.class;
	}

}
