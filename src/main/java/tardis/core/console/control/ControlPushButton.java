package tardis.core.console.control;

import static io.darkcraft.darkcore.mod.nbt.NBTProperty.SerialisableType.TRANSMIT;

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
public class ControlPushButton extends AbstractControl
{
	private static final ButtonModel button = new ButtonModel();
	private static final SonicScrewdriverHolderModel holder = new SonicScrewdriverHolderModel();

	private final Runnable function;

	private int pressedTT;
	private boolean wasPressed = false;
	private double state = 0;
	@NBTProperty(TRANSMIT)
	private boolean pressed;

	public ControlPushButton(ControlPushButtonBuilder builder, ControlHolder holder)
	{
		super(builder, 0.25, 0.25, 0, holder);
		function = builder.function;
	}

	@Override
	protected boolean activateControl(TardisInfo info, PlayerContainer player, boolean sneaking)
	{
		function.run();
		pressed = true;
		markDirty();
		return true;
	}

	@Override
	protected void tickControl()
	{
		state = getState(1);
		if(pressed && wasPressed && (tt > (pressedTT + 8)))
			wasPressed = pressed = false;
		else if(pressed && !wasPressed)
		{
			wasPressed = true;
			pressedTT = tt;
		}
	}

	private double getState(float ptt)
	{
		if(pressed && (state < 1))
			return (state + (Math.min(1-state, 0.25) * ptt));
		else if(!pressed && (state > 0))
			return (state - (Math.min(state, 0.25)*ptt));
		return pressed ? 1 : 0;
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

	public static class ControlPushButtonBuilder extends ControlBuilder<ControlPushButton>
	{
		private final Runnable function;

		public ControlPushButtonBuilder(Runnable function)
		{
			this.function = function;
		}

		@Override
		public ControlPushButton build(ControlHolder holder)
		{
			return new ControlPushButton(this, holder);
		}
	}
}
