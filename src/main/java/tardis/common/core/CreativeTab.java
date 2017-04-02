package tardis.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.common.TMRegistry;

public class CreativeTab extends CreativeTabs {

	public CreativeTab(String s)
	{
		super(s);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return TMRegistry.screwItem;
	}

}
