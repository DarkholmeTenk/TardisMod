package tardis.common.items.extensions.screwtypes;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import tardis.common.core.helpers.ScrewdriverHelper;

public class Twelth extends AbstractScrewdriverType
{
	private IModelCustom screwModel;
	private IModelCustom light;
	private ResourceLocation screwTex;

	@Override
	public void registerClientResources()
	{
		screwModel = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screw/twelveScrew.obj"));
		light = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screw/twelveLight.obj"));
		screwTex = new ResourceLocation("tardismod","textures/models/twelveScrew.png");
	}

	@Override
	public void render(ScrewdriverHelper helper)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(screwTex);
		//GL11.glTranslated(0, -0.1, 0);
		GL11.glScaled(0.5, 0.5, 0.5);
		screwModel.renderAll();
		GL11.glPushMatrix();
		if(helper != null)
		{
			double[] color = helper.getColors();
			if((color != null) && (color.length >= 3))
			{
				GL11.glTranslated(0, 0.789, 0);
				GL11.glColor3d(color[0], color[1], color[2]);
				light.renderAll();
				GL11.glColor3d(1, 1, 1);
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	public String getName()
	{
		return "Twelth";
	}

}
