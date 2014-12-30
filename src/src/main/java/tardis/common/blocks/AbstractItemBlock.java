package tardis.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class AbstractItemBlock extends ItemBlock
{
	Block bID;
	
	public AbstractItemBlock(Block par1)
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
	
	protected abstract AbstractBlock getBlock();
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		AbstractBlock block = getBlock();
		if(block != null)
		{
			return block.getUnlocalizedName(itemStack.getItemDamage());
		}
		return bID.getUnlocalizedName();
	}

}
