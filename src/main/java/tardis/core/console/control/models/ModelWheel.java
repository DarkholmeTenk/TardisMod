package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.ValveWheelModel;

public class ModelWheel extends AbstractControlModel
{
	private static ValveWheelModel wheel = new ValveWheelModel();

	private static final double regularXSize = 1;
	private static final double regularYSize = 1;

	public static final ModelWheel i = new ModelWheel();
	private ModelWheel()
	{
		super(regularXSize, regularYSize, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float state)
	{
		GL11.glPushMatrix();
		GL11.glRotated(state*360, 0, 1, 0);
		GL11.glTranslated(-0.03125, 0, -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisValveWheel.png"));
		wheel.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
	}
}
