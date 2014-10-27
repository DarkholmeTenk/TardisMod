package tardis.common.items;

import java.util.ArrayList;
import java.util.List;

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
	
	public static TardisScrewdriverMode getMode(int i)
	{
		TardisScrewdriverMode[] modes = TardisScrewdriverMode.values();
		if(i < 0 || i >= modes.length)
			return modes[0];
		return modes[i];
	}
	
	public static TardisScrewdriverMode getMode(ItemStack is)
	{
		if(is == null)
			return TardisScrewdriverMode.Dismantle;
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag == null)
		{
			is.stackTagCompound = isTag = new NBTTagCompound();
			isTag.setInteger("screwdriverMode", 0);
		}
		
		return getMode(isTag.getInteger("screwdriverMode"));
	}
	
	public static String getSchema(ItemStack is)
	{
		if(is == null)
			return "";
		
		NBTTagCompound isTag = is.stackTagCompound;
		if(isTag != null)
			return isTag.getString("schemaName");
		return "";
	}
	
	public static TardisCoreTileEntity getLinkedCore(ItemStack is)
	{
		if(is.stackTagCompound != null)
		{
			int dim = is.stackTagCompound.getInteger("linkedTardis");
			return Helper.getTardisCore(dim);
		}
		return null;
	}
	
	public static double[] getColors(TardisScrewdriverMode m)
	{
		double[] colors = new double[3];
		colors[0] = 0;
		colors[1] = 0;
		colors[2] = 1;
		if(m != null)
			return m.c;
		return colors;
	}
	
	public static double[] getColors(ItemStack is)
	{
		TardisScrewdriverMode mode = getMode(is);
		return getColors(mode);
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
			boolean valid = false;
			int newValue = mode.ordinal();
			while(!valid)
			{
				newValue = (newValue + 1) % TardisScrewdriverMode.values().length;
				TardisScrewdriverMode m = getMode(newValue);
				if(m.requiredFunction != null)
				{
					TardisCoreTileEntity te = getLinkedCore(is);
					if(te != null && te.hasFunction(m.requiredFunction))
						valid = true;
				}
				else
				{
					valid = true;
				}
			}
			is.stackTagCompound.setInteger("screwdriverMode", newValue);
			notifyMode(is,player);
		}
		else if(Helper.isServer())
		{
			if(mode.equals(TardisScrewdriverMode.Locate))
			{
				TardisCoreTileEntity core = getLinkedCore(is);
				if(core != null)
				{
					if(Helper.getWorldID(core.worldObj) == Helper.getWorldID(player.worldObj))
					{
						player.addChatMessage("[Sonic Screwdriver]You are in the TARDIS");
					}
					else
					{
						if(core != null)
						{
							TardisTileEntity ext = core.getExterior();
							if(ext != null)
							{
								if(ext.worldObj.provider.dimensionId != player.worldObj.provider.dimensionId)
									player.addChatMessage("[Sonic Screwdriver]The TARDIS does not appear to be in this dimension");
								else
									player.addChatMessage("[Sonic Screwdriver]The TARDIS is at ["+ext.xCoord+","+ext.yCoord+","+ext.zCoord+"]");
							}
						}
					}
				}
				else
					player.addChatMessage("[Sonic Screwdriver]The TARDIS could not be located");
			}
		}
        return is;
    }

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub
		
	}

}
