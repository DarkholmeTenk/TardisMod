package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

public class ModelSlider extends AbstractControlModel
{
	private final static IModelCustom model = RenderHelper.getModel(new ResourceLocation("tardismod","models/console/slider.obj"));
	private final static ResourceLocation tex = new ResourceLocation("tardismod","textures/models/console/slider.png");

	public ModelSlider()
	{
		super(0.2, 1, 0);
	}

	@Override
	public void render(float state)
	{
		double x = 0.42;
		double xx = x * 2;
		RenderHelper.bindTexture(tex);
		GL11.glTranslated(0, 0.02, 0);
		GL11.glRotated(180, 1, 0, 0);
		model.renderOnly("Groove");
		GL11.glTranslated(x-(state* xx), 0, 0);
		model.renderOnly("Slider");
	}

}
