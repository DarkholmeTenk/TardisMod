package tardis.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.MinecraftForgeClient;
import tardis.TardisMod;
import tardis.client.renderer.LabRenderer;
import tardis.client.renderer.TardisComponentRenderer;
import tardis.client.renderer.TardisConsoleRenderer;
import tardis.client.renderer.TardisCoreRenderer;
import tardis.client.renderer.TardisEngineRenderer;
import tardis.client.renderer.TardisRenderer;
import tardis.client.renderer.TardisSonicScrewdriverRenderer;
import tardis.common.TardisProxy;
import tardis.common.core.Helper;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisEngineTileEntity;
import tardis.common.tileents.TardisTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;

public class TardisClientProxy extends TardisProxy
{
	public static World cWorld = null;
	public TardisClientProxy()
	{
	}

	@Override
	public void handleTardisTransparency(int worldID,int x, int y, int z)
	{
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(worldID);
		world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
	}
	
	@Override
	public void postAssignment()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TardisTileEntity.class, new TardisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisCoreTileEntity.class, new TardisCoreRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisConsoleTileEntity.class, new TardisConsoleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisComponentTileEntity.class, new TardisComponentRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisEngineTileEntity.class, new TardisEngineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LabTileEntity.class, new LabRenderer());
		MinecraftForgeClient.registerItemRenderer(TardisMod.screwItem, new TardisSonicScrewdriverRenderer());
	}
	
	@Override
	public World getWorld(int id)
	{
		if(!Helper.isServer())
		{
			if(Minecraft.getMinecraft() != null)
			{
				if(Minecraft.getMinecraft().thePlayer != null)
					cWorld = Minecraft.getMinecraft().thePlayer.worldObj;
			}
		}
		if(cWorld != null)
			if(id == cWorld.provider.dimensionId)
				return cWorld;
		return super.getWorld(id);
	}
}
