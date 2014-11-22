package tardis.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tardis.TardisMod;
import tardis.common.core.TardisOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TardisAbstractItem extends Item
{
	private IIcon iconBuffer;
	private String unlocalizedFragment;
	
	private String[] subNames = null;
	private IIcon[] subIcons = null;

	public TardisAbstractItem()
	{
		setCreativeTab(TardisMod.tab);
	}
	
	public abstract void initRecipes();
	
	public void setSubNames(String... _subNames)
	{
		subNames = _subNames;
		if(subNames != null && subNames.length > 1)
			setHasSubtypes(true);
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
    public String getUnlocalizedName(ItemStack is)
    {
    	if(subNames == null)
    		return getUnlocalizedName();
    	else
    	{
    		int damage = is.getItemDamage();
    		if(damage >= 0 && damage < subNames.length)
    			return getUnlocalizedName() + "." + subNames[damage];
    		else
    			return getUnlocalizedName() + ".Malformed";
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir)
    {
    	TardisOutput.print("TAI", "Registering icon " + unlocalizedFragment);
    	iconBuffer = ir.registerIcon("tardismod:" + unlocalizedFragment);
    	if(subNames != null)
    	{
    		subIcons = new IIcon[subNames.length];
    		for(int i = 0; i< subNames.length; i++)
    			subIcons[i] = ir.registerIcon("tardismod:"+unlocalizedFragment+"."+subNames[i]);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage)
    {
    	if(subNames == null)
    		return iconBuffer;
    	else if(damage >= 0 && damage < subNames.length)
    		return subIcons[damage];
    	return iconBuffer;
    }
    
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list)
    {
    	if(subNames == null)
    		list.add(new ItemStack(par1, 1, 0));
    	else
    	{
    		for(int i = 0;i<subNames.length;i++)
    			list.add(new ItemStack(par1,1,i));
    	}
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
