package tardis.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.IBlockAccess;

public class TardisSchemaBlock extends TardisAbstractBlock 
{
	public TardisSchemaBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("Schema");
	}

	@Override
	public void initRecipes()
	{
		
	}
}
