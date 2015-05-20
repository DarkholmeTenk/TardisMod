package tardis.common.blocks;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.helpers.TeleportHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tardis.TardisMod;

public class DebugBlock extends AbstractBlock
{

	public DebugBlock()
	{
		super(TardisMod.modName);
	}

	@Override
	public void initData()
	{
		setBlockName("DebugBlock");
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
		MinecraftServer.getServer().worldServerForDimension(dimID).setBlock(0, 8, 0, Blocks.stone);
		TeleportHelper.teleportEntity(player, dimID, 0, 10, 0);
		return true;
	}

}
