package tardis.blocks;

import tardis.TardisMod;

public class TardisInternalDoorItemBlock extends TardisAbstractItemBlock
{

	public TardisInternalDoorItemBlock(int par1)
	{
		super(par1);
	}

	@Override
	protected TardisAbstractBlock getBlock()
	{
		return TardisMod.internalDoorBlock;
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return (damage % 8) < 4 ? 0 : 4;
	}

}
