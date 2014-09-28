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
		setSubNames("Floor","Door","DoorHidden");
	}

	@Override
	public void initRecipes()
	{
		
	}
	
	private boolean isExistent(IBlockAccess blockAcc,int x, int y, int z)
	{
		int blockDamage = blockAcc.getBlockMetadata(x,y,z);
		return (!getSubName(blockDamage).equals("DoorHidden"));
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		if(isExistent(blockAccess,x,y,z))
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		else
			setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAcc, int x, int y, int z, int side)
	{
		int blockDamage = blockAcc.getBlockMetadata(x,y,z);
		return isExistent(blockAcc,x,y,z);
	}
}
