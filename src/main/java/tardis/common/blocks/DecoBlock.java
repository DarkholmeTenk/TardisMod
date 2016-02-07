package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractItemBlock;

import java.util.HashMap;

import net.minecraft.world.IBlockAccess;
import tardis.Configs;
import tardis.TardisMod;

public class DecoBlock extends AbstractBlock
{
	private static final String[]		subs		= { "Floor", "Wall", "Walkway"};
	private static String[]				suffixes	= { "topbottom", "side" };
	public HashMap<Integer, Boolean>	litUpDim	= new HashMap<Integer, Boolean>();

	public DecoBlock()
	{
		super(TardisMod.modName);
		initData();
	}

	@Override
	public Class<? extends AbstractItemBlock> getIB()
	{
		return DecoItemBlock.class;
	}

	@Override
	public void initData()
	{
		setBlockName("DecoBlock");
		setSubNames(subs);
		setLightLevel(Configs.lightBlocks ? 1 : 0);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}

	@Override
	public void initRecipes()
	{
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess w, int s, int x, int y, int z, int mX, int mY, int mZ)
	{
		if ((w.getBlock(mX, mY, mZ) == this) && (w.getBlockMetadata(mX, mY, mZ) == w.getBlockMetadata(x, y, z)))
			return false;
		return super.shouldSideBeRendered(w, s, x, y, z, mX, mY, mZ);
	}
}
