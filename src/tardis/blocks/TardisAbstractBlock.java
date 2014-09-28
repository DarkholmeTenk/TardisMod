package tardis.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import tardis.TardisMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public abstract class TardisAbstractBlock extends Block
{
	Icon iconBuffer = null;
	String unlocalizedFragment = "";
	private String[] subNames = null;

	public TardisAbstractBlock(int blockID, Material blockMaterial)
	{
		super(blockID, blockMaterial);
		setHardness(-1.0f);
		setCreativeTab(TardisMod.tab);
		initData();
		initRecipes();
	}
	
	public TardisAbstractBlock(int blockID)
	{
		this(blockID, Material.iron);
	}
	
	public void setSubNames(String... subnames)
	{
		if(subnames.length == 0)
			subNames = null;
		subNames = subnames;
	}
	
	public int getNumSubNames()
	{
		if(subNames == null)
			return 0;
		return subNames.length;
	}
	
	public String getSubName(int num)
	{
		if(subNames == null)
			return null;
		if(num >= 0 && num < subNames.length)
			return subNames[num];
		return "Malformed";
	}
	
	@Override
	public Block setUnlocalizedName(String par1Str)
	{
		unlocalizedFragment = par1Str;
		return super.setUnlocalizedName(par1Str);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "tile.TardisMod." + unlocalizedFragment;
	}
	
	@Override
	public void getSubBlocks(int itemID,CreativeTabs tab,List itemList)
	{
		int numItems = Math.max(1,getNumSubNames());
		for(int i = 0; i<numItems;i++)
		{
			itemList.add(new ItemStack(itemID,1,i));
		}
	}
	
	public String getUnlocalizedName(int damage)
	{
		int numSubnames = getNumSubNames();
		if(numSubnames == 0)
			return "tile.TardisMod." + unlocalizedFragment;
		else
		{
			return "tile.TardisMod." + unlocalizedFragment + "." + getSubName(damage);
		}
	}
	
	@Override
	public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity)
	{
		return false;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register)
	{
		iconBuffer = register.registerIcon("tardismod:" + unlocalizedFragment);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int par1, int par2)
    {
        return iconBuffer;
    }
	
	public abstract void initData();
	
	public abstract void initRecipes();

}
