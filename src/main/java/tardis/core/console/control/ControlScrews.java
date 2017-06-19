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
import tardis.common.items.SonicScrewdriverItem;
import tardis.core.TardisInfo;

@NBTSerialisable
public class ControlScrews extends AbstractControl
{
	private static final ButtonModel	button	= new ButtonModel();

	private double						state;
	@NBTProperty
	private boolean						pressed;

	public ControlScrews(ControlScrewsBuilder builder, ControlHolder holder)
	{
		super(builder, 0.25, 0.25, 0, holder);
		pressed = builder.defaultPressed;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		if(!SonicScrewdriverItem.isPlayerHoldingScrewdriver(player.getEntity()))
			return false;
		pressed = !pressed;
		return true;
	}

	@Override
	protected void tickControlClient()
	{
		state = getState(1);
	}

	public boolean getPressed()
	{
		return pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}

	private double getState(float ptt)
	{
		if (pressed && (state < 1))
			return state + (Math.min(0.2, 1 - state) * ptt);
		else if (!pressed && (state > 0)) return state - (Math.min(0.2, state) * ptt);
		return pressed ? 1 : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float ptt)
	{
		GL11.glPushMatrix();
		GL11.glTranslated(-0.03125, -0.0015 + (getState(ptt) * 0.06), -0.03125);
		RenderHelper.bindTexture(new ResourceLocation("tardismod", "textures/models/PushLever.png"));
		button.render(null, 0F, 0F, 0F, 0F, 0F, 0.0625F);
		GL11.glPopMatrix();
	}

	public static class ControlScrewsBuilder extends ControlBuilder<ControlScrews>
	{
		private boolean defaultPressed;

		public ControlScrewsBuilder(boolean defaultPressed)
		{
			this.defaultPressed = defaultPressed;
		}

		@Override
		public ControlScrews build(ControlHolder holder)
		{
			return new ControlScrews(this, holder);
		}
	}
}
