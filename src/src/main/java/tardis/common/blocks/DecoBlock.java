package tardis.common.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DecoBlock extends AbstractBlock
{
	private final boolean lit;
	private static final String[] subs = {"Floor","Wall","Roundel","Corridor","CorridorRoundel","CorridorFloor","Glass", "WallPlain"};
	private static String[] suffixes = {"topbottom","side"};
	
	public DecoBlock(boolean light)
	{
		super();
		lit = light;
	}

	@Override
	public void initData()
	{
		setBlockName("DecoBlock");
		//setBlockName(lit ? "DecoBlock" : "DarkDecoBlock");
		setSubNames(subs);
		if(lit)
			setLightLevel(1F);
		else
			setLightLevel(0F);
	}
	
	@Override
	public String getUnlocalizedNameForIcon()
	{
		return "DecoBlock";
	}
	
	@Override
	public String[] getIconSuffix()
	{
		return null;
	}
	
	@Override
	public String[] getIconSuffix(int meta)
	{
		return null;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public void initRecipes()
	{
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		if(w.getBlock(mX, mY, mZ) == this && w.getBlockMetadata(mX, mY, mZ) == w.getBlockMetadata(x, y, z))
        	return false;
        return super.shouldSideBeRendered(w, s, x,y,z,mX, mY, mZ);
	}
	
	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
		setBoundsFromState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        resetBounds();
    }
	
	private void setBoundsFromState(IBlockAccess w, int x, int y, int z)
	{
		boolean flag = isLadder(w,x,y,z,null);

		if (flag)
			this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
		else
			resetBounds();
	}
	
	private void resetBounds()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean isLadder(IBlockAccess w, int x, int y, int z,EntityLivingBase ent)
	{
		//TardisOutput.print("TDB", "Ladder?" + w.getBlockMetadata(x, y, z));
		return w.getBlockMetadata(x, y, z) == 8;
	}
}
