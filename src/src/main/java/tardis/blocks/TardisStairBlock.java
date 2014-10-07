package tardis.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.TardisMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.Icon;
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
