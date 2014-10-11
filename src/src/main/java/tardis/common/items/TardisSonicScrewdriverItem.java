package tardis.common.items;

import java.util.ArrayList;
import java.util.List;

import tardis.api.TardisScrewdriverMode;
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
	
	public void addInformation(ItemStack is, EntityPlayer player, List infoList, boolean par4)
	{
		super.addInformation(is, player, infoList, par4);
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
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
    {
		if(!world.isRemote && player.isSneaking())
		{
			TardisScrewdriverMode mode = getMode(is);
			int newValue = (mode.ordinal() + 1) % TardisScrewdriverMode.values().length;
			is.stackTagCompound.setInteger("screwdriverMode", newValue);
			getMode(is);
			ArrayList<Object> list = new ArrayList<Object>();
			addInformation(is,player,list,false);
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
        return is;
    }

}
