package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.abstracts.AbstractBlock;
import io.darkcraft.darkcore.mod.abstracts.AbstractObjRenderer;

import tardis.common.TMRegistry;
import tardis.common.core.TardisOutput;

public class TestRenderer extends AbstractObjRenderer
{
	private static IModelCustom model = null;

	{
		if(model == null)
		{
			ResourceLocation loc = new ResourceLocation("tardismod","models/test.obj");
			TardisOutput.print("TTR",loc.getResourceDomain());
			model = AdvancedModelLoader.loadModel(loc);
			//model = new WavefrontObject();
		}
	}
	@Override
	public AbstractBlock getBlock()
	{
		return TMRegistry.debugBlock;
	}

	@Override
	public void renderBlock(Tessellator tess, TileEntity te, int x, int y, int z)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(1, -0.5, 1);
		bindTexture(new ResourceLocation("tardismod","models/test.png"));
		model.renderAll();
		GL11.glPopMatrix();
	}

}
