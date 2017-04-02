package tardis.common.items.extensions;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import io.darkcraft.darkcore.mod.datastore.SimpleCoordStore;
import io.darkcraft.darkcore.mod.helpers.BlockIterator;
import io.darkcraft.darkcore.mod.interfaces.IBlockIteratorCondition;
import io.darkcraft.darkcore.mod.interfaces.IColorableBlock;

import tardis.common.TMRegistry;

public enum DecoratorToolTypes
{
	WALL("Wall",TMRegistry.colorableWallBlock),
	FLOOR("Floor",TMRegistry.colorableFloorBlock),
	BRICK("Brick",TMRegistry.colorableBrickBlock),
	PLANK("Plank",TMRegistry.colorablePlankBlock),
	GLASS("Glass",TMRegistry.decoTransBlock,0),
	ROUNDEL("Roundel",TMRegistry.colorableRoundelBlock),
	WALKWAY("Walkway",TMRegistry.decoBlock,0),
	WALKWAYTRANS("Walkway2",TMRegistry.decoBlock,2,false),
	WALKWAYTRANSA("Open Walkway",TMRegistry.decoTransBlock,1);

	private final String name;
	private final Block b;
	private final ItemStack is;
	private final int meta;
	public final boolean valid;

	private DecoratorToolTypes(String n, Block b)
	{
		this(n, b, -1);
	}

	private DecoratorToolTypes(String n, Block _b, int _meta)
	{
		this(n, _b, _meta, true);
	}

	private DecoratorToolTypes(String n, Block _b, int _meta, boolean v)
	{
		name = n;
		b = _b;
		meta = _meta;
		is = new ItemStack(b, 1, meta != -1 ? meta : (b instanceof IColorableBlock ? 15 : 0));
		valid = v;
	}

	public String getName()
	{
		return StatCollector.translateToLocal(is.getUnlocalizedName()+".name");
	}

	public ItemStack getIS()
	{
		return is;
	}

	public void set(SimpleCoordStore pos)
	{
		DecoratorToolTypes c = getMatching(pos);
		if(c == null) return;
		if(meta == -1)
		{
			if(c.meta == -1)
				pos.setBlock(b,pos.getMetadata(),3);
			else
				pos.setBlock(b, b instanceof IColorableBlock ? 15 : 0, 3);
		}
		else
			pos.setBlock(b, meta, 3);
	}

	public boolean match(Block block, int m)
	{
		return (b == block) && ((meta == -1) || (meta == m));
	}

	public boolean match(SimpleCoordStore pos)
	{
		return match(pos.getBlock(), pos.getMetadata());
	}

	public static boolean matchAny(SimpleCoordStore pos)
	{
		return getMatching(pos) != null;
	}

	public static DecoratorToolTypes getMatching(SimpleCoordStore pos)
	{
		for(DecoratorToolTypes t : values())
			if(t.match(pos)) return t;
		return null;
	}

	public IBlockIteratorCondition getCondition()
	{
		/*if(meta == -1)
			return BlockIterator.sameExcMeta;
		else
			return BlockIterator.sameIncMeta;*/
		return BlockIterator.sameIncMeta;
	}

	public static DecoratorToolTypes get(int ord)
	{
		DecoratorToolTypes[] vals = values();
		if((ord >= 0) && (ord < vals.length))
			return vals[ord];
		return vals[0];
	}
}
