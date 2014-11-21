package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.renderer.model.TardisOctagonModel;
import tardis.client.renderer.model.TardisRotorModel;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.tileents.TardisCoreTileEntity;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TardisCoreRenderer extends TardisAbstractBlockRenderer
{
	TardisRotorModel rotor;
	TardisOctagonModel oct;
	
	public TardisCoreRenderer()
	{
		rotor = new TardisRotorModel();
		oct = new TardisOctagonModel();
	}
	
	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.tardisCoreBlock;
	}
	
	private void renderRotor(Tessellator tess, TardisCoreTileEntity core)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 0, 0.5F);
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glTranslatef(0F, -0.7F, 0F);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisRotorA.png"));
		float proximity = core.getProximity();
		//GL11.glColor4f(1F, 1F, 1F, tte.getTransparency());
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
	
	private void renderSpinner(Tessellator tess, TardisCoreTileEntity core)
	{
		double spin = core.getSpin();
		GL11.glPushMatrix();
			bindTexture(new ResourceLocation("tardismod","textures/models/Octagon.png"));
			GL11.glTranslated(0.5, 2.3, 0.5);
			GL11.glRotated(180, 1, 0, 0);
			GL11.glScaled(1.4, 1.6, 1.4);
			GL11.glPushMatrix();
				GL11.glRotated(spin, 0, 1, 0);
				oct.render(null, 0, 0, 0, 0, 0, 0.0625F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
				GL11.glTranslated(0, -0.225, 0);
				GL11.glScaled(1.2, 1, 1.2);
				GL11.glRotated(spin, 0, -1, 0);
				oct.render(null, 0, 0, 0, 0, 0, 0.0625F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
				GL11.glTranslated(0, -0.450, 0);
				GL11.glScaled(1.4, 1, 1.4);
				GL11.glRotated(spin+22.5, 0, 1, 0);
				oct.render(null, 0, 0, 0, 0, 0, 0.0625F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslated(0, -0.675, 0);
			GL11.glScaled(1.7, 1, 1.7);
			GL11.glRotated(-(spin+22.5), 0, 1, 0);
			oct.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		if(te instanceof TardisCoreTileEntity)
		{
			TardisCoreTileEntity core = (TardisCoreTileEntity)te;
			renderRotor(tess,core);
			renderSpinner(tess,core);
		}
	}

}
