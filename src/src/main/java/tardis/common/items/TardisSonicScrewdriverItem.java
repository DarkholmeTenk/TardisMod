package tardis.common.items;

import java.util.ArrayList;
import java.util.List;

import tardis.TardisMod;
import tardis.api.TardisScrewdriverMode;
import tardis.common.core.Helper;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
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
	
	public String getSchema(ItemStack is)
	{
		if(is == null)
			return "";
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag != null)
			return isTag.getString("schemaName");
		return "";
	}
	
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		if(is != null)
		{
			TardisScrewdriverMode mode = getMode(is);
			infoList.add("Mode: " + mode.toString());
			if(mode.equals(TardisScrewdriverMode.Schematic))
			{
				String schemaName = getSchema(is);
				if(schemaName == null || schemaName.equals(""))
					infoList.add("Schematic: --None--");
				else
					infoList.add("Schematic: " + schemaName);
			}
		}
	}
	
	public void notifyMode(ItemStack is, EntityPlayer player)
	{
		getMode(is);
		ArrayList<Object> list = new ArrayList<Object>();
		addInfo(is,player,list);
		for(Object o: list)
		{
			if(o instanceof String)
			{
				ChatMessageComponent c = new ChatMessageComponent();
				c.setColor(EnumChatFormatting.AQUA);
				c.addText("[Sonic Screwdriver]" + (String)o);
				player.sendChatToPlayer(c);
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		TardisScrewdriverMode mode = getMode(is);
		if(!world.isRemote && player.isSneaking())
		{
			int newValue = (mode.ordinal() + 1) % TardisScrewdriverMode.values().length;
			is.stackTagCompound.setInteger("screwdriverMode", newValue);
			notifyMode(is,player);
		}
		else if(Helper.isServer())
		{
			if(mode.equals(TardisScrewdriverMode.Locate))
			{
				boolean found = false;
				Integer dim = TardisMod.plReg.getDimension(player);
				if(dim != null)
				{
					if(dim == player.worldObj.provider.dimensionId)
					{
						found = true;
						player.addChatMessage("[Sonic Screwdriver]You are in your TARDIS");
					}
					else
					{
						TardisCoreTileEntity core = Helper.getTardisCore(dim);
						if(core != null)
						{
							TardisTileEntity ext = core.getExterior();
							if(ext != null)
							{
								found = true;
								if(ext.worldObj.provider.dimensionId != player.worldObj.provider.dimensionId)
									player.addChatMessage("[Sonic Screwdriver]Your TARDIS does not appear to be in this dimension");
								else
									player.addChatMessage("[Sonic Screwdriver]Your TARDIS is at ["+ext.xCoord+","+ext.yCoord+","+ext.zCoord+"]");
							}
						}
					}
				}
				if(!found)
					player.addChatMessage("[Sonic Screwdriver]Your TARDIS could not be located");
			}
		}
        return is;
    }

}
