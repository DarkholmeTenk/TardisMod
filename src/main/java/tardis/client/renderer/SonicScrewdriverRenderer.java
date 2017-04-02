package tardis.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import tardis.common.core.helpers.ScrewdriverHelper;
import tardis.common.core.helpers.ScrewdriverHelperFactory;

public class SonicScrewdriverRenderer implements IItemRenderer
{
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
		if(type.equals(ItemRenderType.EQUIPPED))
		{
			GL11.glRotated(45, 0, 1, 0);
			GL11.glRotated(-65, 1, 0, 0);
			GL11.glTranslated(0, -0.8, 0.75);
			GL11.glScaled(1.65, 1.65, 1.65);
			GL11.glRotated(90, 0, 1, 0);
		}
		else if(type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON))
		{
			GL11.glRotated(40, 0, 1, 0);
			GL11.glTranslated(0, 0.6, 0);
			GL11.glScaled(1.65, 1.65, 1.65);
		}
		else if(type.equals(ItemRenderType.INVENTORY))
		{
			GL11.glScaled(1.75, 1.75, 1.75);
			GL11.glTranslated(0,-0.1,0);
		}
		ScrewdriverHelper helper = ScrewdriverHelperFactory.get(item);
		helper.render();
		GL11.glPopMatrix();
	}

}
