package tardis.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import tardis.client.renderer.model.SonicScrewdriverLightModel;
import tardis.client.renderer.model.SonicScrewdriverModel;
import tardis.common.items.SonicScrewdriverItem;

public class SonicScrewdriverRenderer implements IItemRenderer
{
	SonicScrewdriverModel model;
	SonicScrewdriverLightModel light;
	IModelCustom bottom;
	IModelCustom middle;
	IModelCustom top;
	ResourceLocation bottomTex = new ResourceLocation("tardismod","textures/models/screwbottom.png");
	ResourceLocation middleTex = new ResourceLocation("tardismod","textures/models/screwmain.png");;
	ResourceLocation topTex = new ResourceLocation("tardismod","textures/models/screwtop.png");;
	
	{
		model = new SonicScrewdriverModel();
		light = new  SonicScrewdriverLightModel();
		bottom = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwbottom.obj"));
		middle = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwmain.obj"));
		top = AdvancedModelLoader.loadModel(new ResourceLocation("tardismod","models/screwtop.obj"));
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
	
	public void render(ItemStack item)
	{
		GL11.glPushMatrix();
		//Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriver.png"));
		//model.render(null, 0.0F, 0.0F, -0.0F, 0.0F, 0.0F, 0.0625F);
		//GL11.glRotated(180, 1, 0, 0);
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
		if(item != null)
		{
			double[] color = SonicScrewdriverItem.getColors(item);
			if(color != null && color.length >= 3)
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
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		//This line actually rotates the renderer.
		//GL11.glRotatef(180F, 0F, 0, 1F);
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-65, 1, 0, 0);
			GL11.glTranslated(0, -0.8, 0.75);
			GL11.glScaled(1.65, 1.65, 1.65);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotated(220, 0, 1, 0);
			GL11.glTranslated(0, 0.6, 0);
			GL11.glScaled(1.65, 1.65, 1.65);
		}
		else if(type.equals(ItemRenderType.INVENTORY))
		{
			GL11.glScaled(1.75, 1.75, 1.75);
			GL11.glTranslated(0,-0.1,0);
		}
		render(item);
		GL11.glPopMatrix();
	}

}
