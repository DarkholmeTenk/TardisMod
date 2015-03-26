package tardis.common.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import tardis.TardisMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {

	public CreativeTab()
	{
		super("TardisModTab");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return TardisMod.screwItem;
	}

}
