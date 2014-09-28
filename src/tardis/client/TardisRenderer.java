package tardis.client;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.tileents.TardisTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TardisRenderer extends TileEntitySpecialRenderer
{
	TardisModel model;
	
	public TardisRenderer()
	{
		model = new TardisModel();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d0, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//This will move our renderer so that it will be on proper place in the world
		GL11.glTranslatef((float)d0, (float)d1, (float)d2);
		/*Note that true tile entity coordinates (tileEntity.xCoord, etc) do not match to render coordinates (d, etc) that are calculated as [true coordinates] - [player coordinates (camera coordinates)]*/
		renderBlock(tileEntity, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, (Block)TardisMod.tardisBlock);
		GL11.glPopMatrix();
	}

	public void renderBlock(TileEntity tl, World world, int i, int j, int k, Block block) {
		Tessellator tessellator = Tessellator.instance;
		//This will make your block brightness dependent from surroundings lighting.
		float f = block.getBlockBrightness(world, i, j, k);
		int l = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
		int l1 = l % 65536;
		int l2 = l / 65536;
		tessellator.setColorOpaque_F(f, f, f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)l1, (float)l2);
		TardisTileEntity tte = null;
		if(tl != null && tl instanceof TardisTileEntity)
			tte = (TardisTileEntity) tl;
		int dir = world.getBlockMetadata(i,j,k);
		if(dir < 4 && tte != null)
		{
			GL11.glPushMatrix();
			//This line actually rotates the renderer.
			GL11.glTranslatef(0.5F, 0, 0.5F);
			GL11.glRotatef(dir * (-90F), 0F, 1F, 0F);
			GL11.glRotatef(180F, 0F, 0, 1F);
			GL11.glTranslatef(0F, -1.5F, 0F);
			bindTexture(new ResourceLocation("tardismod","textures/models/Tardis.png"));
			GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
			model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		}
	}
}
