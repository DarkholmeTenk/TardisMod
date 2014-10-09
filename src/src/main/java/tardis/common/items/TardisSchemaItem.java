package tardis.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TardisSchemaItem extends TardisAbstractItem
{

	public TardisSchemaItem(int par1)
	{
		super(par1);
		setMaxStackSize(1);
		setUnlocalizedName("Schematic");
	}
	
	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List infoList, boolean par4)
	{
		if(is != null)
		{
			NBTTagCompound isTag = is.stackTagCompound;
			if(isTag != null && isTag.hasKey("schemaName"))
				infoList.add("Schematic file:" + isTag.getString("schemaName"));
			else
				infoList.add("Schematic file: --NONE--");
		}
	}

}
