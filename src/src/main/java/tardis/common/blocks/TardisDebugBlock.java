package tardis.common.blocks;

import tardis.TardisMod;
import tardis.common.core.Helper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TardisDebugBlock extends TardisAbstractBlock
{

	public TardisDebugBlock(int blockID)
	{
		super(blockID);
	}

	@Override
	public void initData()
	{
		setUnlocalizedName("DebugBlock");
	}

	@Override
	public void initRecipes()
	{
		
	}
	
	@Override
	public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		int dimID = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimID, TardisMod.providerID);
		MinecraftServer.getServer().worldServerForDimension(dimID).setBlock(0, 8, 0, 1);
		Helper.teleportEntity(player, dimID, 0, 10, 0);
		return true;
	}

}
