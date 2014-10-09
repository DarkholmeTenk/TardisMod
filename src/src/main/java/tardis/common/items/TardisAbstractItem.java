package tardis.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class TardisAbstractItem extends Item
{
	private Icon iconBuffer;
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister ir)
    {
    	ir.registerIcon("tardismod:" + unlocalizedFragment);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int damage)
    {
    	return iconBuffer;
    }

}
