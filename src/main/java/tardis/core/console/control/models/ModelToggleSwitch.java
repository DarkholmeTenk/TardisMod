package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

public class ModelToggleSwitch extends AbstractControlModel
{
	private final static IModelCustom model = RenderHelper.getModel(new ResourceLocation("tardismod","models/console/toggle.obj"));
	private final static ResourceLocation tex = new ResourceLocation("tardismod","textures/models/console/toggle.png");

	public ModelToggleSwitch()
	{
		super(0.2, 0.2, 0);
	}

	@Override
	public void render(float state)
	{
		RenderHelper.bindTexture(tex);
		GL11.glRotated(180, 1, 0, 0);
		model.renderOnly("Cube");
		GL11.glRotated((45*state) - 22.5f, 0, 0, 1);
		model.renderOnly("Wiggler");
	}

}
