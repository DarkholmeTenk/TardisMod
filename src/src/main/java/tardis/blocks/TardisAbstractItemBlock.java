package tardis.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class TardisAbstractItemBlock extends ItemBlock
{
	int bID;
	
	public TardisAbstractItemBlock(int par1)
	{
		super(par1);
		bID = par1;
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}
	
	protected abstract TardisAbstractBlock getBlock();
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		TardisAbstractBlock block = getBlock();
		if(block != null)
		{
			return block.getUnlocalizedName(itemStack.getItemDamage());
		}
		return Block.blocksList[this.bID].getUnlocalizedName();
	}

}
