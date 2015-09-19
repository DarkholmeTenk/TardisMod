package tardis.client.renderer.tileents;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlockRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.renderer.model.StickModel;
import tardis.client.renderer.model.TardisBlockModel;
import tardis.common.tileents.ComponentTileEntity;
import tardis.common.tileents.LandingPadTileEntity;
import tardis.common.tileents.components.TardisTEComponent;

public class ComponentRenderer extends AbstractBlockRenderer
{
	TardisBlockModel block = new TardisBlockModel();
	private static ResourceLocation tex;
	private static ResourceLocation flatTex;
	StickModel stick = new StickModel();

	static
	{
		tex = new ResourceLocation("tardismod","textures/models/bubbledepressed.png");
		flatTex = new ResourceLocation("tardismod","textures/models/bubbleOverlay.png");
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TardisMod.componentBlock;
	}

	private void renderStick(ResourceLocation tn, double x, double y, double r, double p, double yaw)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5,0.5);
		GL11.glRotated(r, 0, 0, 1);
		GL11.glRotated(p, 0, 1, 0);
		GL11.glRotated(yaw, 1, 0, 0);
		GL11.glTranslated(0.48, x,y-0.03125);
		bindTexture(tn);
		stick.render(null, 0, 0, 0,0,0, 0.06125F);
		GL11.glPopMatrix();
	}

	private void renderStick(String tn,double x, double y, double yaw)
	{
		ResourceLocation res = new ResourceLocation("tardismod","textures/models/"+tn+".png");
		renderStick(res,x,y,0,0,yaw);
		renderStick(res,x,y,-90,0,180-yaw);
		renderStick(res,x,y,90,0,yaw);
		renderStick(res,x,y,0,180,yaw);
		renderStick(res,x,y,0,90,yaw);
		renderStick(res,x,y,0,-90,yaw);
	}

	private void renderBubble(Tessellator tess,double r1, double r2)
	{
		/*
		double y = 1.035;
		double x = 0;
		double z = 0;

		GL11.glPushMatrix();
		GL11.glRotated(r1, 1, 0, 0);
		GL11.glRotated(r2, 0, 0, 1);
		GL11.glTranslated(x, y, z);
		GL11.glScaled(0.5, 0.35, 0.5);
		GL11.glRotated(-10.5, 0, 1, 0);
		bubble.renderAll();
		GL11.glPopMatrix();*/
		GL11.glPushMatrix();
		GL11.glRotated(r1, 0, 1, 0);
		GL11.glRotated(r2, 0, 0, 1);
		GL11.glTranslated(-1.01, -0.5, -0.5);
		bindTexture(flatTex);
		tess.startDrawingQuads();
		tess.addVertexWithUV(0, 0, 0, 0, 0);
		tess.addVertexWithUV(0, 0, 1, 0, 1);
		tess.addVertexWithUV(0, 1, 1, 1, 1);
		tess.addVertexWithUV(0, 1, 0, 1, 0);
		tess.draw();
		GL11.glPopMatrix();
	}

	private void renderBubble(Tessellator tess)
	{
		double xs = 0.5;
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.5, 0.5);
		GL11.glScaled(xs, xs, xs);
		bindTexture(tex);
		renderBubble(tess,0,0);
		renderBubble(tess,90,0);
		renderBubble(tess,180,0);
		renderBubble(tess,270,0);
		renderBubble(tess,0,90);
		renderBubble(tess,0,270);
		GL11.glPopMatrix();
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		/*
		int metadata = te.worldObj.getBlockMetadata(x, y, z);
		if(metadata == 0)
			bindTexture(new ResourceLocation("tardismod","textures/models/TardisOpenRoundel.png"));
		else
			bindTexture(new ResourceLocation("tardismod","textures/models/TardisOpenCorridorRoundel.png"));
		block.render(null, 0, 0, 0, 0, 0, 0.06125F);*/
		if(te instanceof ComponentTileEntity)
		{
			if(!(te instanceof LandingPadTileEntity))
				renderBubble(tess);
			ComponentTileEntity tcte = ((ComponentTileEntity)te);
			int count =0;
			for(TardisTEComponent comp : TardisTEComponent.values())
				if(tcte.hasComponent(comp))
					count++;
			if(count > 0)
			{
				double ang = -360 / count;
				int i = 0;
				for(TardisTEComponent comp : TardisTEComponent.values())
					if(tcte.hasComponent(comp))
						renderStick(comp.tex,0.1,0,(i++)*ang);
			}
		}
	}

}
