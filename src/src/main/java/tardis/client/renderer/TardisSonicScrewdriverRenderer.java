package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import tardis.client.renderer.model.TardisSonicScrewdriverModel;
import tardis.common.core.TardisOutput;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class TardisSonicScrewdriverRenderer implements IItemRenderer
{
	TardisSonicScrewdriverModel model;
	
	{
		model = new TardisSonicScrewdriverModel();
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

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		//This line actually rotates the renderer.
		GL11.glRotatef(180F, 0F, 0, 1F);
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotatef(90F,1F,0F,1F);
			GL11.glRotatef(-25F,1F,0F,1F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glTranslatef(-0.75F, 0.8F, -0F);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotatef(-35F,0F,1F,0F);
			GL11.glRotatef(-15F,0F,0F,1F);
			GL11.glTranslatef(0F, -1F, -0F);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriver.png"));
		model.render(null, 0.0F, 0.0F, -0.0F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

}
