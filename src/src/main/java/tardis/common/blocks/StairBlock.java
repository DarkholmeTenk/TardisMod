package tardis.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;
import tardis.TardisMod;
import cpw.mods.fml.common.registry.GameRegistry;

public class StairBlock extends BlockStairs
{
	private String unlocalizedFragment;
	
	public StairBlock()
	{
		super(TardisMod.decoBlock,3);
		setHardness(-1.0f);
		setCreativeTab(TardisMod.tab);
		setBlockName("StairBlock");
		setLightLevel(1F);
	}
	
	public StairBlock register()
	{
		GameRegistry.registerBlock(this, getUnlocalizedName());
		return this;
	}
	
	@Override
	public Block setBlockName(String name)
	{
		unlocalizedFragment = name;
		return super.setBlockName(name);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "tile.TardisMod." + unlocalizedFragment;
	}
	
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
		return false;
    }
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return false;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
    {
		return false;
    }
	
	@Override
	public int getMobilityFlag()
	{
		return 2;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

}
