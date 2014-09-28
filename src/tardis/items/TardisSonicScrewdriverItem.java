package tardis.items;

import java.util.List;

import tardis.api.TardisScrewdriverMode;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TardisSonicScrewdriverItem extends TardisAbstractItem
{

	public TardisSonicScrewdriverItem(int par1)
	{
		super(par1);
		setUnlocalizedName("SonicScrewdriver");
		setMaxDamage(64);
		setMaxStackSize(1);
	}
	
	public TardisScrewdriverMode getMode(ItemStack is)
	{
		if(is == null)
			return TardisScrewdriverMode.Dismantle;
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag == null)
		{
			is.stackTagCompound = isTag = new NBTTagCompound();
			isTag.setInteger("screwdriverMode", 0);
		}
		
		TardisScrewdriverMode mode = TardisScrewdriverMode.values()[isTag.getInteger("screwdriverMode") % TardisScrewdriverMode.values().length];
		return mode;
	}
	
	public void addInformation(ItemStack is, EntityPlayer player, List infoList, boolean par4)
	{
		if(is != null)
		{
			TardisScrewdriverMode mode = getMode(is);
			infoList.add("Screwdriver mode: " + mode.toString());
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		if(player.isSneaking())
		{
			
			TardisScrewdriverMode mode = getMode(is);
			int newValue = (mode.ordinal() + 1) % TardisScrewdriverMode.values().length;
			is.stackTagCompound.setInteger("screwdriverMode", newValue);
			getMode(is);
		}
        return is;
    }

}
