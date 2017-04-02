package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;

import tardis.common.TMRegistry;
import tardis.common.tileents.MagicDoorTileEntity;

public class MagicDoorTileEntityRenderer extends AbstractObjRenderer
{
	private static ResourceLocation back = new ResourceLocation("tardismod","textures/models/PortalBack.png");
	private static ResourceLocation mid = new ResourceLocation("tardismod","textures/models/PortalMid.png");
	private static ResourceLocation front = new ResourceLocation("tardismod","textures/models/PortalFront.png");

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.magicDoorBlock;
	}

	private void renderFace(Tessellator tess, double mX, double MX, double mY, double MY, double mU, double MU, double mV, double MV, double z)
	{
		tess.startDrawingQuads();
		tess.addVertexWithUV(mX, mY, z, mU, mV);
		tess.addVertexWithUV(mX, MY, z, mU, MV);
		tess.addVertexWithUV(MX, MY, z, MU, MV);
		tess.addVertexWithUV(MX, mY, z, MU, mV);
		tess.draw();
	}

	private void renderFace(Tessellator tess, ResourceLocation tex, double xP, double yP, double z)
	{
		if(xP < 0) xP += 1;
		if(yP < 0) yP += 1;
		double xO = 1 - xP;
		double yO = 1 - yP;
		bindTexture(tex);
		renderFace(tess, 0,xP, 0,yP ,xO,1, yO,1, z);
		if(xP != 1)
			renderFace(tess, xP,1, 0,yP, 0,xO, yO,1, z);
		if(yP != 1)
			renderFace(tess, 0,xP, yP,1, xO,1, 0,yO, z);
		if((yP != 1) && (xP != 1))
			renderFace(tess, xP,1, yP,1, 0,xO, 0,yO,z);
	}

	private void renderMoving(Tessellator tess, ResourceLocation tex, double divisor, boolean xb, double z)
	{
		EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
		if(divisor != 0)
		{
			double tx = ((xb ? pl.posZ : pl.posX) / divisor) % 1;
			double ty = (pl.posY / divisor) % 1;
			renderFace(tess,tex,tx,ty,z);
		}
		else
			renderFace(tess,tex,1,1,z);
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(te == null) return;
		MagicDoorTileEntity mte = (MagicDoorTileEntity) te;
		int facing = mte.coords().getMetadata() % 4;
		GL11.glPushMatrix();
		GL11.glRotated(-90 * (facing+1), 0, 1, 0);
		GL11.glTranslated(0, 0, 1);
		GL11.glScaled(6, 6, 6);
		boolean xb = (te.getBlockMetadata() % 2) == 0;
		GL11.glTranslated(-0.5, -0.5, 0);
		renderMoving(tess, back, 9, xb, -0.001);
		renderMoving(tess, mid, 15, xb, -0.002);
		renderMoving(tess, front, 0, xb, -0.003);
		GL11.glPopMatrix();
	}

}
