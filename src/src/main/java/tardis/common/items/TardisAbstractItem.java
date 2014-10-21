package tardis.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public abstract class TardisAbstractItem extends Item
{
	private Icon iconBuffer;
	private String unlocalizedFragment;

	public TardisAbstractItem(int par1)
	{
		super(par1);
		setCreativeTab(TardisMod.tab);
	}
	
	public abstract void initRecipes();
	
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
    	TardisOutput.print("TAI", "Registering icon " + unlocalizedFragment);
    	iconBuffer = ir.registerIcon("tardismod:" + unlocalizedFragment);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage(int damage)
    {
    	return iconBuffer;
    }
    
    public void addInfo(ItemStack is, EntityPlayer player, List infoList){}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List infoList, boolean par4)
	{
		super.addInformation(is, player, infoList, par4);
		addInfo(is,player,infoList);
	}

}
