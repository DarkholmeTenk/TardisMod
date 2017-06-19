package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.LeverBaseModel;
import tardis.client.renderer.model.console.LeverModel;

public class ModelLever extends AbstractControlModel
{
	private static final double regularXSize = 0.8;
	private static final double regularYSize = 0.45;

	private static final LeverModel lever = new LeverModel();
	private static final LeverBaseModel leverBase = new LeverBaseModel();

	public static final ModelLever i = new ModelLever();

	private ModelLever()
	{
		super(regularXSize, regularYSize, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float state)
	{
		GL11.glPushMatrix();
		GL11.glRotated((state*140) - 70, 1, 0, 0);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLever.png"));
		lever.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/TardisConsoleLeverBase.png"));
		leverBase.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glColor3d(1, 1, 1);
		GL11.glPopMatrix();
	}

}
