package tardis.common.items.extensions.screwtypes;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.helpers.ServerHelper;

import tardis.client.renderer.model.SonicScrewdriverLightModel;
import tardis.client.renderer.model.SonicScrewdriverModel;
import tardis.common.core.helpers.ScrewdriverHelper;

public class Tenth extends AbstractScrewdriverType
{
	SonicScrewdriverModel model;
	SonicScrewdriverLightModel light;
	IModelCustom bottom;
	IModelCustom middle;
	IModelCustom top;
	ResourceLocation bottomTex;
	ResourceLocation middleTex;
	ResourceLocation topTex;

	@Override
	public void registerClientResources()
	{
		if(ServerHelper.isClient())
		{
			model = new SonicScrewdriverModel();
			light = new  SonicScrewdriverLightModel();
			bottom = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwbottom.obj"));
			bottomTex = new ResourceLocation("tardismod","textures/models/screwbottom.png");
			middle = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwmain.obj"));
			middleTex = new ResourceLocation("tardismod","textures/models/screwmain.png");
			top = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwtop.obj"));
			topTex = new ResourceLocation("tardismod","textures/models/screwtop.png");
		}
	}

	@Override
	public void render(ScrewdriverHelper helper)
	{
		GL11.glPushMatrix();
		GL11.glScaled(0.45, 0.4, 0.45);
		Minecraft.getMinecraft().renderEngine.bindTexture(bottomTex);
		GL11.glTranslated(0, -0.8, 0);
			GL11.glPushMatrix();
			GL11.glScaled(0.75, 0.75, 0.75);
			bottom.renderAll();
			GL11.glPopMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(middleTex);
		GL11.glTranslated(0, 0.1, 0);
		middle.renderAll();
		Minecraft.getMinecraft().renderEngine.bindTexture(topTex);
		GL11.glTranslated(0, 1.28, 0);
		top.renderAll();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		if(helper != null)
		{
			double[] color = helper.getColors();
			if((color != null) && (color.length >= 3))
			{
				GL11.glTranslated(0, 0.88, 0);
				GL11.glColor3d(color[0], color[1], color[2]);
				light.render(null, 0.0F, 0.0F, -0.0F, 0.0F, 0.0F, 0.0625F);
				GL11.glColor3d(1, 1, 1);
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	public String getName()
	{
		return "Tenth";
	}

}
