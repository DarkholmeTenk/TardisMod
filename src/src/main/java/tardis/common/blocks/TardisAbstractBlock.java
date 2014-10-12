package tardis.common.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TardisAbstractBlock extends Block
{
	private Icon iconBuffer = null;
	private String unlocalizedFragment = "";
	private String[] subNames = null;
	private Icon[] subIcons = null;

	public TardisAbstractBlock(int blockID, Material blockMaterial)
	{
		super(blockID, blockMaterial);
		setResistance(6000000.0F);
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
		setIconArray(subNames.length);
	}
	
	private void setIconArray(int length)
	{
		String[] suffixes = getIconSuffix();
		if(suffixes == null)
			subIcons = new Icon[length];
		else
			subIcons = new Icon[length * suffixes.length];
	}
	
	public void setIconArray(int names, int suffixes)
	{
		subIcons = new Icon[names * suffixes];
	}
	
	public String[] getIconSuffix()
	{
		return null;
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
	public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
    {
		return false;
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
		if(subIcons != null)
		{
			String[] suffixes = getIconSuffix();
			for(int i = 0; i < subNames.length; i++)
			{
				if(suffixes == null)
				{
					TardisOutput.print("TAB", "Registering icon " + i + ":" + "tardismod:" + unlocalizedFragment + "." + subNames[i]);
					subIcons[i] = register.registerIcon("tardismod:" + unlocalizedFragment + "." + subNames[i]);
				}
				else
				{
					for(int j = 0;j<suffixes.length;j++)
					{
						String iconToReg = "tardismod:" + unlocalizedFragment + "." + subNames[i] +"."+ suffixes[j];
						TardisOutput.print("TAB", "Registering icon " + i + ":" + iconToReg);
						subIcons[(i*suffixes.length) + j] = register.registerIcon(iconToReg);
					}
				}
			}
		}
		else
		{
			TardisOutput.print("TAB", "Registering icon:" + "tardismod:" + unlocalizedFragment);
			iconBuffer = register.registerIcon("tardismod:" + unlocalizedFragment);
		}
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int s)
    {
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
	public Icon getIcon(int side, int metadata)
    {
		if(subIcons != null)
		{
			String[] suffixes = getIconSuffix();
			if(suffixes == null)
			{
				if(metadata >= 0 && metadata < subIcons.length)
					return subIcons[metadata];
				return subIcons[0];
			}
			else
			{
				int metaBase = metadata * suffixes.length;
				int metaAdd  = 0;
				for(int j = 0;j<suffixes.length;j++)
				{
					if(suffixes[j].contains("top") && side == 0)
						metaAdd = j;
					else if(suffixes[j].contains("bottom") && side == 1)
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
	
	public abstract void initData();
	
	public abstract void initRecipes();

}
