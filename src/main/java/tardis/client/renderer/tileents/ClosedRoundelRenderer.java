package tardis.client.renderer.tileents;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;

import tardis.common.TMRegistry;

public class ClosedRoundelRenderer extends AbstractObjRenderer
{
	private static IModelCustom bubble;
	private static ResourceLocation tex;

	static
	{
		bubble = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/bubble.obj"));
		tex = new ResourceLocation("tardismod","textures/models/bubble.png");
	}

	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.colorableRoundelBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z, float ptt)
	{
		bindTexture(tex);
		GL11.glPushMatrix();
		GL11.glRotated(90, 1, 0, 0);
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glRotated(180, 1, 0, 0);
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glRotated(270, 1, 0, 0);
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glRotated(90, 0, 0, 1);
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glRotated(270, 0, 0, 1);
		GL11.glTranslated(0, 0.8, 0);
		GL11.glScaled(0.5, 0.4, 0.5);
		bubble.renderAll();
		GL11.glPopMatrix();
	}

}
