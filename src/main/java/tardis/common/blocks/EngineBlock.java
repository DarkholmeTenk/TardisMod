package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tardis.TardisMod;
import tardis.common.tileents.EngineTileEntity;

public class EngineBlock extends AbstractBlockContainer
{
	public EngineBlock()
	{
		super(TardisMod.modName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int extra)
	{
		return new EngineTileEntity();
	}

	@Override
	public void initData()
	{
		setBlockName("TardisEngineBlock");
		setLightLevel(0.8F);
	}

	@Override
	public void initRecipes()
	{
		// TODO Auto-generated method stub

	}
	
	@Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer pl, int s, float i, float j, float k)
    {
		boolean superEffect = super.onBlockActivated(w, x, y, z, pl, s, i, j, k);
		TileEntity te = w.getTileEntity(x,y,z);
		if(te instanceof EngineTileEntity)
		{
			superEffect = ((EngineTileEntity)te).activate(pl, s, y, i, j, k);;
		}
		return superEffect;
    }

	@Override
	public Class<? extends TileEntity> getTEClass()
	{
		return EngineTileEntity.class;
	}
}
