package tardis.items;

import tardis.TardisMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TardisAbstractItem extends Item
{
	private String unlocalizedFragment;

	public TardisAbstractItem(int par1)
	{
		super(par1);
		setCreativeTab(TardisMod.tab);
	}
	
	@Override
	public Item setUnlocalizedName(String unlocal)
	{
		Item orig = super.setUnlocalizedName(unlocal);
		unlocalizedFragment = unlocal;
		return orig;
	}
	
	@Override
	public String getUnlocalizedName()
    {
        return "item.TardisMod." + unlocalizedFragment;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return "item.TardisMod." + unlocalizedFragment;
    }

}
