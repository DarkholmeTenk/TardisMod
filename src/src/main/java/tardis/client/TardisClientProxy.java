package tardis.client;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.MinecraftForgeClient;
import tardis.TardisMod;
import tardis.client.renderer.TardisComponentRenderer;
import tardis.client.renderer.TardisConsoleRenderer;
import tardis.client.renderer.TardisCoreRenderer;
import tardis.client.renderer.TardisEngineRenderer;
import tardis.client.renderer.TardisRenderer;
import tardis.client.renderer.TardisSonicScrewdriverRenderer;
import tardis.common.TardisProxy;
import tardis.common.core.Helper;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.TardisComponentTileEntity;
import tardis.common.tileents.TardisConsoleTileEntity;
import tardis.common.tileents.TardisCoreTileEntity;
import tardis.common.tileents.TardisEngineTileEntity;
import tardis.common.tileents.TardisTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisClientProxy extends TardisProxy
{
	private ResourceLocation defaultSkin = new ResourceLocation("tardismod","textures/models/Tardis.png");
	public HashMap<String,ResourceLocation> skins = new HashMap<String,ResourceLocation>();
	public static World cWorld = null;
	public TardisClientProxy()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TardisTileEntity.class, new TardisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisCoreTileEntity.class, new TardisCoreRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisConsoleTileEntity.class, new TardisConsoleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisComponentTileEntity.class, new TardisComponentRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TardisEngineTileEntity.class, new TardisEngineRenderer());
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
	
	@SideOnly(Side.CLIENT)
	private ITextureObject loadSkin(TextureManager texMan, TardisTileEntity tte)
	{
		if(tte.owner == null)
			return null;
		texMan = Minecraft.getMinecraft().getTextureManager();
		ResourceLocation skin = new ResourceLocation("tardismod","textures/tardis/" + StringUtils.stripControlCodes(tte.owner) +".png");
		ITextureObject object = texMan.getTexture(skin);
		if(object == null)
		{
			TardisOutput.print("TTE", "Downloading " + tte.owner + " skin");
			object = new ThreadDownloadTardisData(null, TardisTileEntity.baseURL+tte.owner+".png", defaultSkin, new ImageBufferDownload());
		}
		texMan.loadTexture(skin, object);
		skins.put(tte.owner, skin);
		return object;
	}
	
	@SideOnly(Side.CLIENT)
	public ResourceLocation getSkin(TextureManager texMan,TardisTileEntity tte)
	{
		if(!skins.containsKey(tte.owner))
			loadSkin(texMan,tte);
		return skins.containsKey(tte.owner) ? skins.get(tte.owner) : defaultSkin;
	}
}
