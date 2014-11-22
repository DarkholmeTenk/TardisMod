package tardis.common.core;

import tardis.TardisMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TardisCreativeTab extends CreativeTabs {

	public TardisCreativeTab()
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
