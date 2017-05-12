package tardis.core.console.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.helpers.RenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.ButtonModel;
import tardis.client.renderer.model.console.SonicScrewdriverHolderModel;

public class ControlPushButton extends ControlPush
{
	private static final ButtonModel button = new ButtonModel();
	private static final SonicScrewdriverHolderModel holder = new SonicScrewdriverHolderModel();

	public ControlPushButton(ControlPushButtonBuilder builder, ControlHolder holder)
	{
		super(builder, holder);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptt)
	{
		GL11.glPushMatrix();
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/SonicScrewdriverHolder.png"));
		holder.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(-0.03125, -0.0015 + (getState(ptt) * 0.06), -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/PushLever.png"));
		button.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
	}

	public static class ControlPushButtonBuilder extends ControlPushBuilder<ControlPushButton>
	{
		public ControlPushButtonBuilder(Runnable function)
		{
			super(function);
		}

		@Override
		public ControlPushButton build(ControlHolder holder)
		{
			return new ControlPushButton(this, holder);
		}
	}
}
