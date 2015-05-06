package tardis.client;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.MinecraftForgeClient;
import tardis.TardisMod;
import tardis.client.renderer.SonicScrewdriverRenderer;
import tardis.client.renderer.tileents.BatteryRenderer;
import tardis.client.renderer.tileents.ClosedRoundelRenderer;
import tardis.client.renderer.tileents.ComponentRenderer;
import tardis.client.renderer.tileents.ConsoleRenderer;
import tardis.client.renderer.tileents.CoreRenderer;
import tardis.client.renderer.tileents.EngineRenderer;
import tardis.client.renderer.tileents.LabRenderer;
import tardis.client.renderer.tileents.LandingPadRenderer;
import tardis.client.renderer.tileents.TardisRenderer;
import tardis.common.TardisProxy;
import tardis.common.core.TardisOutput;
import tardis.common.tileents.BatteryTileEntity;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.ConsoleTileEntity;
import tardis.common.tileents.CoreTileEntity;
import tardis.common.tileents.EngineTileEntity;
import tardis.common.tileents.LabTileEntity;
import tardis.common.tileents.LandingPadTileEntity;
import tardis.common.tileents.TardisTileEntity;
import tardis.common.tileents.extensions.DummyRoundelTE;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TardisClientProxy extends TardisProxy
{
	private ResourceLocation defaultSkin = new ResourceLocation("tardismod","textures/models/Tardis.png");
	public HashMap<String,ResourceLocation> skins = new HashMap<String,ResourceLocation>();
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
	public void init()
	{
		TardisOutput.print("TM", "Sending message to WAILA");
		FMLInterModComms.sendMessage("Waila","register","tardis.common.integration.waila.WailaCallback.wailaRegister");
	}

	public static SonicScrewdriverRenderer screwRenderer;

	@Override
	public void postAssignment()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TardisTileEntity.class, new TardisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CoreTileEntity.class, new CoreRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConsoleTileEntity.class, new ConsoleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ComponentTileEntity.class, new ComponentRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(EngineTileEntity.class, new EngineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LabTileEntity.class, new LabRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LandingPadTileEntity.class, new LandingPadRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BatteryTileEntity.class, new BatteryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DummyRoundelTE.class, new ClosedRoundelRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TardisMod.labBlock), new LabRenderer());
		MinecraftForgeClient.registerItemRenderer(TardisMod.screwItem, screwRenderer = new SonicScrewdriverRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TardisMod.battery), new BatteryRenderer());
	}

	@Override
	public World getWorld(int id)
	{
		if(!ServerHelper.isServer())
			if(Minecraft.getMinecraft() != null)
				if(Minecraft.getMinecraft().thePlayer != null)
					cWorld = Minecraft.getMinecraft().thePlayer.worldObj;
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
