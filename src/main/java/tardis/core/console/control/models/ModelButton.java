package tardis.core.console.control.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.ButtonModel;
import tardis.client.renderer.model.console.SonicScrewdriverHolderModel;

public class ModelButton extends AbstractControlModel
{
	public static final ModelButton i = new ModelButton();

	private static final ButtonModel button = new ButtonModel();
	private static final SonicScrewdriverHolderModel holder = new SonicScrewdriverHolderModel();

	private ModelButton()
	{
		super(0.25,0.25,0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float state)
	{
		GL11.glPushMatrix();
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(-0.03125, -0.0015 + (state * 0.06), -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/PushLever.png"));
		button.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
	}

}
