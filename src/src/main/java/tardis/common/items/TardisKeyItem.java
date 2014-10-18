package tardis.common.items;

import java.util.List;

import tardis.TardisMod;
import tardis.common.core.Helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TardisKeyItem extends TardisAbstractItem
{

	public TardisKeyItem(int par1)
	{
		super(par1);
		setUnlocalizedName("TardisKey");
		setMaxStackSize(1);
	}
	
	public static String getOwnerName(ItemStack is)
	{
		if(is != null)
		{
			if(is.getItem() instanceof TardisKeyItem)
			{
				NBTTagCompound data = is.stackTagCompound;
				if(data != null)
					return data.getString("keyOwner");
			}
		}
		return null;
	}
	
	public static void setOwnerName(ItemStack is, String ownerName)
	{
		if(is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setString("keyOwner", ownerName);
	}
	
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is != null)
		{
			String owner = getOwnerName(is);
			if(owner != null)
				infoList.add("Owner: " + owner);
			else
			{
				infoList.add("No owner assigned");
				infoList.add("Right click to set");
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		if(is!= null && getOwnerName(is) == null)
			setOwnerName(is,player.username);
		
		if(Helper.isServer() && !TardisMod.plReg.hasTardis(player.username))
		{
			Helper.summonTardis(player);
		}
		return is;
    }

}
