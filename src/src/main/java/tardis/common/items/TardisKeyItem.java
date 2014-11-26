package tardis.common.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;

import tardis.TardisMod;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisCoreTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TardisKeyItem extends TardisAbstractItem
{

	public TardisKeyItem()
	{
		super();
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
			setOwnerName(is,Helper.getUsername(player));
		
		if(Helper.isServer() && !TardisMod.plReg.hasTardis(getOwnerName(is)) && Helper.getUsername(player).equals(getOwnerName(is)))
		{
			Helper.summonNewTardis(player);
			player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
		}
		else if(Helper.isServer() && Helper.getUsername(player).equals(getOwnerName(is)))
		{
			TardisCoreTileEntity te = TardisMod.plReg.getCore(player);
			if(te != null)
			{
				if(!te.hasValidExterior() && !te.inFlight())
				{
					Helper.summonOldTardis(player);
					player.addChatMessage(new ChatComponentText("[TARDIS KEY]The key feels warm"));
				}
			}
		}
		return is;
    }

	@Override
	public void initRecipes()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TardisMod.keyItem,1),true, " i "," i "," ii",
				'i', Items.iron_ingot));
	}

}
