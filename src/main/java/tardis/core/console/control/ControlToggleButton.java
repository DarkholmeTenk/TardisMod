package tardis.core.console.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;

import io.darkcraft.darkcore.mod.handlers.containers.PlayerContainer;
import io.darkcraft.darkcore.mod.helpers.RenderHelper;
import io.darkcraft.darkcore.mod.nbt.NBTProperty;
import io.darkcraft.darkcore.mod.nbt.NBTSerialisable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tardis.client.renderer.model.console.ButtonModel;
import tardis.client.renderer.model.console.SonicScrewdriverHolderModel;
import tardis.core.TardisInfo;

@NBTSerialisable
public class ControlToggleButton extends AbstractControl
{
	private static final ButtonModel button = new ButtonModel();
	private static final SonicScrewdriverHolderModel holder = new SonicScrewdriverHolderModel();

	@NBTProperty
	private boolean pressed;

	public ControlToggleButton(ControlToggleButtonBuilder builder, ControlHolder holder)
	{
		super(builder, 0.3, 0.3, 0, holder);
		pressed = builder.defaultPressed;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		pressed = !pressed;
		return true;
	}

	public boolean getPressed()
	{
		return pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
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
		GL11.glTranslated(-0.03125, -0.0015 + (getPressed() ? 0.06 : 0), -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod","textures/models/PushLever.png"));
		button.render(null,0F,0F,0F,0F,0F,0.0625F);
		GL11.glPopMatrix();
	}

	public static class ControlToggleButtonBuilder extends ControlBuilder<ControlToggleButton>
	{
		private boolean defaultPressed;

		public ControlToggleButtonBuilder(boolean defaultPressed)
		{
			this.defaultPressed = defaultPressed;
		}

		@Override
		public ControlToggleButton build(ControlHolder holder)
		{
			return new ControlToggleButton(this, holder);
		}
	}
}
