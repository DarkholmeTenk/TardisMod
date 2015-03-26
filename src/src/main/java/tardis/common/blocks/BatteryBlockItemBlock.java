package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tardis.TardisMod;

public class BatteryBlockItemBlock extends AbstractItemBlock
{

	public BatteryBlockItemBlock(Block par1)
	{
		super(par1);
	}

	@Override
	protected AbstractBlock getBlock()
	{
		return TardisMod.battery;
	}
	
	@Override
	public void addInfo(ItemStack is, EntityPlayer player, List infoList)
	{
		NBTTagCompound nbt = is.stackTagCompound;
		if(nbt != null)
		{
			infoList.add("Artron energy: " + nbt.getInteger("ae"));
		}
	}

}
