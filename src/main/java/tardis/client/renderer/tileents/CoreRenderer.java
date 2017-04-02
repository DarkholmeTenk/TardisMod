package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractBlockRenderer;

import tardis.client.renderer.model.RotorModel;
import tardis.common.TMRegistry;
import tardis.common.tileents.CoreTileEntity;

public class CoreRenderer extends AbstractBlockRenderer
{
	RotorModel rotor;
	//OctagonModel oct;
	IModelCustom oct;
	IModelCustom cap;
	IModelCustom ang;
	IModelCustom scr;
	ResourceLocation rotorTex = new ResourceLocation("tardismod","textures/models/TardisRotorA.png");
	ResourceLocation octTex = new ResourceLocation("tardismod","textures/models/oct.png");
	ResourceLocation capTex = new ResourceLocation("tardismod","textures/models/cap.png");
	ResourceLocation angTex = new ResourceLocation("tardismod","textures/models/ang.png");
	ResourceLocation scrTex = new ResourceLocation("tardismod","textures/models/screen.png");

	public CoreRenderer()
	{
		rotor = new RotorModel();
		//oct = new OctagonModel();
		oct = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/oct.obj"));
		cap = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/cap.obj"));
		ang = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/ang.obj"));
		scr = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screen.obj"));
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.tardisCoreBlock;
	}

	private void renderRotor(Tessellator tess, CoreTileEntity core)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 0, 0.5F);
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glTranslatef(0F, -0.7F, 0F);
		bindTexture(rotorTex);
		float proximity = core.getProximity();
		//GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
		GL11.glScaled(0.75, 1,0.75);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0.4F -proximity, 0);
		rotor.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glTranslatef(0, 0.4F -proximity, 0);
		rotor.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private void renderOct(Tessellator tess)
	{
		GL11.glTranslated(0, 0.0675, 0);
		GL11.glScaled(0.76, 0.65, 0.76);
		GL11.glRotated(180, 0, 0, 1);
		oct.renderAll();
	}

	private void renderSpinner(Tessellator tess, CoreTileEntity core)
	{
		double spin = core.getSpin();
		GL11.glPushMatrix();
			bindTexture(octTex);
			//bindTexture(new ResourceLocation("tardismod","textures/models/Octagon.png"));
			GL11.glTranslated(0.5, 2.3, 0.5);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glScaled(1.33, 1.6, 1.33);
			GL11.glPushMatrix();
				GL11.glTranslated(0, -0.33, 0);
				GL11.glScaled(1.15, 1.15, 1.15);
				GL11.glRotated(spin+(2*45), 0, 1, 0);
				renderOct(tess);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
				GL11.glTranslated(0, -0.63, 0);
				GL11.glScaled(1.45, 1.2, 1.45);
				GL11.glRotated(spin+(3*45), 0, -1, 0);
				renderOct(tess);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
				GL11.glTranslated(0, -0.98, 0);
				GL11.glScaled(1.8, 1.4, 1.8);
				GL11.glRotated(spin+(4*45), 0, 1, 0);
				renderOct(tess);
			GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private void renderScreen(Tessellator tess, CoreTileEntity te)
	{
		GL11.glPushMatrix();
		int angle = te.getScreenAngle();
		GL11.glTranslated(0.5, -0.5, 0.5);
		GL11.glRotated(angle, 0, 1, 0);
		GL11.glTranslated(0.4, 0, 0);
		GL11.glScaled(0.5, 0.5, 0.5);
		bindTexture(scrTex);
		scr.renderAll();
		GL11.glPushMatrix();
		GL11.glRotated(180, 0, 0, 1);
		GL11.glRotated(90, 0, 1, 0);
		GL11.glTranslated(-0.75, -1.37, -0.583);
		double scale = 0.02;
		GL11.glScaled(scale, scale, scale);
		GL11.glDepthMask(false);
		String[] strings = te.getScreenText();
		for(String s : strings)
		{
			fr.drawString(s, 0, 0, 16579836);
			GL11.glTranslated(0, 10, 0);
		}
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

	private void renderCaps(Tessellator tess, CoreTileEntity te)
	{
		GL11.glPushMatrix();
		bindTexture(capTex);
		double sc = 0.5;
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glScaled(sc, sc, sc);
		GL11.glPushMatrix();
		GL11.glTranslated(0, -0.55, 0);
		GL11.glRotated(180, 1, 0, 0);
		cap.renderAll();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(0, 3.35, 0);
		cap.renderAll();
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 2.37, 0.5);
		GL11.glScaled(sc, 1, sc);
		bindTexture(angTex);
		ang.renderAll();
		GL11.glPopMatrix();
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(te instanceof CoreTileEntity)
		{
			CoreTileEntity core = (CoreTileEntity)te;
			renderRotor(tess,core);
			renderCaps(tess,core);
			renderSpinner(tess,core);
			renderScreen(tess,core);
		}
	}

}
