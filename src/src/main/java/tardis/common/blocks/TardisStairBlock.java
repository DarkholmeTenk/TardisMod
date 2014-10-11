package tardis.common.blocks;

import tardis.TardisMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public class TardisStairBlock extends BlockStairs
{
	private String unlocalizedFragment;
	
	public TardisStairBlock(int id)
	{
		super(id,TardisMod.decoBlock,3);
		setHardness(-1.0f);
		setCreativeTab(TardisMod.tab);
		setUnlocalizedName("StairBlock");
		setLightValue(1F);
	}
	
	@Override
	public Block setUnlocalizedName(String name)
	{
		unlocalizedFragment = name;
		return super.setUnlocalizedName(name);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "tile.TardisMod." + unlocalizedFragment;
	}
	
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
    {
		return false;
    }
	
	@Override
	public boolean canEntityDestroy(World world, int x, int y, int z, Entity entity)
	{
		return false;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

}
