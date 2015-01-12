package tardis.common.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractBlock extends Block
{
	private IIcon iconBuffer = null;
	private String unlocalizedFragment = "";
	private String[] subNames = null;
	private IIcon[] subIcons = null;
	protected static IIcon blankIcon = null;
	private boolean renderIcon;

	public AbstractBlock(Material blockMaterial)
	{
		super(blockMaterial);
		setResistance(6000000.0F);
		setHardness(-1.0f);
		setCreativeTab(TardisMod.tab);
		initData();
		renderIcon = true;
		if(subNames == null)
			setIconArray(1);
	}
	
	public AbstractBlock(boolean render)
	{
		this();
		renderIcon = render;
	}
	
	public AbstractBlock()
	{
		this(Material.iron);
	}
	
	public ItemStack getIS(int am,int dam)
	{
		return new ItemStack(this,am,dam);
	}
	
	public void setSubNames(String... subnames)
	{
		if(subnames.length == 0)
			subNames = null;
		subNames = subnames;
		setIconArray(subNames.length);
	}
	
	private void setIconArray(int length)
	{
		setIconArray(length,getIconSuffixes());
	}
	
	public void setIconArray(int names, int suffixes)
	{
		subIcons = new IIcon[names * suffixes];
	}
	
	public String[] getIconSuffix(int damage)
	{
		return getIconSuffix();
	}
	
	public String[] getIconSuffix()
	{
		return null;
	}
	
	public int getIconSuffixes()
	{
		String[] suffixes = getIconSuffix();
		if(suffixes == null)
			return 1;
		return suffixes.length;
	}
	
	@Override
	public int damageDropped(int damage)
	{
		if(subNames != null)
			return Helper.clamp(damage, 0, subNames.length - 1);
		return 0;
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
	public Block setBlockName(String par1Str)
	{
		unlocalizedFragment = par1Str;
		return super.setBlockName(par1Str);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "tile.TardisMod." + unlocalizedFragment;
	}
	
	public String getUnlocalizedNameForIcon()
	{
		return unlocalizedFragment;
	}
	
	@Override
	public void getSubBlocks(Item itemID,CreativeTabs tab,List itemList)
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
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
		return false;
    }
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return false;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		if(blankIcon == null)
			blankIcon = register.registerIcon("tardismod:blank");
		if(!renderIcon)
			return;
		if(subIcons != null)
		{
			int suffixCount = getIconSuffixes();
			if(subNames != null)
			{
				for(int i = 0; i < subNames.length; i++)
				{
					String[] suffixes = getIconSuffix(i);
					if(suffixes == null)
					{
						subIcons[i*suffixCount] = register.registerIcon("tardismod:" + getUnlocalizedNameForIcon() + "." + subNames[i]);
					}
					else
					{
						for(int j = 0;j<suffixes.length;j++)
						{
							String iconToReg = "tardismod:" + getUnlocalizedNameForIcon() + "." + subNames[i] +"."+ suffixes[j];
							TardisOutput.print("AB", "Registering " + iconToReg + " in slot " + ((i*suffixCount)+j));
							subIcons[(i*suffixCount) + j] = register.registerIcon(iconToReg);
						}
					}
				}
			}
			else
			{
				String[] suffixes = getIconSuffix();
				if(suffixes == null)
				{
					subIcons[0] = register.registerIcon("tardismod:" + getUnlocalizedNameForIcon());
				}
				else
				{
					for(int j = 0;j<suffixes.length;j++)
					{
						String iconToReg = "tardismod:" + getUnlocalizedNameForIcon() + "."+ suffixes[j];
						subIcons[j] = register.registerIcon(iconToReg);
					}
				}
			}
		}
		else
		{
			iconBuffer = register.registerIcon("tardismod:" + getUnlocalizedNameForIcon());
		}
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return renderIcon;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int s)
    {
		if(!renderIcon)
			return false;
        int mX = x;
        int mY = y;
        int mZ = z;
        switch(s)
		{
			case 0: y++;break;
			case 1: y--;break;
			case 2: z++;break;
			case 3: z--;break;
			case 4: x++;break;
			case 5: x--;break;
		}
        return shouldSideBeRendered(w, s, x,y,z, mX, mY, mZ);
    }
	
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x,int y, int z, int ox, int oy, int oz)
	{
		return super.shouldSideBeRendered(w, ox, oy, oz, s);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
    {
		if(!renderIcon)
			return blankIcon;
		int suffixCount = getIconSuffixes();
		if(subIcons != null)
		{
			String[] suffixes = getIconSuffix(metadata);
			if(suffixes == null)
			{
				if(metadata >= 0 && metadata < subIcons.length)
					return subIcons[metadata*suffixCount];
				return subIcons[0];
			}
			else
			{
				int metaBase = metadata * suffixCount;
				int metaAdd  = 0;
				for(int j = 0;j<suffixes.length;j++)
				{
					if(suffixes[j].contains("top") && side == 1)
						return subIcons[metaBase + j];
					else if(suffixes[j].contains("bottom") && side == 0)
						metaAdd = j;
					else if(suffixes[j].contains("side") && side > 1)
						metaAdd = j;
				}
				return subIcons[metaBase + metaAdd];
			}
		}
		else
		{
			return iconBuffer;
		}
    }
	
	@Override
	public int getMobilityFlag()
	{
		return 2;
	}
	
	public abstract void initData();
	
	public abstract void initRecipes();

}
