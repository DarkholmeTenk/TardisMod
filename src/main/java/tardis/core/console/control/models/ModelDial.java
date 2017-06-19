package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

public class ModelDial extends AbstractControlModel
{
	public static final ModelDial i = new ModelDial();

	private static final IModelCustom model = RenderHelper.getModel(new ResourceLocation("tardismod","models/console/dial.obj"));
	private static final ResourceLocation tex = new ResourceLocation("tardismod", "textures/models/console/dial.png");

	private ModelDial()
	{
		super(0.3, 0.3, 0);
	}

	@Override
	public void render(float state)
	{
		RenderHelper.bindTexture(tex);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glTranslated(0, 0, 0);
		GL11.glRotated((-270 * state)-45, 0,1,0);
		model.renderAll();
	}

}
