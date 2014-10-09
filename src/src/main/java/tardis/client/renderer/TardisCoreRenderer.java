package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import tardis.TardisMod;
import tardis.client.renderer.model.TardisRotorModel;
import tardis.common.blocks.TardisAbstractBlock;
import tardis.common.tileents.TardisCoreTileEntity;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TardisCoreRenderer extends TardisAbstractBlockRenderer
{
	TardisRotorModel rotor;
	
	public TardisCoreRenderer()
	{
		rotor = new TardisRotorModel();
	}
	
	@Override
	public TardisAbstractBlock getBlock()
	{
		return TardisMod.tardisCoreBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		GL11.glPushMatrix();
		//This line actually rotates the renderer.
		GL11.glTranslatef(0.5F, 0, 0.5F);
		GL11.glRotatef(180F, 0F, 0, 1F);
		GL11.glTranslatef(0F, -0.5F, 0F);
		bindTexture(new ResourceLocation("tardismod","textures/models/TardisRotorA.png"));
		float proximity = 0.0f;
		if(te instanceof TardisCoreTileEntity)
			proximity = ((TardisCoreTileEntity)te).getProximity();
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

}
